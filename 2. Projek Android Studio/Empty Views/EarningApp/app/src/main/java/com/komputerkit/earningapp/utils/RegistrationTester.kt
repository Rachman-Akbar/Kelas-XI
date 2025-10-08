package com.komputerkit.earningapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object RegistrationTester {
    private const val TAG = "RegistrationTester"
    
    suspend fun testBasicRegistration(context: Context, email: String, password: String, name: String): Boolean {
        return try {
            Log.d(TAG, "Testing basic registration for: $email")
            
            // Step 1: Test Firebase Auth initialization
            val auth = FirebaseAuth.getInstance()
            Log.d(TAG, "✅ Firebase Auth instance created")
            
            // Step 2: Test Firestore initialization  
            val firestore = FirebaseFirestore.getInstance()
            Log.d(TAG, "✅ Firestore instance created")
            
            // Step 3: Try basic authentication (will fail if config is wrong)
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                Log.d(TAG, "✅ Firebase Auth registration successful: ${result.user?.uid}")
                
                // Step 4: Try creating a Firestore document
                val userData = mapOf(
                    "name" to name,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis(),
                    "testRegistration" to true
                )
                
                result.user?.let { user ->
                    firestore.collection("users").document(user.uid).set(userData).await()
                    Log.d(TAG, "✅ Firestore document created successfully")
                    
                    // Clean up test user
                    user.delete().await()
                    Log.d(TAG, "✅ Test user cleaned up")
                    
                    Toast.makeText(context, "✅ Registration test successful!", Toast.LENGTH_LONG).show()
                    true
                } ?: false
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Registration test failed", e)
                
                val errorMessage = when {
                    e.message?.contains("CONFIGURATION_NOT_FOUND") == true -> 
                        "❌ Firebase not configured. Check DATABASE_SETUP_INSTRUCTIONS.md"
                    e.message?.contains("NETWORK") == true -> 
                        "❌ Network error. Check internet connection"
                    e.message?.contains("EMAIL_ALREADY_IN_USE") == true ->
                        "✅ Firebase working (email already exists)"
                    else -> "❌ Error: ${e.message}"
                }
                
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                false
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Fatal registration test error", e)
            Toast.makeText(context, "❌ Fatal error: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }
    
    fun testFirebaseConnection(context: Context) {
        try {
            val auth = FirebaseAuth.getInstance()
            val firestore = FirebaseFirestore.getInstance()
            
            val authStatus = if (auth.app != null) "✅ Connected" else "❌ Not connected"
            val firestoreStatus = if (firestore.app != null) "✅ Connected" else "❌ Not connected"
            
            val message = """
                Firebase Status:
                Auth: $authStatus
                Firestore: $firestoreStatus
                Current User: ${auth.currentUser?.email ?: "None"}
            """.trimIndent()
            
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            Log.d(TAG, message)
            
        } catch (e: Exception) {
            Toast.makeText(context, "❌ Firebase connection test failed: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e(TAG, "Firebase connection test failed", e)
        }
    }
}
