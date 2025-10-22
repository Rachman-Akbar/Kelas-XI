package com.komputerkit.adminwof.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.komputerkit.adminwof.data.model.OrderModel
import com.komputerkit.adminwof.data.model.ProductModel
import com.komputerkit.adminwof.data.model.UserModel
import com.komputerkit.adminwof.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * AdminRepository - Central repository untuk semua operasi CRUD Admin
 * Menggabungkan Product dan Order management dalam satu repository
 */
class AdminRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    // Collections
    private val productsCollection = firestore.collection(Constants.COLLECTION_PRODUCTS)
    private val ordersCollection = firestore.collection(Constants.COLLECTION_ORDERS)
    private val usersCollection = firestore.collection(Constants.COLLECTION_USERS)
    
    // ==================== PRODUCT OPERATIONS ====================
    
    /**
     * Upload image ke Firebase Storage dan return download URL
     * @param uri - URI gambar dari galeri
     * @param productName - Nama produk untuk filename
     * @return Result dengan download URL atau error
     */
    suspend fun uploadImageAndGetUrl(uri: Uri, productName: String): Result<String> {
        return try {
            // Generate unique filename
            val sanitizedName = productName.replace(" ", "_").lowercase()
            val fileName = "${sanitizedName}_${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child(Constants.STORAGE_PRODUCTS)
                .child(fileName)
            
            // Upload file
            storageRef.putFile(uri).await()
            
            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete image dari Firebase Storage
     * @param imageUrl - URL gambar yang akan dihapus
     * @return Result success atau error
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            if (imageUrl.isNotEmpty()) {
                val storageRef = storage.getReferenceFromUrl(imageUrl)
                storageRef.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // Ignore error jika gambar tidak ada
            Result.success(Unit)
        }
    }
    
    /**
     * Fetch all products dari Firestore
     * @return Result dengan list ProductModel atau error
     */
    suspend fun fetchAllProducts(): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
            }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch product by ID
     * @param productId - ID produk
     * @return Result dengan ProductModel atau error
     */
    suspend fun fetchProductById(productId: String): Result<ProductModel> {
        return try {
            val doc = productsCollection.document(productId).get().await()
            val product = doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
            
            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add new product ke Firestore
     * @param productModel - Data produk baru
     * @return Result dengan product ID atau error
     */
    suspend fun addProduct(productModel: ProductModel): Result<String> {
        return try {
            val docRef = productsCollection.add(productModel).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update existing product di Firestore
     * @param productModel - Data produk yang sudah diupdate (harus include ID)
     * @return Result success atau error
     */
    suspend fun updateProduct(productModel: ProductModel): Result<Unit> {
        return try {
            if (productModel.id.isEmpty()) {
                return Result.failure(Exception("Product ID is required for update"))
            }
            
            productsCollection.document(productModel.id).set(productModel).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete product dari Firestore dan hapus gambarnya dari Storage
     * @param productId - ID produk yang akan dihapus
     * @return Result success atau error
     */
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            // First, fetch product untuk get image URL
            val productResult = fetchProductById(productId)
            productResult.onSuccess { product ->
                // Delete image dari storage
                deleteImage(product.imageUrl)
            }
            
            // Delete product document
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== ORDER OPERATIONS ====================
    
    /**
     * Fetch all orders dari semua users (sorted by date descending)
     * @return Result dengan list OrderModel atau error
     */
    suspend fun fetchAllOrders(): Result<List<OrderModel>> {
        return try {
            val snapshot = ordersCollection
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val orders = snapshot.documents.mapNotNull { doc ->
                doc.toObject(OrderModel::class.java)?.copy(id = doc.id)
            }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch order by ID
     * @param orderId - ID order
     * @return Result dengan OrderModel atau error
     */
    suspend fun fetchOrderById(orderId: String): Result<OrderModel> {
        return try {
            val doc = ordersCollection.document(orderId).get().await()
            val order = doc.toObject(OrderModel::class.java)?.copy(id = doc.id)
            
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
     * Fetch user by ID untuk display di order detail
     * @param userId - ID user
     * @return Result dengan UserModel atau error
     */
    suspend fun fetchUserById(userId: String): Result<UserModel> {
        return try {
            val doc = usersCollection.document(userId).get().await()
            val user = doc.toObject(UserModel::class.java)?.copy(id = doc.id)
            
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
     * Update order status di Firestore
     * @param orderId - ID order yang akan diupdate
     * @param newStatus - Status baru (Ordered, Processing, Shipped, Delivered, Cancelled)
     * @return Result success atau error
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId)
                .update("status", newStatus)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Validate product data sebelum save
     * @param product - ProductModel yang akan divalidasi
     * @return true jika valid, false jika tidak
     */
    fun validateProduct(product: ProductModel): Boolean {
        return product.title.isNotBlank() &&
               product.price > 0 &&
               product.category.isNotBlank()
    }
    
    /**
     * Validate order status
     * @param status - Status yang akan divalidasi
     * @return true jika status valid
     */
    fun validateOrderStatus(status: String): Boolean {
        return Constants.ORDER_STATUSES.contains(status)
    }
}
