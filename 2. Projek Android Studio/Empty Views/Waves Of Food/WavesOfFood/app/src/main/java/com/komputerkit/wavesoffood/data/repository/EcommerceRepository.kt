package com.komputerkit.wavesoffood.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.wavesoffood.data.model.OrderModel
import com.komputerkit.wavesoffood.data.model.ProductModel
import com.komputerkit.wavesoffood.data.model.UserModel
import kotlinx.coroutines.tasks.await

/**
 * Repository untuk semua operasi e-commerce (Products, Users, Orders, Cart)
 * Menggunakan Firebase Firestore
 */
class EcommerceRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    
    // ==================== USER OPERATIONS ====================
    
    /**
     * Fetch user data by user ID
     */
    suspend fun fetchUser(userId: String): Result<UserModel> {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val user = snapshot.toObject(UserModel::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update specific field in user document
     */
    suspend fun updateUserField(userId: String, field: String, value: Any): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update(field, value)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update user address
     */
    suspend fun updateUserAddress(userId: String, address: String): Result<Unit> {
        return updateUserField(userId, "address", address)
    }
    
    /**
     * Update entire user document
     */
    suspend fun updateUser(userId: String, user: UserModel): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== PRODUCT OPERATIONS ====================
    
    /**
     * Fetch all products
     */
    suspend fun fetchAllProducts(): Result<List<ProductModel>> {
        return try {
            val snapshot = firestore.collection("products")
                .get()
                .await()
            
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch products by category
     */
    suspend fun fetchProductsByCategory(category: String): Result<List<ProductModel>> {
        return try {
            val snapshot = firestore.collection("products")
                .whereEqualTo("category", category)
                .get()
                .await()
            
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch single product by ID
     */
    suspend fun fetchProductById(productId: String): Result<ProductModel> {
        return try {
            val snapshot = firestore.collection("products")
                .document(productId)
                .get()
                .await()
            
            val product = snapshot.toObject(ProductModel::class.java)?.copy(id = snapshot.id)
            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== CART OPERATIONS ====================
    
    /**
     * Get user's cart items
     */
    suspend fun getCartItems(userId: String): Result<Map<String, Int>> {
        return try {
            val user = fetchUser(userId).getOrThrow()
            Result.success(user.cartItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add item to cart or increase quantity
     */
    suspend fun addToCart(userId: String, productId: String): Result<Unit> {
        return try {
            val user = fetchUser(userId).getOrThrow()
            val currentCart = user.cartItems.toMutableMap()
            
            currentCart[productId] = (currentCart[productId] ?: 0) + 1
            
            updateUserField(userId, "cartItems", currentCart).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove item from cart
     */
    suspend fun removeFromCart(userId: String, productId: String): Result<Unit> {
        return try {
            val user = fetchUser(userId).getOrThrow()
            val currentCart = user.cartItems.toMutableMap()
            
            currentCart.remove(productId)
            
            updateUserField(userId, "cartItems", currentCart).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update quantity of item in cart
     */
    suspend fun updateCartQuantity(userId: String, productId: String, quantity: Int): Result<Unit> {
        return try {
            val user = fetchUser(userId).getOrThrow()
            val currentCart = user.cartItems.toMutableMap()
            
            if (quantity <= 0) {
                currentCart.remove(productId)
            } else {
                currentCart[productId] = quantity
            }
            
            updateUserField(userId, "cartItems", currentCart).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clear entire cart
     */
    suspend fun clearCart(userId: String): Result<Unit> {
        return updateUserField(userId, "cartItems", emptyMap<String, Int>())
    }
    
    // ==================== ORDER OPERATIONS ====================
    
    /**
     * Create new order
     */
    suspend fun createOrder(order: OrderModel): Result<String> {
        return try {
            firestore.collection("orders")
                .document(order.id)
                .set(order)
                .await()
            Result.success(order.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch all orders for specific user
     */
    suspend fun fetchUserOrders(userId: String): Result<List<OrderModel>> {
        return try {
            val snapshot = firestore.collection("orders")
                .whereEqualTo("userID", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(OrderModel::class.java)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch single order by ID
     */
    suspend fun fetchOrderById(orderId: String): Result<OrderModel> {
        return try {
            val snapshot = firestore.collection("orders")
                .document(orderId)
                .get()
                .await()
            
            val order = snapshot.toObject(OrderModel::class.java)
            if (order != null) {
                Result.success(order)
            } else {
                Result.failure(Exception("Order not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update order status
     */
    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("orders")
                .document(orderId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== FAVORITES OPERATIONS ====================
    
    /**
     * Toggle favorite status untuk product
     * @param userId - User ID
     * @param productId - Product ID yang akan ditambah/hapus dari favorit
     * @param isFavorite - true untuk add, false untuk remove
     */
    suspend fun toggleFavorite(userId: String, productId: String, isFavorite: Boolean): Result<Unit> {
        return try {
            val user = fetchUser(userId).getOrThrow()
            val currentFavorites = user.favorites.toMutableList()
            
            if (isFavorite) {
                // Add to favorites jika belum ada
                if (!currentFavorites.contains(productId)) {
                    currentFavorites.add(productId)
                }
            } else {
                // Remove from favorites
                currentFavorites.remove(productId)
            }
            
            updateUserField(userId, "favorites", currentFavorites).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get list of favorite product IDs untuk user
     */
    suspend fun getFavorites(userId: String): Result<List<String>> {
        return try {
            val user = fetchUser(userId).getOrThrow()
            Result.success(user.favorites)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get full product details untuk semua favorites
     */
    suspend fun getFavoriteProducts(userId: String): Result<List<ProductModel>> {
        return try {
            val favoriteIds = getFavorites(userId).getOrThrow()
            
            if (favoriteIds.isEmpty()) {
                return Result.success(emptyList())
            }
            
            // Fetch each favorite product
            val favoriteProducts = mutableListOf<ProductModel>()
            for (productId in favoriteIds) {
                val productResult = fetchProductById(productId)
                productResult.onSuccess { product ->
                    favoriteProducts.add(product)
                }
            }
            
            Result.success(favoriteProducts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check apakah product adalah favorite
     */
    suspend fun isFavorite(userId: String, productId: String): Result<Boolean> {
        return try {
            val user = fetchUser(userId).getOrThrow()
            Result.success(user.favorites.contains(productId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
