package com.komputerkit.earningapp.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.data.model.AuthUser
import com.komputerkit.earningapp.data.model.UserProfile
import com.komputerkit.earningapp.utils.Resource
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    
    companion object {
        private const val TAG = "AuthRepository"
        private const val USERS_COLLECTION = "users"
        
        /**
         * Initialize repository with application context
         */
        fun initialize(context: android.content.Context) {
            Log.d(TAG, "AuthRepository initialized with context")
        }
    }

    /**
     * Sign in with email and password
     */
    suspend fun signIn(email: String, password: String): Resource<AuthUser> {
        return try {
            Log.d(TAG, "Attempting sign in for email: $email")
            
            // Check Firebase configuration before attempting signin
            if (!isFirebaseConfigured()) {
                Log.e(TAG, "Firebase is not properly configured")
                return Resource.Error("Konfigurasi Firebase bermasalah. Silakan hubungi administrator.")
            }
            
            // Additional safety check for auth instance
            if (auth == null) {
                Log.e(TAG, "FirebaseAuth instance is null")
                return Resource.Error("Layanan autentikasi tidak tersedia. Silakan restart aplikasi.")
            }
            
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                Log.d(TAG, "Sign in successful for user: ${firebaseUser.uid}")
                val authUser = AuthUser.fromFirebaseUser(firebaseUser)
                
                if (authUser != null) {
                    // Update user data in Firestore if needed (with error handling)
                    try {
                        updateUserInFirestore(authUser)
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to update user in Firestore during login", e)
                        // Continue with login even if Firestore update fails
                    }
                    
                    Resource.Success(authUser)
                } else {
                    Log.e(TAG, "Failed to create AuthUser from FirebaseUser")
                    Resource.Error("Login gagal: Error dalam pemrosesan data user")
                }
            } else {
                Log.e(TAG, "Sign in failed: Firebase user is null")
                Resource.Error("Login gagal: User tidak ditemukan")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException during sign in", e)
            Resource.Error("Terjadi masalah keamanan. Silakan restart aplikasi dan coba lagi.")
        } catch (e: com.google.firebase.FirebaseException) {
            Log.e(TAG, "Firebase exception during sign in", e)
            val errorMessage = when {
                e.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true -> 
                    "Konfigurasi Firebase tidak ditemukan. Silakan periksa pengaturan aplikasi."
                e.message?.contains("user-not-found", ignoreCase = true) == true -> 
                    "Email tidak terdaftar"
                e.message?.contains("wrong-password", ignoreCase = true) == true -> 
                    "Password salah"
                e.message?.contains("invalid-email", ignoreCase = true) == true -> 
                    "Format email tidak valid"
                e.message?.contains("user-disabled", ignoreCase = true) == true -> 
                    "Akun telah dinonaktifkan"
                e.message?.contains("network", ignoreCase = true) == true -> 
                    "Tidak ada koneksi internet"
                e.message?.contains("internal error", ignoreCase = true) == true -> 
                    "Terjadi kesalahan internal. Silakan coba lagi nanti."
                else -> "Login gagal: ${e.localizedMessage ?: e.message}"
            }
            Resource.Error(errorMessage)
        } catch (e: Exception) {
            Log.e(TAG, "General exception during sign in", e)
            val errorMessage = when {
                e.message?.contains("network", ignoreCase = true) == true -> 
                    "Tidak ada koneksi internet"
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "Koneksi timeout. Silakan coba lagi."
                else -> "Login gagal: ${e.localizedMessage ?: "Terjadi kesalahan tidak terduga"}"
            }
            Resource.Error(errorMessage)
        }
    }

    /**
     * Sign up with email, password, and display name
     */
    suspend fun signUp(email: String, password: String, name: String): Resource<AuthUser> {
        return try {
            Log.d(TAG, "Attempting sign up for email: $email")
            
            // Check Firebase configuration before attempting signup
            if (!isFirebaseConfigured()) {
                Log.e(TAG, "Firebase is not properly configured")
                return Resource.Error("Konfigurasi Firebase bermasalah. Silakan hubungi administrator.")
            }
            
            // Additional safety check for auth instance
            if (auth == null) {
                Log.e(TAG, "FirebaseAuth instance is null")
                return Resource.Error("Layanan autentikasi tidak tersedia. Silakan restart aplikasi.")
            }
            
            // Create user with Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser != null) {
                Log.d(TAG, "User created successfully: ${firebaseUser.uid}")
                
                // Update the user's display name
                try {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    
                    firebaseUser.updateProfile(profileUpdates).await()
                    Log.d(TAG, "Profile updated with display name: $name")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to update profile display name", e)
                    // Continue with registration even if profile update fails
                }
                
                // Create AuthUser object
                val authUser = AuthUser(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = name,
                    isLocal = false
                )
                
                // Save user data to Firestore (with error handling)
                try {
                    saveUserToFirestore(authUser)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to save user to Firestore during registration", e)
                    // Continue with registration even if Firestore save fails
                }
                
                Log.d(TAG, "Sign up complete for user: ${firebaseUser.uid}")
                Resource.Success(authUser)
            } else {
                Log.e(TAG, "Sign up failed: Firebase user is null")
                Resource.Error("Registrasi gagal: User tidak dapat dibuat")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException during sign up", e)
            Resource.Error("Terjadi masalah keamanan. Silakan restart aplikasi dan coba lagi.")
        } catch (e: com.google.firebase.FirebaseException) {
            Log.e(TAG, "Firebase exception during sign up", e)
            val errorMessage = when {
                e.message?.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) == true -> 
                    "Konfigurasi Firebase tidak ditemukan. Silakan periksa pengaturan aplikasi."
                e.message?.contains("email-already-in-use", ignoreCase = true) == true -> 
                    "Email sudah terdaftar"
                e.message?.contains("invalid-email", ignoreCase = true) == true -> 
                    "Format email tidak valid"
                e.message?.contains("weak-password", ignoreCase = true) == true -> 
                    "Password terlalu lemah (minimal 6 karakter)"
                e.message?.contains("operation-not-allowed", ignoreCase = true) == true -> 
                    "Pendaftaran tidak diizinkan"
                e.message?.contains("network", ignoreCase = true) == true -> 
                    "Tidak ada koneksi internet"
                e.message?.contains("internal error", ignoreCase = true) == true -> 
                    "Terjadi kesalahan internal. Silakan coba lagi nanti."
                else -> "Registrasi gagal: ${e.localizedMessage ?: e.message}"
            }
            Resource.Error(errorMessage)
        } catch (e: Exception) {
            Log.e(TAG, "General exception during sign up", e)
            val errorMessage = when {
                e.message?.contains("network", ignoreCase = true) == true -> 
                    "Tidak ada koneksi internet"
                e.message?.contains("timeout", ignoreCase = true) == true -> 
                    "Koneksi timeout. Silakan coba lagi."
                else -> "Registrasi gagal: ${e.localizedMessage ?: "Terjadi kesalahan tidak terduga"}"
            }
            Resource.Error(errorMessage)
        }
    }

    /**
     * Check if Firebase is properly configured
     */
    private fun isFirebaseConfigured(): Boolean {
        return try {
            // Check if Firebase app is initialized
            val app = com.google.firebase.FirebaseApp.getInstance()
            val options = app.options
            
            // Check if essential configurations are present and not placeholders
            val projectId = options.projectId
            val apiKey = options.apiKey
            val appId = options.applicationId
            
            Log.d(TAG, "Firebase config check - Project ID: $projectId")
            Log.d(TAG, "Firebase config check - App ID: $appId")
            
            // Validate that configurations are not placeholders or empty
            val isValid = !projectId.isNullOrEmpty() && 
                         !apiKey.isNullOrEmpty() && 
                         !appId.isNullOrEmpty() &&
                         !projectId.contains("placeholder", ignoreCase = true) &&
                         !apiKey.contains("placeholder", ignoreCase = true) &&
                         !appId.contains("placeholder", ignoreCase = true) &&
                         !projectId.contains("project-id", ignoreCase = true) &&
                         !apiKey.contains("api-key", ignoreCase = true)
            
            if (!isValid) {
                Log.e(TAG, "Firebase configuration contains placeholder or invalid values")
                Log.e(TAG, "Project ID: $projectId, API Key length: ${apiKey?.length}, App ID: $appId")
            }
            
            // Additional check for auth and firestore instances
            if (isValid) {
                try {
                    val authInstance = FirebaseAuth.getInstance()
                    val firestoreInstance = FirebaseFirestore.getInstance()
                    val authValid = authInstance != null
                    val firestoreValid = firestoreInstance != null
                    
                    Log.d(TAG, "Firebase Auth valid: $authValid, Firestore valid: $firestoreValid")
                    
                    return authValid && firestoreValid
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking Firebase service instances", e)
                    return false
                }
            }
            
            isValid
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException checking Firebase configuration", e)
            false
        } catch (e: IllegalStateException) {
            Log.e(TAG, "IllegalStateException - Firebase not initialized", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Firebase configuration", e)
            false
        }
    }

    /**
     * Save user data to Firestore
     */
    private suspend fun saveUserToFirestore(authUser: AuthUser) {
        try {
            Log.d(TAG, "Saving user to Firestore: ${authUser.uid}")
            
            // Check if firestore is available
            if (firestore == null) {
                Log.e(TAG, "Firestore instance is null")
                throw Exception("Firestore not available")
            }
            
            val userProfile = UserProfile.fromAuthUser(authUser)
            
            firestore.collection(USERS_COLLECTION)
                .document(authUser.uid)
                .set(userProfile.toMap())
                .await()
            
            Log.d(TAG, "User saved to Firestore successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException saving user to Firestore", e)
            throw Exception("Masalah keamanan saat menyimpan data user")
        } catch (e: com.google.firebase.FirebaseException) {
            Log.e(TAG, "Firebase exception saving user to Firestore", e)
            throw Exception("Gagal menyimpan data user: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user to Firestore", e)
            throw Exception("Gagal menyimpan data user: ${e.localizedMessage}")
        }
    }

    /**
     * Update user data in Firestore on login
     */
    private suspend fun updateUserInFirestore(authUser: AuthUser) {
        try {
            Log.d(TAG, "Updating user in Firestore: ${authUser.uid}")
            
            // Check if firestore is available
            if (firestore == null) {
                Log.e(TAG, "Firestore instance is null")
                throw Exception("Firestore not available")
            }
            
            val updates = hashMapOf<String, Any>(
                "lastLoginAt" to System.currentTimeMillis(),
                "email" to (authUser.email ?: ""),
                "displayName" to (authUser.displayName ?: "")
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(authUser.uid)
                .update(updates)
                .await()
            
            Log.d(TAG, "User updated in Firestore successfully")
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException updating user in Firestore", e)
            // Try to save user instead if update fails due to security
            try {
                saveUserToFirestore(authUser)
            } catch (saveException: Exception) {
                Log.e(TAG, "Failed to save user after update failed", saveException)
                throw Exception("Gagal memperbarui data user")
            }
        } catch (e: com.google.firebase.firestore.FirebaseFirestoreException) {
            Log.w(TAG, "Firestore exception - user might not exist yet", e)
            // If user doesn't exist, create them
            try {
                saveUserToFirestore(authUser)
            } catch (saveException: Exception) {
                Log.e(TAG, "Failed to save user after update failed", saveException)
                throw Exception("Gagal memperbarui data user")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not update user in Firestore (user might not exist yet)", e)
            try {
                saveUserToFirestore(authUser)
            } catch (saveException: Exception) {
                Log.e(TAG, "Failed to save user after update failed", saveException)
                throw Exception("Gagal memperbarui data user")
            }
        }
    }

    /**
     * Send password reset email
     */
    suspend fun resetPassword(email: String): Resource<Unit> {
        return try {
            Log.d(TAG, "Sending password reset email to: $email")
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Password reset email sent successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending password reset email", e)
            val errorMessage = when {
                e.message?.contains("user-not-found", ignoreCase = true) == true -> 
                    "Email tidak terdaftar"
                e.message?.contains("invalid-email", ignoreCase = true) == true -> 
                    "Format email tidak valid"
                e.message?.contains("network", ignoreCase = true) == true -> 
                    "Tidak ada koneksi internet"
                else -> "Gagal mengirim email reset: ${e.localizedMessage}"
            }
            Resource.Error(errorMessage)
        }
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        try {
            Log.d(TAG, "Signing out user")
            auth.signOut()
            Log.d(TAG, "User signed out successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out", e)
        }
    }

    /**
     * Get current Firebase user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Get user data from Firestore
     */
    suspend fun getUserData(uid: String): Resource<Map<String, Any>> {
        return try {
            Log.d(TAG, "Getting user data from Firestore: $uid")
            
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            
            if (document.exists()) {
                Log.d(TAG, "User data retrieved successfully")
                Resource.Success(document.data ?: emptyMap())
            } else {
                Log.w(TAG, "User document does not exist")
                Resource.Error("Data pengguna tidak ditemukan")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user data", e)
            Resource.Error("Gagal mengambil data pengguna: ${e.localizedMessage}")
        }
    }

    /**
     * Get user profile from Firestore
     */
    suspend fun getUserProfile(uid: String): Resource<UserProfile> {
        return try {
            Log.d(TAG, "Getting user profile from Firestore: $uid")
            
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            
            if (document.exists()) {
                val userProfile = UserProfile.fromMap(document.data ?: emptyMap())
                Log.d(TAG, "User profile retrieved successfully")
                Resource.Success(userProfile)
            } else {
                Log.w(TAG, "User profile does not exist")
                Resource.Error("Profil pengguna tidak ditemukan")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile", e)
            Resource.Error("Gagal mengambil profil pengguna: ${e.localizedMessage}")
        }
    }

    /**
     * Update user profile in Firestore
     */
    suspend fun updateUserProfile(userProfile: UserProfile): Resource<UserProfile> {
        return try {
            Log.d(TAG, "Updating user profile in Firestore: ${userProfile.uid}")
            
            firestore.collection(USERS_COLLECTION)
                .document(userProfile.uid)
                .set(userProfile.toMap())
                .await()
            
            Log.d(TAG, "User profile updated successfully")
            Resource.Success(userProfile)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            Resource.Error("Gagal memperbarui profil: ${e.localizedMessage}")
        }
    }

    /**
     * Update specific user data fields
     */
    suspend fun updateUserData(uid: String, updates: Map<String, Any>): Resource<Unit> {
        return try {
            Log.d(TAG, "Updating user data in Firestore: $uid")
            
            val updatesWithTimestamp = updates.toMutableMap()
            updatesWithTimestamp["lastLoginAt"] = System.currentTimeMillis()
            
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(updatesWithTimestamp)
                .await()
            
            Log.d(TAG, "User data updated successfully")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user data", e)
            Resource.Error("Gagal memperbarui data: ${e.localizedMessage}")
        }
    }

    /**
     * Check if user exists in Firestore
     */
    suspend fun userExistsInFirestore(uid: String): Boolean {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
            document.exists()
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if user exists", e)
            false
        }
    }
}
