package com.komputerkit.easyshop.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.tasks.await

class ProductRepository(
    private val firestore: FirebaseFirestore
) {
    
    suspend fun getProducts(): Resource<List<Product>> {
        return try {
            // Debug: First try to get all products without any filter
            val querySnapshot = firestore.collection("products")
                .get()
                .await()
            
            val products = querySnapshot.documents.mapNotNull { document ->
                val data = document.data
                android.util.Log.d("ProductRepository", "Document ID: ${document.id}")
                android.util.Log.d("ProductRepository", "Document data: $data")
                
                try {
                    // Manual parsing untuk memastikan tipe data benar
                    Product(
                        id = document.id,
                        name = data?.get("name") as? String ?: "",
                        description = data?.get("description") as? String ?: "",
                        price = (data?.get("price") as? Number)?.toDouble() ?: 0.0,
                        imageUrl = data?.get("imageUrl") as? String ?: "",
                        category = data?.get("category") as? String ?: "",
                        stock = (data?.get("stock") as? Number)?.toInt() ?: 0,
                        rating = (data?.get("rating") as? Number)?.toFloat() ?: 0f,
                        reviewCount = (data?.get("reviewCount") as? Number)?.toInt() ?: 0,
                        isAvailable = data?.get("isAvailable") as? Boolean ?: true,
                        createdAt = (data?.get("createdAt") as? Number)?.toLong() ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepository", "Error parsing product ${document.id}", e)
                    null
                }
            }
            
            android.util.Log.d("ProductRepository", "Total products loaded: ${products.size}")
            
            Resource.Success(products)
        } catch (e: Exception) {
            android.util.Log.e("ProductRepository", "Error loading products", e)
            Resource.Error("Gagal memuat produk: ${e.message}")
        }
    }
    
    suspend fun getProductsByCategory(categoryName: String): Resource<List<Product>> {
        return try {
            val querySnapshot = firestore.collection("products")
                .whereEqualTo("category", categoryName)
                .get()
                .await()
            
            val products = querySnapshot.documents.mapNotNull { document ->
                val data = document.data
                
                try {
                    Product(
                        id = document.id,
                        name = data?.get("name") as? String ?: "",
                        description = data?.get("description") as? String ?: "",
                        price = (data?.get("price") as? Number)?.toDouble() ?: 0.0,
                        imageUrl = data?.get("imageUrl") as? String ?: "",
                        category = data?.get("category") as? String ?: "",
                        stock = (data?.get("stock") as? Number)?.toInt() ?: 0,
                        rating = (data?.get("rating") as? Number)?.toFloat() ?: 0f,
                        reviewCount = (data?.get("reviewCount") as? Number)?.toInt() ?: 0,
                        isAvailable = data?.get("isAvailable") as? Boolean ?: true,
                        createdAt = (data?.get("createdAt") as? Number)?.toLong() ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepository", "Error parsing category product ${document.id}", e)
                    null
                }
            }
            
            Resource.Success(products)
        } catch (e: Exception) {
            android.util.Log.e("ProductRepository", "Error loading category products", e)
            Resource.Error("Gagal memuat produk kategori: ${e.message}")
        }
    }
    
    suspend fun getTopProducts(limit: Long = 10): Resource<List<Product>> {
        return try {
            // Debug: Try without filters first
            val querySnapshot = firestore.collection("products")
                .orderBy("rating", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            
            val products = querySnapshot.documents.mapNotNull { document ->
                val data = document.data
                android.util.Log.d("ProductRepository", "Top product: ${document.id} - ${document.get("name")}")
                
                try {
                    Product(
                        id = document.id,
                        name = data?.get("name") as? String ?: "",
                        description = data?.get("description") as? String ?: "",
                        price = (data?.get("price") as? Number)?.toDouble() ?: 0.0,
                        imageUrl = data?.get("imageUrl") as? String ?: "",
                        category = data?.get("category") as? String ?: "",
                        stock = (data?.get("stock") as? Number)?.toInt() ?: 0,
                        rating = (data?.get("rating") as? Number)?.toFloat() ?: 0f,
                        reviewCount = (data?.get("reviewCount") as? Number)?.toInt() ?: 0,
                        isAvailable = data?.get("isAvailable") as? Boolean ?: true,
                        createdAt = (data?.get("createdAt") as? Number)?.toLong() ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepository", "Error parsing top product ${document.id}", e)
                    null
                }
            }
            
            android.util.Log.d("ProductRepository", "Top products loaded: ${products.size}")
            
            Resource.Success(products)
        } catch (e: Exception) {
            android.util.Log.e("ProductRepository", "Error loading top products", e)
            Resource.Error("Gagal memuat produk terpopuler: ${e.message}")
        }
    }
    
    suspend fun getProductById(productId: String): Resource<Product> {
        return try {
            val document = firestore.collection("products")
                .document(productId)
                .get()
                .await()
            
            val data = document.data
            if (data != null) {
                val product = Product(
                    id = document.id,
                    name = data["name"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    price = (data["price"] as? Number)?.toDouble() ?: 0.0,
                    imageUrl = data["imageUrl"] as? String ?: "",
                    category = data["category"] as? String ?: "",
                    stock = (data["stock"] as? Number)?.toInt() ?: 0,
                    rating = (data["rating"] as? Number)?.toFloat() ?: 0f,
                    reviewCount = (data["reviewCount"] as? Number)?.toInt() ?: 0,
                    isAvailable = data["isAvailable"] as? Boolean ?: true,
                    createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
                )
                Resource.Success(product)
            } else {
                Resource.Error("Produk tidak ditemukan")
            }
        } catch (e: Exception) {
            android.util.Log.e("ProductRepository", "Error loading product by ID", e)
            Resource.Error("Gagal memuat detail produk: ${e.message}")
        }
    }
    
    suspend fun searchProducts(query: String): Resource<List<Product>> {
        return try {
            val querySnapshot = firestore.collection("products")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            
            val products = querySnapshot.documents.mapNotNull { document ->
                val data = document.data
                
                try {
                    Product(
                        id = document.id,
                        name = data?.get("name") as? String ?: "",
                        description = data?.get("description") as? String ?: "",
                        price = (data?.get("price") as? Number)?.toDouble() ?: 0.0,
                        imageUrl = data?.get("imageUrl") as? String ?: "",
                        category = data?.get("category") as? String ?: "",
                        stock = (data?.get("stock") as? Number)?.toInt() ?: 0,
                        rating = (data?.get("rating") as? Number)?.toFloat() ?: 0f,
                        reviewCount = (data?.get("reviewCount") as? Number)?.toInt() ?: 0,
                        isAvailable = data?.get("isAvailable") as? Boolean ?: true,
                        createdAt = (data?.get("createdAt") as? Number)?.toLong() ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    android.util.Log.e("ProductRepository", "Error parsing search product ${document.id}", e)
                    null
                }
            }
            
            Resource.Success(products)
        } catch (e: Exception) {
            android.util.Log.e("ProductRepository", "Error searching products", e)
            Resource.Error("Gagal mencari produk: ${e.message}")
        }
    }
}