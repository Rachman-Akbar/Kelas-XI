package com.komputerkit.earningapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Local authentication fallback for when Firebase is not properly configured
 * This allows testing the registration flow without a real Firebase setup
 */
object LocalAuthManager {
    private const val TAG = "LocalAuthManager"
    private const val PREFS_NAME = "local_auth_prefs"
    private const val KEY_USERS = "registered_users"
    private const val KEY_CURRENT_USER = "current_user"
    
    data class LocalUser(
        val id: String,
        val name: String,
        val email: String,
        val passwordHash: String,
        val createdAt: Long = System.currentTimeMillis()
    )
    
    suspend fun registerUser(context: Context, name: String, email: String, password: String): Result<LocalUser> {
        return try {
            Log.d(TAG, "Starting local registration for: $email")
            
            // Simulate network delay
            delay(1500)
            
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val existingUsers = getRegisteredUsers(prefs)
            
            // Check if email already exists
            if (existingUsers.any { it.email.equals(email, ignoreCase = true) }) {
                Log.w(TAG, "Email already registered: $email")
                return Result.failure(Exception("Email sudah terdaftar. Silakan gunakan email lain."))
            }
            
            // Create new user
            val userId = "local_${UUID.randomUUID()}"
            val passwordHash = hashPassword(password)
            val newUser = LocalUser(
                id = userId,
                name = name,
                email = email,
                passwordHash = passwordHash
            )
            
            // Save user
            val updatedUsers = existingUsers + newUser
            saveUsers(prefs, updatedUsers)
            
            // Set as current user
            setCurrentUser(prefs, newUser)
            
            Log.d(TAG, "Local registration successful for: $email")
            Result.success(newUser)
            
        } catch (e: Exception) {
            Log.e(TAG, "Local registration failed", e)
            Result.failure(e)
        }
    }
    
    suspend fun loginUser(context: Context, email: String, password: String): Result<LocalUser> {
        return try {
            Log.d(TAG, "Starting local login for: $email")
            
            // Simulate network delay
            delay(1000)
            
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val existingUsers = getRegisteredUsers(prefs)
            
            val user = existingUsers.find { it.email.equals(email, ignoreCase = true) }
            
            if (user == null) {
                return Result.failure(Exception("Email tidak terdaftar."))
            }
            
            if (!verifyPassword(password, user.passwordHash)) {
                return Result.failure(Exception("Password salah."))
            }
            
            // Set as current user
            setCurrentUser(prefs, user)
            
            Log.d(TAG, "Local login successful for: $email")
            Result.success(user)
            
        } catch (e: Exception) {
            Log.e(TAG, "Local login failed", e)
            Result.failure(e)
        }
    }
    
    fun getCurrentUser(context: Context): LocalUser? {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val userJson = prefs.getString(KEY_CURRENT_USER, null)
            userJson?.let { parseUser(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user", e)
            null
        }
    }
    
    fun signOut(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().remove(KEY_CURRENT_USER).apply()
            Log.d(TAG, "User signed out")
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out", e)
        }
    }
    
    fun isFirebaseConfigured(): Boolean {
        // Simple check - always return false to use local auth
        return false
    }
    
    private fun getRegisteredUsers(prefs: SharedPreferences): List<LocalUser> {
        return try {
            val usersJson = prefs.getString(KEY_USERS, "[]") ?: "[]"
            parseUsers(usersJson)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing users", e)
            emptyList()
        }
    }
    
    private fun saveUsers(prefs: SharedPreferences, users: List<LocalUser>) {
        val usersJson = serializeUsers(users)
        prefs.edit().putString(KEY_USERS, usersJson).apply()
    }
    
    private fun setCurrentUser(prefs: SharedPreferences, user: LocalUser) {
        val userJson = serializeUser(user)
        prefs.edit().putString(KEY_CURRENT_USER, userJson).apply()
    }
    
    private fun hashPassword(password: String): String {
        // Simple hash - in production use proper bcrypt or similar
        return "hash_${password.hashCode()}_${password.length}"
    }
    
    private fun verifyPassword(password: String, hash: String): Boolean {
        return hashPassword(password) == hash
    }
    
    private fun serializeUser(user: LocalUser): String {
        return "${user.id}|${user.name}|${user.email}|${user.passwordHash}|${user.createdAt}"
    }
    
    private fun parseUser(userString: String): LocalUser? {
        return try {
            val parts = userString.split("|")
            if (parts.size >= 5) {
                LocalUser(
                    id = parts[0],
                    name = parts[1],
                    email = parts[2],
                    passwordHash = parts[3],
                    createdAt = parts[4].toLong()
                )
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing user", e)
            null
        }
    }
    
    private fun serializeUsers(users: List<LocalUser>): String {
        return users.joinToString("\n") { serializeUser(it) }
    }
    
    private fun parseUsers(usersString: String): List<LocalUser> {
        return if (usersString.trim() == "[]" || usersString.isBlank()) {
            emptyList()
        } else {
            usersString.split("\n")
                .mapNotNull { parseUser(it) }
        }
    }
}
