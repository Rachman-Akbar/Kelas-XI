package com.komputerkit.socialmedia.data.manager

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.data.model.Story
import com.komputerkit.socialmedia.utils.ImageUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.UUID
class StoryManager(
    private val authManager: AuthManager
) {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    /**
     * Upload new story using base64 (simplified version)
     */
    suspend fun uploadStoryWithBase64(
        base64Image: String,
        caption: String = ""
    ): Result<Story> {
        return try {
            android.util.Log.d("StoryManager", "Starting story upload with Base64 length: ${base64Image.length}")
            
            val currentUser = authManager.currentUser
            if (currentUser == null) {
                android.util.Log.e("StoryManager", "User not logged in")
                return Result.Error(Exception("User not logged in"))
            }
            
            // Check Base64 size limit
            if (base64Image.length > 400_000) { // 400KB limit
                android.util.Log.e("StoryManager", "Base64 image too large: ${base64Image.length} characters")
                return Result.Error(Exception("Image too large (${base64Image.length} chars). Please use a smaller image."))
            }
            
            // Use simple user data from Firebase Auth
            val username = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "Unknown User"
            val profileImageUrl = currentUser.photoUrl?.toString() ?: ""
            
            android.util.Log.d("StoryManager", "Creating story for user: $username")
            
            // Calculate expiry time (24 hours from now)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR_OF_DAY, 24)
            val expiresAt = Timestamp(calendar.time)
            
            // Create story object with simplified data
            val storyData = hashMapOf(
                "userId" to currentUser.uid,
                "username" to username,
                "userProfileImageUrl" to profileImageUrl,
                "userProfileImageBase64" to "", // Empty for now
                "profileImageUrl" to profileImageUrl,
                "imageUrl" to base64Image, // For compatibility
                "imageBase64" to base64Image, // New field
                "text" to caption,
                "caption" to caption,
                "backgroundColor" to "#000000",
                "createdAt" to Timestamp.now(),
                "expiresAt" to expiresAt,
                "timestamp" to System.currentTimeMillis(),
                "viewedBy" to emptyList<String>(),
                "isViewed" to false,
                "isActive" to true
            )
            
            android.util.Log.d("StoryManager", "Saving story to Firestore...")
            
            // Save story to Firestore
            val storyRef = firestore.collection("stories").document()
            storyData["storyId"] = storyRef.id // Add document ID
            
            storyRef.set(storyData).await()
            
            android.util.Log.d("StoryManager", "Story saved successfully with ID: ${storyRef.id}")
            
            // Create Story object for return
            val story = Story(
                storyId = storyRef.id,
                userId = currentUser.uid,
                username = username,
                userProfileImageUrl = profileImageUrl,
                userProfileImageBase64 = "",
                profileImageUrl = profileImageUrl,
                imageUrl = base64Image,
                imageBase64 = base64Image,
                text = caption,
                caption = caption,
                backgroundColor = "#000000",
                createdAt = Timestamp.now(),
                expiresAt = expiresAt,
                timestamp = System.currentTimeMillis(),
                viewedBy = emptyList(),
                isViewed = false,
                isActive = true
            )
            
            Result.Success(story)
            
        } catch (e: Exception) {
            android.util.Log.e("StoryManager", "Error uploading story: ${e.message}")
            Result.Error(Exception("Failed to upload story: ${e.message}"))
        }
    }

    /**
     * Upload new story (legacy method using Firebase Storage)
     */
    suspend fun uploadStoryLegacy(
        imageUri: Uri?,
        text: String = "",
        backgroundColor: String = "#000000",
        userId: String
    ): Result<Story> {
        return try {
            // Get user data
            val userResult = authManager.getCurrentUserData()
            if (userResult is Result.Error) {
                return Result.Error(Exception("User data not found"))
            }
            
            val user = (userResult as Result.Success).data
            
            // Upload image if provided
            val imageUrl = if (imageUri != null) {
                uploadStoryImage(userId, imageUri)
            } else ""
            
            // Calculate expiry time (24 hours from now)
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR_OF_DAY, 24)
            val expiresAt = Timestamp(calendar.time)
            
            // Create story object
            val story = Story(
                userId = userId,
                username = user.username,
                userProfileImageUrl = user.profileImageUrl,
                imageUrl = imageUrl,
                text = text,
                backgroundColor = backgroundColor,
                createdAt = Timestamp.now(),
                expiresAt = expiresAt
            )
            
            // Save story to Firestore
            val storyRef = firestore.collection("stories").document()
            val storyWithId = story.copy(storyId = storyRef.id)
            
            storyRef.set(storyWithId).await()
            
            Result.Success(storyWithId)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get active stories (not expired)
     */
    fun getActiveStories(): Flow<Result<List<Story>>> = flow {
        try {
            emit(Result.Loading)
            
            val now = Timestamp.now()
            
            // Get stories that haven't expired yet
            val storiesSnapshot = firestore.collection("stories")
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", now)
                .orderBy("expiresAt")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val stories = storiesSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Story::class.java)
            }
            
            // Group stories by user, keeping only the most recent story per user for the list
            val groupedStories = stories.groupBy { it.userId }
                .mapValues { (_, userStories) -> 
                    userStories.sortedByDescending { it.createdAt.seconds }
                }
                .values
                .map { it.first() } // Take the most recent story for each user
                .distinctBy { it.userId }
            
            emit(Result.Success(groupedStories))
            
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    /**
     * Get all stories by specific user
     */
    suspend fun getUserStories(userId: String): Result<List<Story>> {
        return try {
            val now = Timestamp.now()
            
            val storiesSnapshot = firestore.collection("stories")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isActive", true)
                .whereGreaterThan("expiresAt", now)
                .orderBy("expiresAt")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .await()
            
            val stories = storiesSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Story::class.java)
            }
            
            Result.Success(stories)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Mark story as viewed by user
     */
    suspend fun markStoryAsViewed(storyId: String, viewerId: String): Result<Unit> {
        return try {
            val storyRef = firestore.collection("stories").document(storyId)
            val storyDoc = storyRef.get().await()
            
            val story = storyDoc.toObject(Story::class.java)
                ?: throw Exception("Story not found")
            
            val currentViewedBy = story.viewedBy.toMutableList()
            
            if (!currentViewedBy.contains(viewerId)) {
                currentViewedBy.add(viewerId)
                
                storyRef.update("viewedBy", currentViewedBy).await()
            }
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Delete story
     */
    suspend fun deleteStory(storyId: String, userId: String): Result<Unit> {
        return try {
            val storyRef = firestore.collection("stories").document(storyId)
            val storyDoc = storyRef.get().await()
            
            val story = storyDoc.toObject(Story::class.java)
                ?: throw Exception("Story not found")
            
            // Check if user owns the story
            if (story.userId != userId) {
                throw Exception("You can only delete your own stories")
            }
            
            // Mark story as inactive instead of deleting (for analytics)
            storyRef.update("isActive", false).await()
            
            // Delete image from Storage if exists
            if (story.imageUrl.isNotEmpty()) {
                try {
                    val imageRef = storage.getReferenceFromUrl(story.imageUrl)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    // Image deletion failed, but continue
                }
            }
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get story viewers
     */
    suspend fun getStoryViewers(storyId: String, ownerId: String): Result<List<String>> {
        return try {
            val storyDoc = firestore.collection("stories")
                .document(storyId)
                .get()
                .await()
            
            val story = storyDoc.toObject(Story::class.java)
                ?: throw Exception("Story not found")
            
            // Check if user owns the story
            if (story.userId != ownerId) {
                throw Exception("You can only view your own story viewers")
            }
            
            Result.Success(story.viewedBy)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Clean up expired stories (should be called periodically)
     */
    suspend fun cleanupExpiredStories(): Result<Unit> {
        return try {
            val now = Timestamp.now()
            
            val expiredStoriesSnapshot = firestore.collection("stories")
                .whereLessThan("expiresAt", now)
                .whereEqualTo("isActive", true)
                .get()
                .await()
            
            val batch = firestore.batch()
            
            expiredStoriesSnapshot.documents.forEach { doc ->
                batch.update(doc.reference, "isActive", false)
            }
            
            batch.commit().await()
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Upload story image to Firebase Storage
     */
    private suspend fun uploadStoryImage(userId: String, imageUri: Uri): String {
        return try {
            val timestamp = System.currentTimeMillis()
            val imageRef = storage.reference
                .child("stories")
                .child(userId)
                .child("$timestamp")
                .child("${UUID.randomUUID()}.jpg")
            
            val uploadTask = imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
            
        } catch (e: Exception) {
            throw Exception("Failed to upload story image: ${e.message}")
        }
    }
}