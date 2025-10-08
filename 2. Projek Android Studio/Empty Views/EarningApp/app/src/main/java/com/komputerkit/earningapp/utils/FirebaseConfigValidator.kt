package com.komputerkit.earningapp.utils

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirebaseConfigValidator {
    private const val TAG = "FirebaseConfigValidator"
    
    fun validateFirebaseConfiguration(context: Context): ValidationResult {
        Log.d(TAG, "=== Starting Firebase Configuration Validation ===")
        
        val issues = mutableListOf<String>()
        val successes = mutableListOf<String>()
        
        try {
            // 1. Check Firebase App Initialization
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                issues.add("❌ Firebase App not initialized")
            } else {
                successes.add("✅ Firebase App initialized (${apps.size} app(s))")
                
                val mainApp = apps.first()
                val options = mainApp.options
                
                // 2. Validate Firebase Options
                if (!options.applicationId.isNullOrEmpty()) {
                    successes.add("✅ Application ID: ${options.applicationId}")
                } else {
                    issues.add("❌ Missing Application ID")
                }
                
                if (!options.apiKey.isNullOrEmpty()) {
                    successes.add("✅ API Key configured")
                } else {
                    issues.add("❌ Missing API Key")
                }
                
                if (!options.projectId.isNullOrEmpty()) {
                    successes.add("✅ Project ID: ${options.projectId}")
                } else {
                    issues.add("❌ Missing Project ID")
                }
            }
            
            // 3. Test Firebase Auth
            try {
                val auth = FirebaseAuth.getInstance()
                successes.add("✅ Firebase Auth instance created")
                
                // Test if we can get current user (should be null for new users)
                val currentUser = auth.currentUser
                successes.add("✅ Firebase Auth connectivity: ${if (currentUser != null) "User logged in" else "Ready for registration"}")
                
            } catch (e: Exception) {
                issues.add("❌ Firebase Auth error: ${e.message}")
            }
            
            // 4. Test Firebase Firestore
            try {
                val firestore = FirebaseFirestore.getInstance()
                successes.add("✅ Firebase Firestore instance created")
            } catch (e: Exception) {
                issues.add("❌ Firebase Firestore error: ${e.message}")
            }
            
            // 5. Check Network Configuration
            val networkAvailable = NetworkUtils.isNetworkAvailable(context)
            if (networkAvailable) {
                successes.add("✅ Network connectivity available")
            } else {
                issues.add("❌ No network connectivity")
            }
            
        } catch (e: Exception) {
            issues.add("❌ Validation failed with exception: ${e.message}")
            Log.e(TAG, "Validation exception", e)
        }
        
        Log.d(TAG, "=== Validation Complete ===")
        Log.d(TAG, "Successes: ${successes.size}")
        Log.d(TAG, "Issues: ${issues.size}")
        
        return ValidationResult(
            isValid = issues.isEmpty(),
            successes = successes,
            issues = issues
        )
    }
    
    suspend fun testFirebaseAuth(email: String, password: String): AuthTestResult {
        return try {
            Log.d(TAG, "Testing Firebase Auth with email: $email")
            
            val auth = FirebaseAuth.getInstance()
            
            // Try to create user (this will fail if configuration is wrong)
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            
            // If successful, immediately delete the test user
            result.user?.delete()?.await()
            
            AuthTestResult(
                success = true,
                message = "✅ Firebase Authentication is working correctly"
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Auth test failed", e)
            
            val message = when {
                e.message?.contains("CONFIGURATION_NOT_FOUND") == true ->
                    "❌ Firebase configuration not found - check google-services.json"
                e.message?.contains("API_KEY_INVALID") == true ->
                    "❌ Invalid API key in Firebase configuration"
                e.message?.contains("PROJECT_NOT_FOUND") == true ->
                    "❌ Firebase project not found - check project ID"
                e.message?.contains("NETWORK") == true ->
                    "❌ Network error - check internet connection"
                else ->
                    "❌ Auth test failed: ${e.message}"
            }
            
            AuthTestResult(
                success = false,
                message = message,
                exception = e
            )
        }
    }
    
    data class ValidationResult(
        val isValid: Boolean,
        val successes: List<String>,
        val issues: List<String>
    )
    
    data class AuthTestResult(
        val success: Boolean,
        val message: String,
        val exception: Exception? = null
    )
}
