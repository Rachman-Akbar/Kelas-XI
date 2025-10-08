package com.komputerkit.blogapp.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor() {
    
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser
    
    val isUserLoggedIn: Boolean
        get() = currentUser != null
    
    suspend fun registerUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let { user ->
                Result.success(user)
            } ?: Result.failure(Exception("Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let { user ->
                Result.success(user)
            } ?: Result.failure(Exception("Login failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logoutUser() {
        firebaseAuth.signOut()
    }
    
    fun getCurrentUserId(): String? = currentUser?.uid
    
    fun getCurrentUserEmail(): String? = currentUser?.email
}