package com.komputerkit.socialmedia.data.manager

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.data.model.User
import kotlinx.coroutines.tasks.await
import java.util.UUID
class AuthManager {
    
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    val currentUser: FirebaseUser?
        get() = auth.currentUser
    
    val isUserLoggedIn: Boolean
        get() = currentUser != null
    
    val currentUserId: String?
        get() = currentUser?.uid
    
    val currentUserEmail: String?
        get() = currentUser?.email
    
    val currentUserDisplayName: String?
        get() = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "User"
    
    /**
     * Register new user with email, password and username
     */
    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        profileImageUri: Uri? = null
    ): Result<User> {
        return try {
            // Check if username already exists
            val usernameExists = checkUsernameExists(username)
            if (usernameExists) {
                return Result.Error(Exception("Username sudah digunakan"))
            }
            
            // Create user with Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to create user")
            
            // Upload profile image if provided
            val profileImageUrl = if (profileImageUri != null) {
                uploadProfileImage(firebaseUser.uid, profileImageUri)
            } else ""
            
            // Create user document in Firestore
            val user = User(
                userId = firebaseUser.uid,
                username = username,
                email = email,
                profileImageUrl = profileImageUrl
            )
            
            // Save user to Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()
            
            Result.Success(user)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Login user with email and password
     */
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Login failed")
            
            // Get user data from Firestore
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java) 
                ?: throw Exception("User data not found")
            
            Result.Success(user)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Get current user data from Firestore
     */
    suspend fun getCurrentUserData(): Result<User> {
        return try {
            val firebaseUser = currentUser ?: throw Exception("User not logged in")
            
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()
            
            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User data not found")
            
            Result.Success(user)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateUserProfile(
        username: String? = null,
        bio: String? = null,
        profileImageUri: Uri? = null
    ): Result<User> {
        return try {
            val firebaseUser = currentUser ?: throw Exception("User not logged in")
            
            // Get current user data
            val currentUserResult = getCurrentUserData()
            if (currentUserResult is Result.Error) {
                return currentUserResult
            }
            
            val currentUserData = (currentUserResult as Result.Success).data
            
            // Check if new username already exists (if username is being changed)
            if (username != null && username != currentUserData.username) {
                val usernameExists = checkUsernameExists(username)
                if (usernameExists) {
                    return Result.Error(Exception("Username sudah digunakan"))
                }
            }
            
            // Upload new profile image if provided
            val newProfileImageUrl = if (profileImageUri != null) {
                uploadProfileImage(firebaseUser.uid, profileImageUri)
            } else currentUserData.profileImageUrl
            
            // Create updated user data
            val updatedUser = currentUserData.copy(
                username = username ?: currentUserData.username,
                bio = bio ?: currentUserData.bio,
                profileImageUrl = newProfileImageUrl
            )
            
            // Update in Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(updatedUser)
                .await()
            
            Result.Success(updatedUser)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Logout current user
     */
    fun logout() {
        auth.signOut()
    }
    
    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    /**
     * Check if username already exists
     */
    private suspend fun checkUsernameExists(username: String): Boolean {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()
            
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Upload profile image to Firebase Storage
     */
    private suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        return try {
            val imageRef = storage.reference
                .child("profile_images")
                .child(userId)
                .child("${UUID.randomUUID()}.jpg")
            
            val uploadTask = imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await().toString()
            
        } catch (e: Exception) {
            throw Exception("Failed to upload profile image: ${e.message}")
        }
    }
    
    /**
     * Delete user account
     */
    suspend fun deleteUserAccount(): Result<Unit> {
        return try {
            val firebaseUser = currentUser ?: throw Exception("User not logged in")
            val userId = firebaseUser.uid
            
            // Delete user data from Firestore
            firestore.collection("users")
                .document(userId)
                .delete()
                .await()
            
            // Delete user posts
            val userPosts = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            userPosts.documents.forEach { doc ->
                doc.reference.delete()
            }
            
            // Delete Firebase Auth user
            firebaseUser.delete().await()
            
            Result.Success(Unit)
            
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}