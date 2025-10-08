package com.komputerkit.easyshop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.easyshop.data.model.Order
import com.komputerkit.easyshop.data.model.OrderItem
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.data.model.ShippingAddress
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.tasks.await

class OrderRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    suspend fun createOrder(
        items: List<OrderItem>,
        shippingAddress: ShippingAddress,
        subtotal: Double,
        tax: Double,
        shippingCost: Double,
        total: Double
    ): Resource<String> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            val currentUser = auth.currentUser ?: return Resource.Error("User tidak ditemukan")
            
            val orderId = firestore.collection("orders").document().id
            
            val order = Order(
                id = orderId,
                userId = userId,
                userEmail = currentUser.email ?: "",
                userName = currentUser.displayName ?: "User",
                items = items,
                shippingAddress = shippingAddress,
                subtotal = subtotal,
                tax = tax,
                shippingCost = shippingCost,
                total = total,
                status = "Ordered",
                paymentStatus = "Pending"
            )
            
            firestore.collection("orders")
                .document(orderId)
                .set(order.toMap())
                .await()
            
            Resource.Success("Pesanan berhasil dibuat dengan ID: $orderId")
        } catch (e: Exception) {
            Resource.Error("Gagal membuat pesanan: ${e.message}")
        }
    }
    
    suspend fun getUserOrders(): Resource<List<Order>> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                try {
                    parseOrderFromDocument(doc.data, doc.id)
                } catch (e: Exception) {
                    android.util.Log.e("OrderRepository", "Error parsing order ${doc.id}: ${e.message}")
                    null
                }
            }.sortedByDescending { it.createdAt } // Sort manually by creation date
            
            android.util.Log.d("OrderRepository", "Retrieved ${orders.size} orders for user $userId")
            Resource.Success(orders)
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error getting user orders: ${e.message}")
            Resource.Error("Gagal mengambil pesanan: ${e.message}")
        }
    }
    
    suspend fun getOrderById(orderId: String): Resource<Order> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            val doc = firestore.collection("orders")
                .document(orderId)
                .get()
                .await()
            
            if (doc.exists()) {
                val order = parseOrderFromDocument(doc.data, doc.id)
                if (order?.userId == userId) {
                    android.util.Log.d("OrderRepository", "Successfully retrieved order: ${order.id}")
                    Resource.Success(order)
                } else {
                    android.util.Log.w("OrderRepository", "Access denied to order $orderId for user $userId")
                    Resource.Error("Anda tidak memiliki akses ke pesanan ini")
                }
            } else {
                android.util.Log.w("OrderRepository", "Order not found: $orderId")
                Resource.Error("Pesanan tidak ditemukan")
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderRepository", "Error getting order by ID: ${e.message}")
            Resource.Error("Gagal mengambil detail pesanan: ${e.message}")
        }
    }
    
    suspend fun updateOrderStatus(orderId: String, status: String): Resource<String> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            firestore.collection("orders")
                .document(orderId)
                .update(
                    mapOf(
                        "status" to status,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            
            Resource.Success("Status pesanan berhasil diperbarui")
        } catch (e: Exception) {
            Resource.Error("Gagal memperbarui status pesanan: ${e.message}")
        }
    }
    
    suspend fun cancelOrder(orderId: String): Resource<String> {
        return updateOrderStatus(orderId, "Cancelled")
    }
}

// Extension function untuk konversi Order ke Map
private fun Order.toMap(): Map<String, Any> {
    return mapOf(
        "id" to id,
        "userId" to userId,
        "userEmail" to userEmail,
        "userName" to userName,
        "items" to items.map { it.toMap() },
        "shippingAddress" to shippingAddress.toMap(),
        "subtotal" to subtotal,
        "tax" to tax,
        "shippingCost" to shippingCost,
        "total" to total,
        "status" to status,
        "paymentMethod" to paymentMethod,
        "paymentStatus" to paymentStatus,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

private fun OrderItem.toMap(): Map<String, Any> {
    return mapOf(
        "productId" to productId,
        "productName" to productName,
        "productPrice" to productPrice,
        "productImageUrl" to productImageUrl,
        "quantity" to quantity,
        "totalPrice" to totalPrice
    )
}

private fun ShippingAddress.toMap(): Map<String, Any> {
    return mapOf(
        "recipientName" to recipientName,
        "fullAddress" to fullAddress,
        "phoneNumber" to phoneNumber,
        "city" to city,
        "postalCode" to postalCode,
        "notes" to notes
    )
}

private fun parseOrderFromDocument(data: Map<String, Any>?, documentId: String): Order? {
    if (data == null) return null
    
    return try {
        // Parse order items
        val itemsList = data["items"] as? List<Map<String, Any>> ?: emptyList()
        val orderItems = itemsList.map { itemData ->
            OrderItem(
                productId = itemData["productId"] as? String ?: "",
                productName = itemData["productName"] as? String ?: "",
                productPrice = (itemData["productPrice"] as? Number)?.toDouble() ?: 0.0,
                productImageUrl = itemData["productImageUrl"] as? String ?: "",
                quantity = (itemData["quantity"] as? Number)?.toInt() ?: 1,
                totalPrice = (itemData["totalPrice"] as? Number)?.toDouble() ?: 0.0
            )
        }
        
        // Parse shipping address
        val shippingData = data["shippingAddress"] as? Map<String, Any> ?: emptyMap()
        val shippingAddress = ShippingAddress(
            recipientName = shippingData["recipientName"] as? String ?: "",
            fullAddress = shippingData["fullAddress"] as? String ?: "",
            phoneNumber = shippingData["phoneNumber"] as? String ?: "",
            city = shippingData["city"] as? String ?: "",
            postalCode = shippingData["postalCode"] as? String ?: "",
            notes = shippingData["notes"] as? String ?: ""
        )
        
        Order(
            id = documentId,
            userId = data["userId"] as? String ?: "",
            userEmail = data["userEmail"] as? String ?: "",
            userName = data["userName"] as? String ?: "",
            items = orderItems,
            shippingAddress = shippingAddress,
            subtotal = (data["subtotal"] as? Number)?.toDouble() ?: 0.0,
            tax = (data["tax"] as? Number)?.toDouble() ?: 0.0,
            shippingCost = (data["shippingCost"] as? Number)?.toDouble() ?: 0.0,
            total = (data["total"] as? Number)?.toDouble() ?: 0.0,
            status = data["status"] as? String ?: "Ordered",
            paymentMethod = data["paymentMethod"] as? String ?: "",
            paymentStatus = data["paymentStatus"] as? String ?: "Pending",
            createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        android.util.Log.e("OrderRepository", "Error parsing order: ${e.message}")
        null
    }
}