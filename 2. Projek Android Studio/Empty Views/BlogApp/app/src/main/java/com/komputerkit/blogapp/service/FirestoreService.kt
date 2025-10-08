package com.komputerkit.blogapp.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.komputerkit.blogapp.model.Blog
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor() {
    
    private val firestore = FirebaseFirestore.getInstance()
    private val blogsCollection = firestore.collection("blogs")
    
    suspend fun addBlogPost(blog: Blog): Result<String> {
        return try {
            val documentRef = blogsCollection.add(blog).await()
            val blogId = documentRef.id
            
            // Update blog dengan ID yang baru dibuat
            blogsCollection.document(blogId).update("id", blogId).await()
            
            Result.success(blogId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getAllBlogs(): Flow<List<Blog>> = callbackFlow {
        val listener = blogsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val blogs = snapshot?.documents?.mapNotNull { document ->
                    try {
                        document.toObject(Blog::class.java)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(blogs)
            }
        
        awaitClose { listener.remove() }
    }
    
    fun getBlogsByAuthor(authorId: String): Flow<List<Blog>> = callbackFlow {
        val listener = blogsCollection
            .whereEqualTo("authorId", authorId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val blogs = snapshot?.documents?.mapNotNull { document ->
                    try {
                        document.toObject(Blog::class.java)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(blogs)
            }
        
        awaitClose { listener.remove() }
    }
    
    suspend fun updateBlogPost(blogId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            blogsCollection.document(blogId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteBlogPost(blogId: String): Result<Unit> {
        return try {
            blogsCollection.document(blogId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBlogById(blogId: String): Result<Blog?> {
        return try {
            val document = blogsCollection.document(blogId).get().await()
            val blog = document.toObject(Blog::class.java)
            Result.success(blog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}