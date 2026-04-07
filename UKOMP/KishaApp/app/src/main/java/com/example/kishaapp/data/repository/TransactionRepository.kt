package com.example.kishaapp.data.repository

import com.example.kishaapp.data.model.Cart
import com.example.kishaapp.data.model.CartWithItems
import com.example.kishaapp.data.model.MarketplaceCartItem
import com.example.kishaapp.data.model.Order
import com.example.kishaapp.data.model.OrderItem
import com.example.kishaapp.data.model.Product
import com.example.kishaapp.data.remote.FirebaseProvider
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TransactionRepository(
    private val firestore: FirebaseFirestore = FirebaseProvider.firestore,
    private val auth: FirebaseAuth = FirebaseProvider.auth
) {

    private val cartsCollection = firestore.collection("carts")
    private val ordersCollection = firestore.collection("orders")

    suspend fun getCart(): Result<CartWithItems> {
        return runCatching {
            val userId = requireUserId()
            val cart = getOrCreateCart(userId)
            val items = cartsCollection.document(cart.id)
                .collection("items")
                .orderBy("productTitle", Query.Direction.ASCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { it.toCartItem() }
            CartWithItems(cart = cart, items = items)
        }
    }

    suspend fun addToCart(product: Product, quantity: Int = 1): Result<Unit> {
        return runCatching {
            require(quantity > 0) { "Quantity harus lebih dari 0" }
            val userId = requireUserId()
            val cart = getOrCreateCart(userId)
            val itemsRef = cartsCollection.document(cart.id).collection("items")

            val existing = itemsRef
                .whereEqualTo("productId", product.id)
                .limit(1)
                .get()
                .await()
                .documents
                .firstOrNull()

            if (existing != null) {
                val currentQty = existing.getLong("quantity")?.toInt() ?: 1
                existing.reference.update(
                    mapOf(
                        "quantity" to (currentQty + quantity),
                        "price" to product.price
                    )
                ).await()
            } else {
                val itemRef = itemsRef.document()
                val payload = mapOf(
                    "cartId" to cart.id,
                    "productId" to product.id,
                    "productTitle" to product.title,
                    "productImageUrl" to product.imageUrl,
                    "quantity" to quantity,
                    "price" to product.price
                )
                itemRef.set(payload).await()
            }

            cartsCollection.document(cart.id).update("updatedAt", Timestamp.now()).await()
            Unit
        }
    }

    suspend fun updateCartItem(itemId: String, quantity: Int): Result<Unit> {
        return runCatching {
            require(quantity > 0) { "Quantity harus lebih dari 0" }
            val cart = getCurrentUserCart()
            val itemRef = cartsCollection.document(cart.id).collection("items").document(itemId)
            val itemSnap = itemRef.get().await()
            require(itemSnap.exists()) { "Item tidak ditemukan" }
            itemRef.update("quantity", quantity).await()
            cartsCollection.document(cart.id).update("updatedAt", Timestamp.now()).await()
            Unit
        }
    }

    suspend fun removeCartItem(itemId: String): Result<Unit> {
        return runCatching {
            val cart = getCurrentUserCart()
            cartsCollection.document(cart.id).collection("items").document(itemId).delete().await()
            cartsCollection.document(cart.id).update("updatedAt", Timestamp.now()).await()
            Unit
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return runCatching {
            val cart = getCurrentUserCart()
            val items = cartsCollection.document(cart.id).collection("items").get().await().documents
            items.forEach { doc -> doc.reference.delete().await() }
            cartsCollection.document(cart.id).update("updatedAt", Timestamp.now()).await()
            Unit
        }
    }

    suspend fun checkout(): Result<Order> {
        return runCatching {
            val userId = requireUserId()
            val cart = getCurrentUserCart()
            val itemsRef = cartsCollection.document(cart.id).collection("items")
            val cartItems = itemsRef.get().await().documents.mapNotNull { it.toCartItem() }

            require(cartItems.isNotEmpty()) { "Keranjang kosong" }

            val totalPrice = cartItems.sumOf { it.subtotal }
            val orderRef = ordersCollection.document()
            val createdAt = Timestamp.now()
            val transactionCode = generateTransactionCode()

            val orderPayload = mapOf(
                "userId" to userId,
                "totalPrice" to totalPrice,
                "status" to "pending",
                "transactionCode" to transactionCode,
                "createdAt" to createdAt
            )
            orderRef.set(orderPayload).await()

            val orderItemsRef = orderRef.collection("items")
            cartItems.forEach { cartItem ->
                val itemRef = orderItemsRef.document()
                itemRef.set(
                    mapOf(
                        "orderId" to orderRef.id,
                        "productId" to cartItem.productId,
                        "productTitle" to cartItem.productTitle,
                        "productImageUrl" to cartItem.productImageUrl,
                        "quantity" to cartItem.quantity,
                        "price" to cartItem.price
                    )
                ).await()
            }

            cartItems.forEach { item ->
                itemsRef.document(item.id).delete().await()
            }
            cartsCollection.document(cart.id).update("updatedAt", Timestamp.now()).await()

            val orderItems = orderItemsRef.get().await().documents.mapNotNull { it.toOrderItem(orderRef.id) }
            Order(
                id = orderRef.id,
                userId = userId,
                totalPrice = totalPrice,
                status = "pending",
                transactionCode = transactionCode,
                createdAt = createdAt,
                items = orderItems
            )
        }
    }

    suspend fun getOrders(): Result<List<Order>> {
        return runCatching {
            val userId = requireUserId()
            val docs = ordersCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents

            docs.mapNotNull { it.toOrderWithoutItems() }
        }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return runCatching {
            val userId = requireUserId()
            val orderSnap = ordersCollection.document(orderId).get().await()
            require(orderSnap.exists()) { "Order tidak ditemukan" }
            val ownerId = orderSnap.getString("userId").orEmpty()
            require(ownerId == userId) { "Anda tidak memiliki akses ke order ini" }

            val items = ordersCollection.document(orderId)
                .collection("items")
                .get()
                .await()
                .documents
                .mapNotNull { it.toOrderItem(orderId) }

            orderSnap.toOrderWithoutItems()?.copy(items = items)
                ?: error("Gagal membaca data order")
        }
    }

    private suspend fun getCurrentUserCart(): Cart {
        val userId = requireUserId()
        return getOrCreateCart(userId)
    }

    private suspend fun getOrCreateCart(userId: String): Cart {
        val existing = cartsCollection
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()

        if (existing != null) {
            return existing.toCart() ?: Cart(
                id = existing.id,
                userId = userId,
                createdAt = existing.getTimestamp("createdAt"),
                updatedAt = existing.getTimestamp("updatedAt")
            )
        }

        val now = Timestamp.now()
        val cartRef = cartsCollection.document()
        cartRef.set(
            mapOf(
                "userId" to userId,
                "createdAt" to now,
                "updatedAt" to now
            )
        ).await()

        return Cart(
            id = cartRef.id,
            userId = userId,
            createdAt = now,
            updatedAt = now
        )
    }

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("Silakan login terlebih dahulu")
    }

    private fun generateTransactionCode(): String {
        val token = UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
        return "TRX-$token"
    }

    private fun DocumentSnapshot.toCart(): Cart? {
        return Cart(
            id = id,
            userId = getString("userId").orEmpty(),
            createdAt = getTimestamp("createdAt"),
            updatedAt = getTimestamp("updatedAt")
        )
    }

    private fun DocumentSnapshot.toCartItem(): MarketplaceCartItem? {
        val productId = getString("productId").orEmpty()
        if (productId.isBlank()) return null

        return MarketplaceCartItem(
            id = id,
            cartId = getString("cartId").orEmpty(),
            productId = productId,
            productTitle = getString("productTitle").orEmpty(),
            productImageUrl = getString("productImageUrl").orEmpty(),
            quantity = getLong("quantity")?.toInt() ?: 1,
            price = getDouble("price") ?: 0.0
        )
    }

    private fun DocumentSnapshot.toOrderWithoutItems(): Order? {
        if (!exists()) return null
        return Order(
            id = id,
            userId = getString("userId").orEmpty(),
            totalPrice = getDouble("totalPrice") ?: 0.0,
            status = getString("status").orEmpty(),
            transactionCode = getString("transactionCode").orEmpty(),
            createdAt = getTimestamp("createdAt"),
            items = emptyList()
        )
    }

    private fun DocumentSnapshot.toOrderItem(orderId: String): OrderItem? {
        val productId = getString("productId").orEmpty()
        if (productId.isBlank()) return null

        return OrderItem(
            id = id,
            orderId = orderId,
            productId = productId,
            productTitle = getString("productTitle").orEmpty(),
            productImageUrl = getString("productImageUrl").orEmpty(),
            quantity = getLong("quantity")?.toInt() ?: 1,
            price = getDouble("price") ?: 0.0
        )
    }
}
