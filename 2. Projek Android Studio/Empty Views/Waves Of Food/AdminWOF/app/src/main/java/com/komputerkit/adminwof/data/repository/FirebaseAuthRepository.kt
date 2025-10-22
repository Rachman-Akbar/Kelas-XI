package com.komputerkit.adminwof.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.komputerkit.adminwof.utils.Constants
import kotlinx.coroutines.tasks.await

/**
 * Firebase Authentication implementation dengan admin validation
 */
class FirebaseAuthRepository : AuthRepository {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            // Check if email is admin
            if (!isAdminEmail(email)) {
                return Result.failure(Exception("Access denied. Admin privileges required."))
            }
            
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun isAdminEmail(email: String): Boolean {
        return Constants.ADMIN_EMAILS.contains(email.lowercase())
    }
    
    override fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    override fun signOut() {
        auth.signOut()
    }
}
