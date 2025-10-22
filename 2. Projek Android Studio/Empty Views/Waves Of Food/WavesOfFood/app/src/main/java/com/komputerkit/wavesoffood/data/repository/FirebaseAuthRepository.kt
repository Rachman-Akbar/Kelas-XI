package com.komputerkit.wavesoffood.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.wavesoffood.data.model.UserModel
import kotlinx.coroutines.tasks.await

/**
 * Implementation dari AuthRepository menggunakan Firebase Authentication
 */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {
    
    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signUp(email: String, password: String, name: String): Result<FirebaseUser> {
        return try {
            // Create user di Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Update display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdates).await()
                
                // Create user document di Firestore
                val userModel = UserModel(
                    uid = user.uid,
                    email = email,
                    name = name,
                    address = "",
                    cartItems = emptyMap()
                )
                firestore.collection("users")
                    .document(user.uid)
                    .set(userModel)
                    .await()
                
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    override fun signOut() {
        auth.signOut()
    }
    
    override fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
