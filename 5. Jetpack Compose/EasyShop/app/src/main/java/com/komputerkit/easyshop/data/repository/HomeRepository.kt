package com.komputerkit.easyshop.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.easyshop.data.model.Banner
import com.komputerkit.easyshop.data.model.Category
import com.komputerkit.easyshop.utils.Resource
import kotlinx.coroutines.tasks.await

class HomeRepository(
    private val firestore: FirebaseFirestore
) {
    
    suspend fun getBanners(): Resource<List<Banner>> {
        return try {
            val querySnapshot = firestore.collection("banners")
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            val banners = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Banner::class.java)?.copy(id = document.id)
            }
            
            Resource.Success(banners)
        } catch (e: Exception) {
            // Return empty list instead of error for missing banners
            Resource.Success(emptyList())
        }
    }
    
    suspend fun getCategories(): Resource<List<Category>> {
        return try {
            val querySnapshot = firestore.collection("categories")
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            val categories = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Category::class.java)?.copy(id = document.id)
            }
            
            Resource.Success(categories)
        } catch (e: Exception) {
            Resource.Error("Gagal memuat kategori: ${e.message}")
        }
    }
    
    suspend fun getUserName(userId: String): Resource<String> {
        return try {
            val userDoc = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            val userName = userDoc.getString("name") ?: "User"
            Resource.Success(userName)
        } catch (e: Exception) {
            Resource.Error("Gagal memuat nama pengguna: ${e.message}")
        }
    }
}