package com.komputerkit.earningapp.utils

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.data.repository.AuthRepository
import kotlinx.coroutines.tasks.await

/**
 * Comprehensive test for registration and login flow with Firebase integration
 */
object RegistrationFlowTester {
    private const val TAG = "RegistrationFlowTester"
    
    /**
     * Test complete registration and login flow
     */
    suspend fun testCompleteFlow(context: Context): TestResult {
        val results = mutableListOf<String>()
        val errors = mutableListOf<String>()
        
        try {
            results.add("=== Testing Complete Registration & Login Flow ===")
            
            val testEmail = "testuser_${System.currentTimeMillis()}@example.com"
            val testPassword = "SecurePassword123!"
            val testName = "Test User"
            
            results.add("📧 Test Email: $testEmail")
            results.add("👤 Test Name: $testName")
            
            val authRepository = AuthRepository()
            
            // 1. Test Registration
            results.add("\n🔶 Step 1: Testing Registration...")
            
            val registerResult = authRepository.signUp(testEmail, testPassword, testName)
            when (registerResult) {
                is Resource.Success -> {
                    results.add("✅ Registration successful!")
                    results.add("✅ User UID: ${registerResult.data?.uid}")
                    results.add("✅ User Email: ${registerResult.data?.email}")
                    results.add("✅ User Name: ${registerResult.data?.displayName}")
                    
                    val userId = registerResult.data?.uid
                    if (userId != null) {
                        // 2. Test Firestore Data
                        results.add("\n🔶 Step 2: Testing Firestore Data...")
                        val profileResult = authRepository.getUserProfile(userId)
                        when (profileResult) {
                            is Resource.Success -> {
                                results.add("✅ User profile saved to Firestore")
                                results.add("✅ Profile data: ${profileResult.data?.displayName}")
                                results.add("✅ Level: ${profileResult.data?.level}")
                                results.add("✅ Coins: ${profileResult.data?.coins}")
                            }
                            is Resource.Error -> {
                                errors.add("❌ Failed to get user profile: ${profileResult.message}")
                            }
                            is Resource.Loading -> {
                                results.add("🔄 Loading profile...")
                            }
                        }
                        
                        // 3. Test Sign Out
                        results.add("\n🔶 Step 3: Testing Sign Out...")
                        authRepository.signOut()
                        val currentUserAfterSignOut = authRepository.getCurrentUser()
                        if (currentUserAfterSignOut == null) {
                            results.add("✅ Sign out successful")
                        } else {
                            errors.add("❌ Sign out failed - user still logged in")
                        }
                        
                        // 4. Test Sign In
                        results.add("\n🔶 Step 4: Testing Sign In...")
                        val signInResult = authRepository.signIn(testEmail, testPassword)
                        when (signInResult) {
                            is Resource.Success -> {
                                results.add("✅ Sign in successful!")
                                results.add("✅ User UID: ${signInResult.data?.uid}")
                                results.add("✅ User Email: ${signInResult.data?.email}")
                                
                                // 5. Test Profile Update
                                results.add("\n🔶 Step 5: Testing Profile Update...")
                                val updateData = mapOf(
                                    "experience" to 100,
                                    "coins" to 50,
                                    "level" to 2
                                )
                                
                                val updateResult = authRepository.updateUserData(userId, updateData)
                                when (updateResult) {
                                    is Resource.Success -> {
                                        results.add("✅ Profile update successful")
                                        
                                        // Verify update
                                        val updatedProfileResult = authRepository.getUserProfile(userId)
                                        when (updatedProfileResult) {
                                            is Resource.Success -> {
                                                results.add("✅ Updated experience: ${updatedProfileResult.data?.experience}")
                                                results.add("✅ Updated coins: ${updatedProfileResult.data?.coins}")
                                                results.add("✅ Updated level: ${updatedProfileResult.data?.level}")
                                            }
                                            is Resource.Error -> {
                                                errors.add("❌ Failed to verify profile update")
                                            }
                                            is Resource.Loading -> {
                                                results.add("🔄 Loading updated profile...")
                                            }
                                        }
                                    }
                                    is Resource.Error -> {
                                        errors.add("❌ Profile update failed: ${updateResult.message}")
                                    }
                                    is Resource.Loading -> {
                                        results.add("🔄 Updating profile...")
                                    }
                                }
                                
                                // 6. Cleanup - Delete test user
                                results.add("\n🔶 Step 6: Cleanup...")
                                try {
                                    // Delete from Firestore
                                    FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(userId)
                                        .delete()
                                        .await()
                                    results.add("✅ Test user data cleaned from Firestore")
                                    
                                    // Delete from Firebase Auth
                                    val currentUser = FirebaseAuth.getInstance().currentUser
                                    currentUser?.delete()?.await()
                                    results.add("✅ Test user deleted from Firebase Auth")
                                    
                                } catch (e: Exception) {
                                    results.add("⚠️ Cleanup warning: ${e.message}")
                                    Log.w(TAG, "Cleanup warning", e)
                                }
                            }
                            is Resource.Error -> {
                                errors.add("❌ Sign in failed: ${signInResult.message}")
                            }
                            is Resource.Loading -> {
                                results.add("🔄 Signing in...")
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    errors.add("❌ Registration failed: ${registerResult.message}")
                }
                is Resource.Loading -> {
                    results.add("🔄 Registering...")
                }
            }
            
            return TestResult(errors.isEmpty(), results, errors)
            
        } catch (e: Exception) {
            Log.e(TAG, "Registration flow test failed", e)
            errors.add("❌ Fatal error during registration flow test: ${e.message}")
            return TestResult(false, results, errors)
        }
    }
    
    /**
     * Test authentication validation
     */
    suspend fun testAuthValidation(): TestResult {
        val results = mutableListOf<String>()
        val errors = mutableListOf<String>()
        
        try {
            results.add("=== Testing Authentication Validation ===")
            
            val authRepository = AuthRepository()
            
            // Test invalid email
            results.add("\n🔶 Testing Invalid Email...")
            val invalidEmailResult = authRepository.signUp("invalid-email", "password123", "Test User")
            when (invalidEmailResult) {
                is Resource.Error -> {
                    results.add("✅ Invalid email properly rejected")
                }
                is Resource.Success -> {
                    errors.add("❌ Invalid email was accepted")
                }
                is Resource.Loading -> {
                    results.add("🔄 Testing invalid email...")
                }
            }
            
            // Test weak password
            results.add("\n🔶 Testing Weak Password...")
            val weakPasswordResult = authRepository.signUp("test@example.com", "123", "Test User")
            when (weakPasswordResult) {
                is Resource.Error -> {
                    results.add("✅ Weak password properly rejected")
                }
                is Resource.Success -> {
                    errors.add("❌ Weak password was accepted")
                }
                is Resource.Loading -> {
                    results.add("🔄 Testing weak password...")
                }
            }
            
            // Test existing email
            results.add("\n🔶 Testing Existing Email (if any)...")
            val existingEmailResult = authRepository.signUp("admin@example.com", "password123", "Admin User")
            when (existingEmailResult) {
                is Resource.Error -> {
                    if (existingEmailResult.message?.contains("already", ignoreCase = true) == true) {
                        results.add("✅ Existing email properly rejected")
                    } else {
                        results.add("✅ Email validation working (${existingEmailResult.message})")
                    }
                }
                is Resource.Success -> {
                    results.add("ℹ️ Email was new, registration succeeded")
                    // Clean up if this was successful
                    try {
                        authRepository.getCurrentUser()?.delete()?.await()
                        results.add("✅ Test user cleaned up")
                    } catch (e: Exception) {
                        results.add("⚠️ Cleanup warning: ${e.message}")
                    }
                }
                is Resource.Loading -> {
                    results.add("🔄 Testing existing email...")
                }
            }
            
            return TestResult(errors.isEmpty(), results, errors)
            
        } catch (e: Exception) {
            Log.e(TAG, "Auth validation test failed", e)
            errors.add("❌ Fatal error during auth validation test: ${e.message}")
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
            report.appendLine("=== Registration Flow Test Report ===")
            report.appendLine()
            
            if (success) {
                report.appendLine("🎉 All tests passed! Login and Registration are working properly with Firebase!")
            } else {
                report.appendLine("❌ Some tests failed - check errors below")
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
