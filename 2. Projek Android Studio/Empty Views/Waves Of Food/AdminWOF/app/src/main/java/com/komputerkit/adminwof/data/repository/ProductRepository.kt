package com.komputerkit.adminwof.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.komputerkit.adminwof.data.model.ProductModel
import com.komputerkit.adminwof.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository untuk Product CRUD operations
 */
class ProductRepository {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val productsCollection = firestore.collection(Constants.COLLECTION_PRODUCTS)
    
    /**
     * Fetch all products
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
     * Upload image to Firebase Storage
     */
    suspend fun uploadProductImage(imageUri: Uri): Result<String> {
        return try {
            val fileName = "product_${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference
                .child(Constants.STORAGE_PRODUCTS)
                .child(fileName)
            
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete image from Firebase Storage
     */
    suspend fun deleteProductImage(imageUrl: String): Result<Unit> {
        return try {
            if (imageUrl.isNotEmpty()) {
                val storageRef = storage.getReferenceFromUrl(imageUrl)
                storageRef.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            // Ignore error if image doesn't exist
            Result.success(Unit)
        }
    }
    
    /**
     * Create new product
     */
    suspend fun createProduct(product: ProductModel): Result<String> {
        return try {
            val docRef = productsCollection.add(product).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update existing product
     */
    suspend fun updateProduct(productId: String, product: ProductModel): Result<Unit> {
        return try {
            productsCollection.document(productId).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Delete product
     */
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            // First get the product to get image URL
            val productResult = fetchProductById(productId)
            productResult.onSuccess { product ->
                // Delete image from storage
                deleteProductImage(product.imageUrl)
            }
            
            // Delete product document
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
