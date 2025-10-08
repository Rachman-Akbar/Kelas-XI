package com.komputerkit.socialmedia.data.manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.socialmedia.data.model.Post
import com.komputerkit.socialmedia.data.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Base64PostManager(private val authManager: AuthManager) {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    companion object {
        private const val POSTS_COLLECTION = "posts"
        private const val MAX_IMAGE_SIZE = 300 // pixels (much smaller for Firestore limits)
        private const val JPEG_QUALITY = 50 // percent (lower quality for smaller size)
        private const val MAX_POSTS_LIMIT = 50 // Limit untuk mengurangi beban Firestore
        private const val MAX_BASE64_SIZE = 400_000 // 400KB limit for Base64 string
    }
    
    fun uploadPost(
        imageStream: InputStream,
        caption: String
    ): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)
            
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.Error(Exception("User not authenticated")))
                return@flow
            }
            
            // Convert image to optimized base64
            val base64Image = convertImageToBase64(imageStream)
            
            // Create post object
            val post = hashMapOf(
                "postId" to "", // Will be set by Firestore
                "userId" to currentUser.uid,
                "username" to (currentUser.displayName ?: "Unknown User"),
                "userProfileImageUrl" to (currentUser.photoUrl?.toString() ?: ""),
                "profileImageUrl" to (currentUser.photoUrl?.toString() ?: ""),
                "imageUrl" to base64Image, // Base64 encoded image (for compatibility)
                "imageBase64" to base64Image, // Base64 encoded image (new field)
                "caption" to caption,
                "likesCount" to 0L,
                "commentsCount" to 0L,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "likedBy" to listOf<String>(),
                "likes" to listOf<String>(),
                "comments" to listOf<String>(),
                "isLiked" to false,
                "timestamp" to System.currentTimeMillis(),
                "isVisible" to true
            )
            
            // Add to Firestore
            val documentRef = firestore.collection(POSTS_COLLECTION).add(post).await()
            
            // Update with document ID
            documentRef.update("postId", documentRef.id).await()
            
            emit(Result.Success(documentRef.id))
            
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    /**
     * Upload post directly with Base64 string (bypasses InputStream issues)
     */
    fun uploadPostWithBase64(
        base64Image: String,
        caption: String
    ): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)
            
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.Error(Exception("User not authenticated")))
                return@flow
            }
            
            // Create post object with Base64 image
            val post = hashMapOf(
                "postId" to "", // Will be set by Firestore
                "userId" to currentUser.uid,
                "username" to (currentUser.displayName ?: "Unknown User"),
                "userProfileImageUrl" to (currentUser.photoUrl?.toString() ?: ""),
                "imageBase64" to base64Image,
                "imageUrl" to "", // Keep for compatibility
                "caption" to caption,
                "likes" to emptyList<String>(),
                "comments" to emptyList<Map<String, Any>>(),
                "likesCount" to 0,
                "commentsCount" to 0,
                "isLiked" to false,
                "timestamp" to System.currentTimeMillis(),
                "isVisible" to true
            )
            
            // Add to Firestore
            val documentRef = firestore.collection(POSTS_COLLECTION).add(post).await()
            
            // Update with document ID
            documentRef.update("postId", documentRef.id).await()
            
            emit(Result.Success(documentRef.id))
            
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
    
    fun uploadStory(
        imageStream: InputStream,
        caption: String
    ): Flow<Result<String>> = flow {
        try {
            emit(Result.Loading)
            
            android.util.Log.d("Base64PostManager", "Starting story upload...")
            
            val currentUser = auth.currentUser
            if (currentUser == null) {
                android.util.Log.e("Base64PostManager", "User not authenticated")
                emit(Result.Error(Exception("User not authenticated")))
                return@flow
            }
            
            // Convert image to optimized base64
            android.util.Log.d("Base64PostManager", "Converting image to Base64...")
            val base64Image = convertImageToBase64(imageStream)
            android.util.Log.d("Base64PostManager", "Base64 image created, length: ${base64Image.length}")
            
            // Check size limit
            if (base64Image.length > MAX_BASE64_SIZE) {
                android.util.Log.e("Base64PostManager", "Base64 image too large: ${base64Image.length}")
                emit(Result.Error(Exception("Image too large after compression (${base64Image.length} chars). Please use a smaller image.")))
                return@flow
            }
            
            // Create story object
            val story = hashMapOf(
                "userId" to currentUser.uid,
                "username" to (currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "Unknown User"),
                "userProfileImageUrl" to (currentUser.photoUrl?.toString() ?: ""),
                "userProfileImageBase64" to "", // Empty for now
                "profileImageUrl" to (currentUser.photoUrl?.toString() ?: ""),
                "imageUrl" to base64Image, // For compatibility
                "imageBase64" to base64Image, // New field
                "text" to "",
                "caption" to caption,
                "backgroundColor" to "#000000",
                "createdAt" to com.google.firebase.Timestamp.now(),
                "expiresAt" to com.google.firebase.Timestamp(com.google.firebase.Timestamp.now().seconds + 86400, 0), // 24 hours
                "viewedBy" to emptyList<String>(),
                "timestamp" to System.currentTimeMillis(),
                "isViewed" to false,
                "isActive" to true
            )
            
            android.util.Log.d("Base64PostManager", "Saving story to Firestore...")
            
            // Add to Firestore
            val documentRef = firestore.collection("stories").add(story).await()
            
            // Update with document ID
            documentRef.update("storyId", documentRef.id).await()
            
            android.util.Log.d("Base64PostManager", "Story uploaded successfully with ID: ${documentRef.id}")
            emit(Result.Success(documentRef.id))
            
        } catch (e: Exception) {
            android.util.Log.e("Base64PostManager", "Error uploading story: ${e.message}")
            emit(Result.Error(Exception("Failed to upload story: ${e.message}")))
        }
    }
    
    private fun convertImageToBase64(imageStream: InputStream): String {
        try {
            // Decode the image with options for memory efficiency
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            
            // First decode to get dimensions
            imageStream.mark(imageStream.available())
            BitmapFactory.decodeStream(imageStream, null, options)
            imageStream.reset()
            
            // Calculate sample size for memory efficiency
            options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
            options.inJustDecodeBounds = false
            
            // Decode with sample size
            val originalBitmap = BitmapFactory.decodeStream(imageStream, null, options)
                ?: throw Exception("Failed to decode image")
            
            // Calculate new dimensions to keep aspect ratio
            val (newWidth, newHeight) = calculateNewDimensions(originalBitmap.width, originalBitmap.height)
            
            // Resize bitmap if needed
            val finalBitmap = if (originalBitmap.width != newWidth || originalBitmap.height != newHeight) {
                val resized = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                originalBitmap.recycle()
                resized
            } else {
                originalBitmap
            }
            
            // Convert to base64 with multiple compression attempts
            var quality = JPEG_QUALITY
            var base64String: String
            var byteArray: ByteArray
            
            do {
                val outputStream = ByteArrayOutputStream()
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                byteArray = outputStream.toByteArray()
                outputStream.close()
                
                base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                
                if (base64String.length > MAX_BASE64_SIZE && quality > 20) {
                    quality -= 10 // Reduce quality and try again
                    android.util.Log.d("Base64PostManager", "Image too large (${base64String.length} chars), reducing quality to $quality%")
                } else {
                    break
                }
            } while (quality >= 20)
            
            // Clean up
            finalBitmap.recycle()
            
            // Final check
            if (base64String.length > MAX_BASE64_SIZE) {
                throw Exception("Image too large even after maximum compression (${base64String.length} chars). Please use a much smaller image.")
            }
            
            android.util.Log.d("Base64PostManager", "Successfully converted image to Base64: ${base64String.length} characters at $quality% quality")
            return base64String
            
        } catch (e: Exception) {
            throw Exception("Failed to convert image to base64: ${e.message}")
        }
    }
    
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    private fun calculateNewDimensions(originalWidth: Int, originalHeight: Int): Pair<Int, Int> {
        if (originalWidth <= MAX_IMAGE_SIZE && originalHeight <= MAX_IMAGE_SIZE) {
            return Pair(originalWidth, originalHeight)
        }
        
        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
        
        return if (originalWidth > originalHeight) {
            // Landscape
            Pair(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / aspectRatio).toInt())
        } else {
            // Portrait
            Pair((MAX_IMAGE_SIZE * aspectRatio).toInt(), MAX_IMAGE_SIZE)
        }
    }
    
    fun getAllPosts(): Flow<Result<List<Post>>> = flow {
        try {
            emit(Result.Loading)
            
            android.util.Log.d("Base64PostManager", "Loading all posts...")
            
            // Use realtime listener for immediate updates
            firestore.collection(POSTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Use timestamp instead of createdAt
                .limit(MAX_POSTS_LIMIT.toLong())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        android.util.Log.e("Base64PostManager", "Error listening to posts: ${error.message}")
                        return@addSnapshotListener
                    }
                    
                    if (snapshot != null) {
                        android.util.Log.d("Base64PostManager", "Firestore returned ${snapshot.documents.size} documents")
                        
                        val posts = snapshot.documents.mapNotNull { document ->
                            try {
                                // Get Base64 image - try both fields for compatibility
                                val imageBase64 = document.getString("imageBase64") ?: document.getString("imageUrl") ?: ""
                                
                                android.util.Log.d("Base64PostManager", "Processing post ${document.id}, imageBase64 length: ${imageBase64.length}")
                                
                                Post(
                                    postId = document.id,
                                    userId = document.getString("userId") ?: "",
                                    username = document.getString("username") ?: "Unknown User",
                                    profileImageUrl = document.getString("userProfileImageUrl") ?: document.getString("profileImageUrl") ?: "",
                                    userProfileImageBase64 = document.getString("userProfileImageBase64") ?: "",
                                    imageUrl = document.getString("imageUrl") ?: "",
                                    imageBase64 = imageBase64,
                                    caption = document.getString("caption") ?: "",
                                    timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                                    createdAt = document.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now(),
                                    likesCount = document.getLong("likesCount") ?: 0L,
                                    commentsCount = document.getLong("commentsCount") ?: 0L,
                                    likes = (document.get("likes") as? List<String>)?.toMutableList() ?: mutableListOf(),
                                    likedBy = (document.get("likedBy") as? List<String>) ?: emptyList(),
                                    comments = (document.get("comments") as? List<String>)?.toMutableList() ?: mutableListOf(),
                                    isLiked = false,
                                    isVisible = document.getBoolean("isVisible") ?: true
                                )
                            } catch (e: Exception) {
                                android.util.Log.e("Base64PostManager", "Error parsing post ${document.id}: ${e.message}")
                                null
                            }
                        }
                        
                        android.util.Log.d("Base64PostManager", "Successfully parsed ${posts.size} posts")
                        // Note: We can't emit from inside a callback in Flow, so we'll use the original approach
                    }
                }
            
            // Fallback to one-time fetch
            val snapshot = firestore.collection(POSTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(MAX_POSTS_LIMIT.toLong())
                .get()
                .await()
            
            android.util.Log.d("Base64PostManager", "Firestore returned ${snapshot.documents.size} documents")
            
            val posts = snapshot.documents.mapNotNull { document ->
                try {
                    // Get Base64 image - try both fields for compatibility
                    val imageBase64 = document.getString("imageBase64") ?: document.getString("imageUrl") ?: ""
                    
                    android.util.Log.d("Base64PostManager", "Processing post ${document.id}, imageBase64 length: ${imageBase64.length}")
                    
                    Post(
                        postId = document.id,
                        userId = document.getString("userId") ?: "",
                        username = document.getString("username") ?: "Unknown User",
                        profileImageUrl = document.getString("userProfileImageUrl") ?: document.getString("profileImageUrl") ?: "",
                        userProfileImageBase64 = document.getString("userProfileImageBase64") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        imageBase64 = imageBase64,
                        caption = document.getString("caption") ?: "",
                        timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                        createdAt = document.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now(),
                        likesCount = document.getLong("likesCount") ?: 0L,
                        commentsCount = document.getLong("commentsCount") ?: 0L,
                        likes = (document.get("likes") as? List<String>)?.toMutableList() ?: mutableListOf(),
                        likedBy = (document.get("likedBy") as? List<String>) ?: emptyList(),
                        comments = (document.get("comments") as? List<String>)?.toMutableList() ?: mutableListOf(),
                        isLiked = false,
                        isVisible = document.getBoolean("isVisible") ?: true
                    )
                } catch (e: Exception) {
                    android.util.Log.e("Base64PostManager", "Error parsing post ${document.id}: ${e.message}")
                    null
                }
            }
            
            android.util.Log.d("Base64PostManager", "Successfully parsed ${posts.size} posts")
            emit(Result.Success(posts))
            
        } catch (e: Exception) {
            android.util.Log.e("Base64PostManager", "Error loading posts: ${e.message}")
            emit(Result.Error(e))
        }
    }
    
    fun likePost(postId: String): Flow<Result<Boolean>> = flow {
        try {
            emit(Result.Loading)
            
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.Error(Exception("Please login first")))
                return@flow
            }
            
            if (postId.isEmpty()) {
                emit(Result.Error(Exception("Invalid post ID")))
                return@flow
            }
            
            val postRef = firestore.collection(POSTS_COLLECTION).document(postId)
            
            val isLiked = firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                
                if (!snapshot.exists()) {
                    throw Exception("Post not found")
                }
                
                val currentLikes = try {
                    snapshot.get("likedBy") as? List<String> ?: emptyList()
                } catch (e: Exception) {
                    emptyList<String>()
                }
                
                val newLikes = if (currentLikes.contains(currentUser.uid)) {
                    // Unlike
                    currentLikes - currentUser.uid
                } else {
                    // Like
                    currentLikes + currentUser.uid
                }
                
                // Update all like-related fields
                transaction.update(postRef, "likedBy", newLikes)
                transaction.update(postRef, "likes", newLikes)
                transaction.update(postRef, "likesCount", newLikes.size.toLong())
                
                newLikes.contains(currentUser.uid)
            }.await()
            
            emit(Result.Success(isLiked))
            
        } catch (e: Exception) {
            emit(Result.Error(Exception("Failed to like post: ${e.message}")))
        }
    }
    
    /**
     * Add comment to a post (Base64 version - Firebase gratis)
     */
    fun addComment(postId: String, commentText: String): Flow<Result<Boolean>> = flow {
        try {
            emit(Result.Loading)
            
            val currentUser = auth.currentUser
            if (currentUser == null) {
                emit(Result.Error(Exception("User not logged in")))
                return@flow
            }
            
            if (postId.isEmpty()) {
                emit(Result.Error(Exception("Invalid post ID")))
                return@flow
            }
            
            if (commentText.trim().isEmpty()) {
                emit(Result.Error(Exception("Comment cannot be empty")))
                return@flow
            }
            
            val postRef = firestore.collection(POSTS_COLLECTION).document(postId)
            
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                
                if (!snapshot.exists()) {
                    throw Exception("Post not found")
                }
                
                val currentComments = try {
                    snapshot.get("comments") as? List<String> ?: emptyList()
                } catch (e: Exception) {
                    emptyList<String>()
                }
                
                // Create simple comment string: "username: comment text"
                val username = currentUser.displayName ?: "Unknown User"
                val commentString = "$username: ${commentText.trim()}"
                
                val newComments = currentComments + commentString
                
                // Update comment-related fields
                transaction.update(postRef, "comments", newComments)
                transaction.update(postRef, "commentsCount", newComments.size.toLong())
                
            }.await()
            
            emit(Result.Success(true))
            
        } catch (e: Exception) {
            emit(Result.Error(Exception("Failed to add comment: ${e.message}")))
        }
    }

    fun getUserPosts(userId: String, callback: (Result<List<Post>>) -> Unit) {
        try {
            if (userId.isEmpty()) {
                android.util.Log.e("Base64PostManager", "User ID is empty")
                callback(Result.Error(Exception("User ID is empty")))
                return
            }

            android.util.Log.d("Base64PostManager", "Loading posts for user: $userId")

            firestore.collection(POSTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(MAX_POSTS_LIMIT.toLong())
                .get()
                .addOnSuccessListener { snapshot ->
                    try {
                        android.util.Log.d("Base64PostManager", "Found ${snapshot.documents.size} posts for user $userId")
                        
                        val posts = snapshot.documents.mapNotNull { doc ->
                            try {
                                // Get Base64 image - try both fields for compatibility
                                val imageBase64 = doc.getString("imageBase64") ?: doc.getString("imageUrl") ?: ""
                                
                                android.util.Log.d("Base64PostManager", "Loading user post: ${doc.id}, imageBase64 length: ${imageBase64.length}")
                                
                                Post(
                                    postId = doc.id,
                                    userId = doc.getString("userId") ?: "",
                                    username = doc.getString("username") ?: "Unknown User",
                                    profileImageUrl = doc.getString("userProfileImageUrl") ?: doc.getString("profileImageUrl") ?: "",
                                    userProfileImageBase64 = doc.getString("userProfileImageBase64") ?: "",
                                    imageUrl = doc.getString("imageUrl") ?: "",
                                    imageBase64 = imageBase64,
                                    caption = doc.getString("caption") ?: "",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    createdAt = doc.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now(),
                                    likesCount = doc.getLong("likesCount") ?: 0L,
                                    commentsCount = doc.getLong("commentsCount") ?: 0L,
                                    likes = (doc.get("likes") as? List<String>)?.toMutableList() ?: mutableListOf(),
                                    likedBy = (doc.get("likedBy") as? List<String>) ?: emptyList(),
                                    comments = (doc.get("comments") as? List<String>)?.toMutableList() ?: mutableListOf(),
                                    isLiked = false,
                                    isVisible = doc.getBoolean("isVisible") ?: true
                                )
                            } catch (e: Exception) {
                                android.util.Log.e("Base64PostManager", "Error parsing post ${doc.id}: ${e.message}")
                                null
                            }
                        }

                        android.util.Log.d("Base64PostManager", "Successfully parsed ${posts.size} posts for user $userId")
                        callback(Result.Success(posts))
                    } catch (e: Exception) {
                        android.util.Log.e("Base64PostManager", "Error parsing user posts: ${e.message}")
                        callback(Result.Error(Exception("Error parsing user posts: ${e.message}")))
                    }
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("Base64PostManager", "Failed to load user posts: ${exception.message}")
                    callback(Result.Error(Exception("Failed to load user posts: ${exception.message}")))
                }
        } catch (e: Exception) {
            android.util.Log.e("Base64PostManager", "Exception in getUserPosts: ${e.message}")
            callback(Result.Error(Exception("Failed to get user posts: ${e.message}")))
        }
    }

    /**
     * Get single post by ID
     */
    fun getPostById(postId: String, callback: (Result<Post?>) -> Unit) {
        try {
            if (postId.isEmpty()) {
                android.util.Log.e("Base64PostManager", "Post ID is empty")
                callback(Result.Error(Exception("Post ID is empty")))
                return
            }

            android.util.Log.d("Base64PostManager", "Loading post by ID: $postId")

            firestore.collection(POSTS_COLLECTION)
                .document(postId)
                .get()
                .addOnSuccessListener { document ->
                    try {
                        if (!document.exists()) {
                            android.util.Log.e("Base64PostManager", "Post not found: $postId")
                            callback(Result.Success(null))
                            return@addOnSuccessListener
                        }

                        android.util.Log.d("Base64PostManager", "Found post: $postId")

                        // Get Base64 image - try both fields for compatibility
                        val imageBase64 = document.getString("imageBase64") ?: document.getString("imageUrl") ?: ""
                        
                        android.util.Log.d("Base64PostManager", "Processing post: $postId, imageBase64 length: ${imageBase64.length}")
                        
                        val post = Post(
                            postId = document.id,
                            userId = document.getString("userId") ?: "",
                            username = document.getString("username") ?: "Unknown User",
                            profileImageUrl = document.getString("userProfileImageUrl") ?: document.getString("profileImageUrl") ?: "",
                            userProfileImageBase64 = document.getString("userProfileImageBase64") ?: "",
                            imageUrl = document.getString("imageUrl") ?: "",
                            imageBase64 = imageBase64,
                            caption = document.getString("caption") ?: "",
                            timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                            createdAt = document.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now(),
                            likesCount = document.getLong("likesCount") ?: 0L,
                            commentsCount = document.getLong("commentsCount") ?: 0L,
                            likes = (document.get("likes") as? List<String>)?.toMutableList() ?: mutableListOf(),
                            likedBy = (document.get("likedBy") as? List<String>) ?: emptyList(),
                            comments = (document.get("comments") as? List<String>)?.toMutableList() ?: mutableListOf(),
                            isLiked = false,
                            isVisible = document.getBoolean("isVisible") ?: true
                        )

                        android.util.Log.d("Base64PostManager", "Successfully loaded post: $postId")
                        callback(Result.Success(post))
                    } catch (e: Exception) {
                        android.util.Log.e("Base64PostManager", "Error parsing post $postId: ${e.message}")
                        callback(Result.Error(Exception("Error parsing post: ${e.message}")))
                    }
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("Base64PostManager", "Failed to load post $postId: ${exception.message}")
                    callback(Result.Error(Exception("Failed to load post: ${exception.message}")))
                }
        } catch (e: Exception) {
            android.util.Log.e("Base64PostManager", "Exception in getPostById: ${e.message}")
            callback(Result.Error(Exception("Failed to get post: ${e.message}")))
        }
    }

    /**
     * Simple method to get all posts directly (for debugging)
     */
    fun getAllPostsSimple(callback: (Result<List<Post>>) -> Unit) {
        try {
            android.util.Log.d("Base64PostManager", "Loading all posts (simple method)...")
            
            firestore.collection(POSTS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(MAX_POSTS_LIMIT.toLong())
                .get()
                .addOnSuccessListener { snapshot ->
                    try {
                        android.util.Log.d("Base64PostManager", "Found ${snapshot.documents.size} total posts")
                        
                        val posts = snapshot.documents.mapNotNull { doc ->
                            try {
                                // Get Base64 image - try both fields for compatibility
                                val imageBase64 = doc.getString("imageBase64") ?: doc.getString("imageUrl") ?: ""
                                
                                android.util.Log.d("Base64PostManager", "Processing post: ${doc.id}, imageBase64 length: ${imageBase64.length}")
                                
                                Post(
                                    postId = doc.id,
                                    userId = doc.getString("userId") ?: "",
                                    username = doc.getString("username") ?: "Unknown User",
                                    profileImageUrl = doc.getString("userProfileImageUrl") ?: doc.getString("profileImageUrl") ?: "",
                                    userProfileImageBase64 = doc.getString("userProfileImageBase64") ?: "",
                                    imageUrl = doc.getString("imageUrl") ?: "",
                                    imageBase64 = imageBase64,
                                    caption = doc.getString("caption") ?: "",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    createdAt = doc.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now(),
                                    likesCount = doc.getLong("likesCount") ?: 0L,
                                    commentsCount = doc.getLong("commentsCount") ?: 0L,
                                    likes = (doc.get("likes") as? List<String>)?.toMutableList() ?: mutableListOf(),
                                    likedBy = (doc.get("likedBy") as? List<String>) ?: emptyList(),
                                    comments = (doc.get("comments") as? List<String>)?.toMutableList() ?: mutableListOf(),
                                    isLiked = false,
                                    isVisible = doc.getBoolean("isVisible") ?: true
                                )
                            } catch (e: Exception) {
                                android.util.Log.e("Base64PostManager", "Error parsing post ${doc.id}: ${e.message}")
                                null
                            }
                        }

                        android.util.Log.d("Base64PostManager", "Successfully parsed ${posts.size} posts")
                        callback(Result.Success(posts))
                    } catch (e: Exception) {
                        android.util.Log.e("Base64PostManager", "Error parsing posts: ${e.message}")
                        callback(Result.Error(Exception("Error parsing posts: ${e.message}")))
                    }
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("Base64PostManager", "Failed to load posts: ${exception.message}")
                    callback(Result.Error(Exception("Failed to load posts: ${exception.message}")))
                }
        } catch (e: Exception) {
            android.util.Log.e("Base64PostManager", "Exception in getAllPostsSimple: ${e.message}")
            callback(Result.Error(Exception("Failed to get posts: ${e.message}")))
        }
    }
}