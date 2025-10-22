package com.komputerkit.wavesoffood.data.repository

import com.google.firebase.auth.FirebaseUser

/**
 * Repository interface untuk Firebase Authentication
 * Mengabstraksi semua operasi auth dari Firebase
 */
interface AuthRepository {
    
    /**
     * Sign in dengan email dan password
     * @return Result<FirebaseUser> - Success dengan user atau Failure dengan exception
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    
    /**
     * Sign up / register user baru
     * @return Result<FirebaseUser> - Success dengan user atau Failure dengan exception
     */
    suspend fun signUp(email: String, password: String, name: String): Result<FirebaseUser>
    
    /**
     * Get current logged in user ID
     * @return String? - User ID atau null jika tidak login
     */
    fun getCurrentUserId(): String?
    
    /**
     * Get current logged in user
     * @return FirebaseUser? - User atau null jika tidak login
     */
    fun getCurrentUser(): FirebaseUser?
    
    /**
     * Sign out user
     */
    fun signOut()
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean
}
