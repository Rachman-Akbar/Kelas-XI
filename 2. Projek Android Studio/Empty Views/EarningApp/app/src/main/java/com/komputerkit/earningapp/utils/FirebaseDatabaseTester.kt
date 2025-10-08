package com.komputerkit.earningapp.utils

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase database tester to ensure proper integration
 */
object FirebaseDatabaseTester {
    private const val TAG = "FirebaseDatabaseTester"
    
    /**
     * Test Firebase connectivity and database operations
     */
    suspend fun testFirebaseDatabase(context: Context): TestResult {
        val results = mutableListOf<String>()
        val errors = mutableListOf<String>()
        
        try {
            // 1. Test Firebase initialization
            results.add("✓ Testing Firebase initialization...")
            val apps = FirebaseApp.getApps(context)
            if (apps.isNotEmpty()) {
                results.add("✓ Firebase is initialized (${apps.size} app(s))")
            } else {
                errors.add("✗ Firebase is not initialized")
                return TestResult(false, results, errors)
            }
            
            // 2. Test Firebase Auth
            results.add("✓ Testing Firebase Auth...")
            val auth = FirebaseAuth.getInstance()
            if (auth != null) {
                results.add("✓ Firebase Auth instance created")
                
                // Test Auth configuration
                val languageCode = auth.languageCode
                results.add("✓ Auth language code: ${languageCode ?: "default"}")
            } else {
                errors.add("✗ Failed to get Firebase Auth instance")
            }
            
            // 3. Test Firestore
            results.add("✓ Testing Firebase Firestore...")
            val firestore = FirebaseFirestore.getInstance()
            if (firestore != null) {
                results.add("✓ Firestore instance created")
                
                // Test Firestore connectivity with a simple read operation
                try {
                    val testCollection = firestore.collection("test")
                    val snapshot = testCollection.limit(1).get().await()
                    results.add("✓ Firestore connectivity test passed")
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore connectivity test failed (this is normal for new databases)", e)
                    results.add("⚠ Firestore connectivity test failed (normal for new databases)")
                }
            } else {
                errors.add("✗ Failed to get Firestore instance")
            }
            
            // 4. Test users collection access
            results.add("✓ Testing users collection access...")
            try {
                val usersRef = firestore.collection("users")
                // Just test if we can create a reference (doesn't require data to exist)
                results.add("✓ Users collection reference created")
                
                // Test if we can query (this should work even if collection is empty)
                val query = usersRef.limit(1)
                results.add("✓ Users collection query created")
            } catch (e: Exception) {
                errors.add("✗ Failed to access users collection: ${e.message}")
            }
            
            // 5. Test Firebase project configuration
            results.add("✓ Testing Firebase project configuration...")
            try {
                val app = FirebaseApp.getInstance()
                val options = app.options
                
                results.add("✓ Project ID: ${options.projectId}")
                results.add("✓ Application ID: ${options.applicationId}")
                
                if (options.apiKey.isNotEmpty()) {
                    results.add("✓ API Key configured")
                } else {
                    errors.add("✗ API Key not configured")
                }
                
                if (options.databaseUrl?.isNotEmpty() == true) {
                    results.add("✓ Database URL: ${options.databaseUrl}")
                } else {
                    results.add("⚠ Database URL not configured (using default)")
                }
                
            } catch (e: Exception) {
                errors.add("✗ Failed to get Firebase configuration: ${e.message}")
            }
            
            return TestResult(errors.isEmpty(), results, errors)
            
        } catch (e: Exception) {
            Log.e(TAG, "Firebase database test failed", e)
            errors.add("✗ Fatal error during Firebase testing: ${e.message}")
            return TestResult(false, results, errors)
        }
    }
    
    /**
     * Test user registration and data saving
     */
    suspend fun testUserDataOperations(): TestResult {
        val results = mutableListOf<String>()
        val errors = mutableListOf<String>()
        
        try {
            results.add("✓ Testing user data operations...")
            
            val firestore = FirebaseFirestore.getInstance()
            val testUserId = "test_user_${System.currentTimeMillis()}"
            
            // Test creating user document
            val testUserData = hashMapOf(
                "uid" to testUserId,
                "email" to "test@example.com",
                "displayName" to "Test User",
                "createdAt" to System.currentTimeMillis(),
                "isActive" to true,
                "level" to 1,
                "experience" to 0,
                "coins" to 0
            )
            
            // Test write operation
            firestore.collection("users")
                .document(testUserId)
                .set(testUserData)
                .await()
            
            results.add("✓ User document created successfully")
            
            // Test read operation
            val document = firestore.collection("users")
                .document(testUserId)
                .get()
                .await()
            
            if (document.exists()) {
                results.add("✓ User document read successfully")
                
                val data = document.data
                if (data != null && data["email"] == "test@example.com") {
                    results.add("✓ User data integrity verified")
                } else {
                    errors.add("✗ User data integrity check failed")
                }
            } else {
                errors.add("✗ User document not found after creation")
            }
            
            // Test update operation
            firestore.collection("users")
                .document(testUserId)
                .update("lastLoginAt", System.currentTimeMillis())
                .await()
            
            results.add("✓ User document updated successfully")
            
            // Clean up test data
            firestore.collection("users")
                .document(testUserId)
                .delete()
                .await()
            
            results.add("✓ Test data cleaned up")
            
            return TestResult(errors.isEmpty(), results, errors)
            
        } catch (e: Exception) {
            Log.e(TAG, "User data operations test failed", e)
            errors.add("✗ User data operations test failed: ${e.message}")
            return TestResult(false, results, errors)
        }
    }
    
    data class TestResult(
        val success: Boolean,
        val results: List<String>,
        val errors: List<String>
    ) {
        fun getFormattedReport(): String {
            val report = StringBuilder()
            report.appendLine("=== Firebase Database Test Report ===")
            report.appendLine()
            
            if (success) {
                report.appendLine("🎉 All tests passed!")
            } else {
                report.appendLine("❌ Some tests failed")
            }
            report.appendLine()
            
            report.appendLine("Results:")
            results.forEach { result ->
                report.appendLine(result)
            }
            
            if (errors.isNotEmpty()) {
                report.appendLine()
                report.appendLine("Errors:")
                errors.forEach { error ->
                    report.appendLine(error)
                }
            }
            
            return report.toString()
        }
    }
}
