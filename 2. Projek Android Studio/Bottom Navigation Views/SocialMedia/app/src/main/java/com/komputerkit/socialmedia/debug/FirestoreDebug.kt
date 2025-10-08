package com.komputerkit.socialmedia.debug

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.socialmedia.data.model.Post

class FirestoreDebug {
    
    companion object {
        private const val TAG = "FirestoreDebug"
        
        fun testFirestoreConnection(context: Context) {
            Log.d(TAG, "Testing Firestore connection...")
            
            val firestore = FirebaseFirestore.getInstance()
            
            // Test reading posts collection
            firestore.collection("posts")
                .limit(5)
                .get()
                .addOnSuccessListener { snapshot ->
                    Log.d(TAG, "✅ Firestore connection successful!")
                    Log.d(TAG, "Found ${snapshot.documents.size} documents in posts collection")
                    
                    snapshot.documents.forEach { doc ->
                        val data = doc.data
                        Log.d(TAG, "Document ID: ${doc.id}")
                        Log.d(TAG, "  - username: ${data?.get("username")}")
                        Log.d(TAG, "  - caption: ${data?.get("caption")}")
                        Log.d(TAG, "  - userId: ${data?.get("userId")}")
                        Log.d(TAG, "  - has imageBase64: ${(data?.get("imageBase64") as? String)?.isNotEmpty() == true}")
                        Log.d(TAG, "  - has imageUrl: ${(data?.get("imageUrl") as? String)?.isNotEmpty() == true}")
                        Log.d(TAG, "  - timestamp: ${data?.get("timestamp")}")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Firestore connection failed: ${exception.message}")
                }
                
            // Test writing a simple test document
            val testData = hashMapOf(
                "test" to true,
                "timestamp" to System.currentTimeMillis(),
                "message" to "Test from Android app"
            )
            
            firestore.collection("debug")
                .add(testData)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "✅ Test document written with ID: ${documentReference.id}")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error writing test document: ${exception.message}")
                }
        }
        
        fun createSamplePost(userId: String, username: String) {
            Log.d(TAG, "Creating sample post...")
            
            val firestore = FirebaseFirestore.getInstance()
            
            // Create a simple colored square as Base64 for testing
            val sampleBase64Image = createSampleBase64Image()
            
            val samplePost = hashMapOf(
                "userId" to userId,
                "username" to username,
                "caption" to "Sample post created by debug tool - ${System.currentTimeMillis()}",
                "imageUrl" to sampleBase64Image, // Simple Base64 image for testing
                "imageBase64" to sampleBase64Image, // Same image in both fields
                "timestamp" to System.currentTimeMillis(),
                "createdAt" to com.google.firebase.Timestamp.now(),
                "likesCount" to (0..50).random().toLong(), // Random likes for testing
                "commentsCount" to (0..10).random().toLong(), // Random comments for testing
                "likes" to emptyList<String>(),
                "likedBy" to emptyList<String>(),
                "comments" to listOf("Great post!", "Nice picture!", "Love it!").take((0..3).random()),
                "isVisible" to true
            )
            
            firestore.collection("posts")
                .add(samplePost)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "✅ Sample post created with ID: ${documentReference.id}")
                    
                    // Update with document ID
                    documentReference.update("postId", documentReference.id)
                        .addOnSuccessListener {
                            Log.d(TAG, "✅ Post ID updated successfully")
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "❌ Error updating post ID: ${exception.message}")
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error creating sample post: ${exception.message}")
                }
        }
        
        private fun createSampleBase64Image(): String {
            // Create a simple 100x100 colored square as Base64
            // This is a very small Base64 image for testing
            val colors = listOf(
                "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FECA57", 
                "#FF9FF3", "#54A0FF", "#5F27CD", "#00D2D3", "#FF9F43"
            )
            
            val randomColor = colors.random()
            
            // Simple 1x1 pixel Base64 image (very small for testing)
            // This is a red pixel in Base64 format
            val base64Images = listOf(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAI/hRWkOAAAAABJRU5ErkJggg==", // Red
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==", // Green
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==", // Blue
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP4/5+hHgAFhAJ/wlseKgAAAABJRU5ErkJggg==" // Yellow
            )
            
            return base64Images.random()
        }
    }
}