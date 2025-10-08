package com.komputerkit.earningapp.data.model

/**
 * Simple wrapper for user authentication data
 * Can represent both Firebase and local users
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String? = null,
    val isLocal: Boolean = false
) {
    companion object {
        fun fromFirebaseUser(firebaseUser: com.google.firebase.auth.FirebaseUser?): AuthUser? {
            return try {
                if (firebaseUser == null) {
                    null
                } else {
                    AuthUser(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email,
                        displayName = firebaseUser.displayName,
                        isLocal = false
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthUser", "Error creating AuthUser from FirebaseUser", e)
                null
            }
        }
        
        fun createLocalUser(email: String, name: String? = null): AuthUser {
            return AuthUser(
                uid = "local_${email.hashCode()}",
                email = email,
                displayName = name,
                isLocal = true
            )
        }
    }
}
