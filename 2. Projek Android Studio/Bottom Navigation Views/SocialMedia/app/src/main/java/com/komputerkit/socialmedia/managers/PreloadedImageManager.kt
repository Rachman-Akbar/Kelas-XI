package com.komputerkit.socialmedia.managers

import com.komputerkit.socialmedia.models.ImageCategory
import com.komputerkit.socialmedia.models.PreloadedImage
import com.komputerkit.socialmedia.models.StoryTemplate
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class PreloadedImageManager {
    
    private val db = FirebaseFirestore.getInstance()
    
    suspend fun getPreloadedImages(type: String, category: String? = null): List<PreloadedImage> {
        return try {
            var query: Query = db.collection("preloaded_images")
                .whereEqualTo("type", type)
                .whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
            
            if (category != null) {
                query = query.whereEqualTo("category", category)
            }
            
            val snapshot = query.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(PreloadedImage::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch preloaded images: ${e.message}")
        }
    }
    
    suspend fun getImageCategories(): List<ImageCategory> {
        return try {
            val snapshot = db.collection("image_categories")
                .whereEqualTo("isActive", true)
                .orderBy("name")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ImageCategory::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch image categories: ${e.message}")
        }
    }
    
    suspend fun getStoryTemplates(): List<StoryTemplate> {
        return try {
            val snapshot = db.collection("story_templates")
                .whereEqualTo("isActive", true)
                .orderBy("name")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(StoryTemplate::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch story templates: ${e.message}")
        }
    }
    
    suspend fun getPreloadedImageById(imageId: String): PreloadedImage? {
        return try {
            val snapshot = db.collection("preloaded_images")
                .document(imageId)
                .get()
                .await()
            
            snapshot.toObject(PreloadedImage::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getRandomImages(type: String, count: Int = 10): List<PreloadedImage> {
        return try {
            val snapshot = db.collection("preloaded_images")
                .whereEqualTo("type", type)
                .whereEqualTo("isActive", true)
                .limit(count.toLong())
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(PreloadedImage::class.java)?.copy(id = doc.id)
            }.shuffled()
        } catch (e: Exception) {
            throw Exception("Failed to fetch random images: ${e.message}")
        }
    }
    
    suspend fun searchImages(type: String, searchQuery: String): List<PreloadedImage> {
        return try {
            // First get all images of the type
            val snapshot = db.collection("preloaded_images")
                .whereEqualTo("type", type)
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            // Filter on client side for more flexible search
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(PreloadedImage::class.java)?.copy(id = doc.id)
            }.filter { image ->
                image.title.contains(searchQuery, ignoreCase = true) ||
                image.description.contains(searchQuery, ignoreCase = true) ||
                image.category.contains(searchQuery, ignoreCase = true) ||
                image.tags.any { it.contains(searchQuery, ignoreCase = true) }
            }
        } catch (e: Exception) {
            throw Exception("Failed to search images: ${e.message}")
        }
    }
}