package com.komputerkit.adminwof.data.repository

import com.google.firebase.auth.FirebaseUser

/**
 * Interface untuk Authentication operations
 */
interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    fun isAdminEmail(email: String): Boolean
    fun getCurrentUserId(): String?
    fun signOut()
}
