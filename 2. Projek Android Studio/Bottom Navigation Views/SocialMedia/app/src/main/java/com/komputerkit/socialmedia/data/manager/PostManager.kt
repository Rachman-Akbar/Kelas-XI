package com.komputerkit.socialmedia.data.manager

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
class PostManager(
    private val authManager: AuthManager
) {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    /**
     * Upload new post with image and caption
     */
    suspend fun uploadPost(
        imageUri: Uri,
        caption: String,
        userId: String
    ): Result<Post> {
        return try {
            // Get user data
            val userResult = authManager.getCurrentUserData()
            if (userResult is Result.Error) {
                return Result.Error(Exception("User data not found"))
            }
            
            val user = (userResult as Result.Success).data
            
            // Upload image to Firebase Storage
            val imageUrl = uploadPostImage(userId, imageUri)
            
            // Create post object
            val post = Post(
                userId = userId,
                username = user.username,
                userProfileImageUrl = user.profileImageUrl,
                imageUrl = imageUrl,
                caption = caption,
                createdAt = Timestamp.now()
            )
            
            // Save post to Firestore
            val postRef = firestore.collection("posts").document()
            val postWithId = post.copy(postId = postRef.id)
            
            postRef.set(postWithId).await()
            
            // Update user's posts count
            updateUserPostsCount(userId, 1)
            
            Result.Success(postWithId)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get posts for home feed (following users + own posts)
     */
    fun getHomeFeedPosts(userId: String, limit: Long = 20): Flow<Result<List<Post>>> = flow {
        try {
            emit(Result.Loading)
            
            // Get posts ordered by creation time (newest first)
            val postsSnapshot = firestore.collection("posts")
                .whereEqualTo("isVisible", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            
            val posts = postsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
            
            emit(Result.Success(posts))
            
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    /**
     * Get posts by specific user
     */
    suspend fun getUserPosts(userId: String): Result<List<Post>> {
        return try {
            val postsSnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .whereEqualTo("isVisible", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val posts = postsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
            
            Result.Success(posts)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Like or unlike a post
     */
    suspend fun toggleLikePost(postId: String, userId: String): Result<Boolean> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            val postDoc = postRef.get().await()
            
            val post = postDoc.toObject(Post::class.java)
                ?: throw Exception("Post not found")
            
            val currentLikedBy = post.likedBy.toMutableList()
            val isLiked = currentLikedBy.contains(userId)
            
            if (isLiked) {
                // Unlike post
                currentLikedBy.remove(userId)
            } else {
                // Like post
                currentLikedBy.add(userId)
            }
            
            // Update post in Firestore
            postRef.update(
                mapOf(
                    "likedBy" to currentLikedBy,
                    "likesCount" to currentLikedBy.size.toLong()
                )
            ).await()
            
            Result.Success(!isLiked) // Return new like status
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get single post by ID
     */
    suspend fun getPostById(postId: String): Result<Post> {
        return try {
            val postDoc = firestore.collection("posts")
                .document(postId)
                .get()
                .await()
            
            val post = postDoc.toObject(Post::class.java)
                ?: throw Exception("Post not found")
            
            Result.Success(post)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Delete post
     */
    suspend fun deletePost(postId: String, userId: String): Result<Unit> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            val postDoc = postRef.get().await()
            
            val post = postDoc.toObject(Post::class.java)
                ?: throw Exception("Post not found")
            
            // Check if user owns the post
            if (post.userId != userId) {
                throw Exception("You can only delete your own posts")
            }
            
            // Delete post from Firestore
            postRef.delete().await()
            
            // Delete image from Storage
            try {
                val imageRef = storage.getReferenceFromUrl(post.imageUrl)
                imageRef.delete().await()
            } catch (e: Exception) {
                // Image deletion failed, but continue
            }
            
            // Update user's posts count
            updateUserPostsCount(userId, -1)
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Update post caption
     */
    suspend fun updatePostCaption(postId: String, newCaption: String, userId: String): Result<Post> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            val postDoc = postRef.get().await()
            
            val post = postDoc.toObject(Post::class.java)
                ?: throw Exception("Post not found")
            
            // Check if user owns the post
            if (post.userId != userId) {
                throw Exception("You can only edit your own posts")
            }
            
            // Update caption
            postRef.update("caption", newCaption).await()
            
            val updatedPost = post.copy(caption = newCaption)
            Result.Success(updatedPost)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Toggle like on a post using Firestore Transaction to prevent race conditions
     */
    suspend fun toggleLike(postId: String, userId: String): Result<Boolean> {
        return try {
            val postRef = firestore.collection("posts").document(postId)
            
            firestore.runTransaction { transaction ->
                val postSnapshot = transaction.get(postRef)
                val post = postSnapshot.toObject(Post::class.java)
                    ?: throw Exception("Post not found")
                
                val currentLikes = post.likes.toMutableList()
                val isLiked = currentLikes.contains(userId)
                
                if (isLiked) {
                    // Unlike: remove user from likes
                    currentLikes.remove(userId)
                } else {
                    // Like: add user to likes
                    currentLikes.add(userId)
                }
                
                // Update the post with new likes list
                transaction.update(postRef, "likes", currentLikes)
                transaction.update(postRef, "likesCount", currentLikes.size)
                
                !isLiked // Return new like status
            }.await()
            
            Result.Success(true)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Add comment to a post (simplified version)
     */
    suspend fun addComment(postId: String, userId: String, commentText: String): Result<Unit> {
        return try {
            val userResult = authManager.getCurrentUserData()
            if (userResult is Result.Error) {
                return Result.Error(Exception("User data not found"))
            }
            
            val user = (userResult as Result.Success).data
            
            // Create simple comment string: "username: comment text"
            val commentString = "${user.username}: $commentText"
            
            val postRef = firestore.collection("posts").document(postId)
            
            firestore.runTransaction { transaction ->
                val postSnapshot = transaction.get(postRef)
                val post = postSnapshot.toObject(Post::class.java)
                    ?: throw Exception("Post not found")
                
                val currentComments = post.comments.toMutableList()
                currentComments.add(commentString)
                
                transaction.update(postRef, "comments", currentComments)
                transaction.update(postRef, "commentsCount", currentComments.size)
            }.await()
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Search posts by caption or username
     */
    suspend fun searchPosts(query: String): Result<List<Post>> {
        return try {
            // This is a simple search - in production, you might want to use Algolia or similar
            val postsSnapshot = firestore.collection("posts")
                .whereEqualTo("isVisible", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .await()
            
            val allPosts = postsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
            
            // Filter posts that contain the query in caption or username
            val filteredPosts = allPosts.filter { post ->
                post.caption.contains(query, ignoreCase = true) ||
                post.username.contains(query, ignoreCase = true)
            }
            
            Result.Success(filteredPosts)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get posts with pagination
     */
    suspend fun getPostsWithPagination(
        lastPostTimestamp: Timestamp? = null,
        limit: Long = 20
    ): Result<List<Post>> {
        return try {
            var query = firestore.collection("posts")
                .whereEqualTo("isVisible", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
            
            // Add pagination if lastPostTimestamp is provided
            if (lastPostTimestamp != null) {
                query = query.startAfter(lastPostTimestamp)
            }
            
            val postsSnapshot = query.get().await()
            
            val posts = postsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
            
            Result.Success(posts)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Upload post image to Firebase Storage
     */
    private suspend fun uploadPostImage(userId: String, imageUri: Uri): String {
        return try {
            val timestamp = System.currentTimeMillis()
            val imageRef = storage.reference
                .child("posts")
                .child(userId)
                .child("$timestamp")
                .child("${UUID.randomUUID()}.jpg")
            
            val uploadTask = imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
            
        } catch (e: Exception) {
            throw Exception("Failed to upload post image: ${e.message}")
        }
    }
    
    /**
     * Update user's posts count
     */
    private suspend fun updateUserPostsCount(userId: String, increment: Long) {
        try {
            val userRef = firestore.collection("users").document(userId)
            firestore.runTransaction { transaction ->
                val userDoc = transaction.get(userRef)
                val user = userDoc.toObject(User::class.java)
                
                if (user != null) {
                    val newCount = maxOf(0, user.postsCount + increment)
                    transaction.update(userRef, "postsCount", newCount)
                }
            }.await()
        } catch (e: Exception) {
            // Don't throw error if count update fails
        }
    }
    
    /**
     * Report post
     */
    suspend fun reportPost(postId: String, userId: String, reason: String): Result<Unit> {
        return try {
            val reportData = mapOf(
                "postId" to postId,
                "reportedBy" to userId,
                "reason" to reason,
                "reportedAt" to Timestamp.now(),
                "status" to "pending"
            )
            
            firestore.collection("reports")
                .document()
                .set(reportData)
                .await()
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}