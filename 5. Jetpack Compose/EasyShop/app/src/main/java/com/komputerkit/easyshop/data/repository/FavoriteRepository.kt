package com.komputerkit.easyshop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.data.model.User
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun addToFavorites(productId: String): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")
            
            firestore.collection("users")
                .document(userId)
                .update("favorites", FieldValue.arrayUnion(productId))
                .await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add to favorites")
        }
    }
    
    suspend fun removeFromFavorites(productId: String): Resource<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")
            
            firestore.collection("users")
                .document(userId)
                .update("favorites", FieldValue.arrayRemove(productId))
                .await()
            
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove from favorites")
        }
    }
    
    suspend fun getFavoriteProducts(): Resource<List<Product>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")
            
            // Get user's favorite product IDs
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java)
            val favoriteIds = user?.favorites ?: emptyList()
            
            if (favoriteIds.isEmpty()) {
                return Resource.Success(emptyList())
            }
            
            // Get favorite products details
            val favoriteProducts = mutableListOf<Product>()
            
            for (productId in favoriteIds) {
                val productDoc = firestore.collection("products")
                    .document(productId)
                    .get()
                    .await()
                
                productDoc.toObject(Product::class.java)?.let { product ->
                    favoriteProducts.add(product.copy(id = productDoc.id))
                }
            }
            
            Resource.Success(favoriteProducts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to load favorites")
        }
    }
    
    suspend fun isProductFavorite(productId: String): Resource<Boolean> {
        return try {
            val userId = auth.currentUser?.uid ?: return Resource.Error("User not authenticated")
            
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java)
            val isFavorite = user?.favorites?.contains(productId) ?: false
            
            Resource.Success(isFavorite)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to check favorite status")
        }
    }
}