package com.komputerkit.easyshop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.easyshop.data.model.CartItem
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.tasks.await

class CartRepository(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    private fun getCurrentUserId(): String? = auth.currentUser?.uid
    
    suspend fun addToCart(product: Product, quantity: Int = 1): Resource<String> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            val cartRef = firestore.collection("carts").document(userId)
            val cartDoc = cartRef.get().await()
            
            val cartItem = CartItem(
                productId = product.id,
                productName = product.name,
                productPrice = product.price,
                productImageUrl = product.imageUrl,
                quantity = quantity,
                userId = userId
            )
            
            if (cartDoc.exists()) {
                // Check if product already exists in cart
                val existingItems = cartDoc.get("items") as? List<Map<String, Any>> ?: emptyList()
                val existingItemIndex = existingItems.indexOfFirst { 
                    it["productId"] == product.id 
                }
                
                if (existingItemIndex != -1) {
                    // Update quantity of existing item
                    val updatedItems = existingItems.toMutableList()
                    val existingItem = existingItems[existingItemIndex].toMutableMap()
                    val currentQuantity = (existingItem["quantity"] as? Long)?.toInt() ?: 1
                    existingItem["quantity"] = currentQuantity + quantity
                    existingItem["addedAt"] = System.currentTimeMillis()
                    updatedItems[existingItemIndex] = existingItem
                    
                    cartRef.update("items", updatedItems).await()
                } else {
                    // Add new item to cart
                    cartRef.update("items", FieldValue.arrayUnion(cartItemToMap(cartItem))).await()
                }
            } else {
                // Create new cart
                val cartData = mapOf(
                    "userId" to userId,
                    "items" to listOf(cartItemToMap(cartItem)),
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )
                cartRef.set(cartData).await()
            }
            
            Resource.Success("Produk berhasil ditambahkan ke keranjang")
        } catch (e: Exception) {
            Resource.Error("Gagal menambahkan ke keranjang: ${e.message}")
        }
    }
    
    suspend fun getCartItems(): Resource<List<CartItem>> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            val cartDoc = firestore.collection("carts")
                .document(userId)
                .get()
                .await()
            
            if (cartDoc.exists()) {
                val itemsData = cartDoc.get("items") as? List<Map<String, Any>> ?: emptyList()
                val cartItems = itemsData.map { mapToCartItem(it) }
                Resource.Success(cartItems)
            } else {
                Resource.Success(emptyList())
            }
        } catch (e: Exception) {
            Resource.Error("Gagal memuat keranjang: ${e.message}")
        }
    }
    
    suspend fun updateCartItemQuantity(productId: String, newQuantity: Int): Resource<String> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            val cartRef = firestore.collection("carts").document(userId)
            val cartDoc = cartRef.get().await()
            
            if (cartDoc.exists()) {
                val existingItems = cartDoc.get("items") as? List<Map<String, Any>> ?: emptyList()
                val updatedItems = existingItems.map { item ->
                    if (item["productId"] == productId) {
                        item.toMutableMap().apply {
                            this["quantity"] = newQuantity
                            this["addedAt"] = System.currentTimeMillis()
                        }
                    } else {
                        item
                    }
                }
                
                cartRef.update("items", updatedItems).await()
                Resource.Success("Kuantitas berhasil diupdate")
            } else {
                Resource.Error("Keranjang tidak ditemukan")
            }
        } catch (e: Exception) {
            Resource.Error("Gagal mengupdate kuantitas: ${e.message}")
        }
    }
    
    suspend fun removeFromCart(productId: String): Resource<String> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            val cartRef = firestore.collection("carts").document(userId)
            val cartDoc = cartRef.get().await()
            
            if (cartDoc.exists()) {
                val existingItems = cartDoc.get("items") as? List<Map<String, Any>> ?: emptyList()
                val updatedItems = existingItems.filter { item ->
                    item["productId"] != productId
                }
                
                cartRef.update("items", updatedItems).await()
                Resource.Success("Produk berhasil dihapus dari keranjang")
            } else {
                Resource.Error("Keranjang tidak ditemukan")
            }
        } catch (e: Exception) {
            Resource.Error("Gagal menghapus dari keranjang: ${e.message}")
        }
    }
    
    suspend fun clearCart(): Resource<String> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("User tidak terautentikasi")
            
            firestore.collection("carts")
                .document(userId)
                .update("items", emptyList<Map<String, Any>>())
                .await()
            
            Resource.Success("Keranjang berhasil dikosongkan")
        } catch (e: Exception) {
            Resource.Error("Gagal mengosongkan keranjang: ${e.message}")
        }
    }
    
    suspend fun getCartItemCount(): Resource<Int> {
        return try {
            val cartItems = getCartItems()
            when (cartItems) {
                is Resource.Success -> {
                    val totalCount = cartItems.data?.sumOf { it.quantity } ?: 0
                    Resource.Success(totalCount)
                }
                is Resource.Error -> Resource.Error(cartItems.message ?: "Gagal menghitung item")
                is Resource.Loading -> Resource.Loading()
            }
        } catch (e: Exception) {
            Resource.Error("Gagal menghitung item keranjang: ${e.message}")
        }
    }
    
    private fun cartItemToMap(cartItem: CartItem): Map<String, Any> {
        return mapOf(
            "productId" to cartItem.productId,
            "productName" to cartItem.productName,
            "productPrice" to cartItem.productPrice,
            "productImageUrl" to cartItem.productImageUrl,
            "quantity" to cartItem.quantity,
            "userId" to cartItem.userId,
            "addedAt" to cartItem.addedAt
        )
    }
    
    private fun mapToCartItem(map: Map<String, Any>): CartItem {
        return CartItem(
            productId = map["productId"] as? String ?: "",
            productName = map["productName"] as? String ?: "",
            productPrice = (map["productPrice"] as? Number)?.toDouble() ?: 0.0,
            productImageUrl = map["productImageUrl"] as? String ?: "",
            quantity = (map["quantity"] as? Number)?.toInt() ?: 1,
            userId = map["userId"] as? String ?: "",
            addedAt = (map["addedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
        )
    }
}