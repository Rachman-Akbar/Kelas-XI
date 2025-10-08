package com.komputerkit.earningapp.utils

import android.content.Context
import android.util.Log
import com.komputerkit.earningapp.data.repository.AuthRepository
import com.komputerkit.earningapp.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Test utility to verify that registration works with local fallback
 */
object RegistrationTestUtils {
    private const val TAG = "RegistrationTestUtils"
    
    fun testRegistrationFlow(context: Context) {
        Log.d(TAG, "🧪 Starting registration flow test...")
        
        val authRepository = AuthRepository().apply {
            // Note: AuthRepository.initialize is static, should use AuthRepository.initialize(context)
        }
        AuthRepository.initialize(context)
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d(TAG, "🔄 Testing registration with test email...")
                
                val testEmail = "test_${System.currentTimeMillis()}@local.com"
                val testPassword = "password123"
                val testName = "Test User"
                
                Log.d(TAG, "📧 Test email: $testEmail")
                
                val result = authRepository.signUp(testEmail, testPassword, testName)
                
                when (result) {
                    is Resource.Success -> {
                        val user = result.data
                        if (user != null) {
                            Log.d(TAG, "✅ Registration successful!")
                            Log.d(TAG, "   User ID: ${user.uid}")
                            Log.d(TAG, "   Email: ${user.email}")
                            Log.d(TAG, "   Display Name: ${user.displayName}")
                            Log.d(TAG, "   Is Local: ${user.isLocal}")
                            
                            // Test if we can login with the registered user (indicating successful storage)
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val loginResult = LocalAuthManager.loginUser(context, testEmail, testPassword)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        if (loginResult.isSuccess) {
                                            val localUser = loginResult.getOrNull()
                                            Log.d(TAG, "✅ Local storage verification successful!")
                                            Log.d(TAG, "   Can login as: ${localUser?.name} (${localUser?.email})")
                                        } else {
                                            Log.w(TAG, "⚠️ Could not login with registered credentials")
                                        }
                                    }
                                } catch (e: Exception) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        Log.e(TAG, "💥 Error verifying local storage", e)
                                    }
                                }
                            }
                        } else {
                            Log.e(TAG, "❌ Registration successful but user data is null")
                        }
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "❌ Registration failed: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "🔄 Registration still loading...")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Registration test failed with exception", e)
            }
        }
    }
    
    fun testLocalAuthManager(context: Context) {
        Log.d(TAG, "🧪 Testing LocalAuthManager directly...")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val testEmail = "direct_test_${System.currentTimeMillis()}@local.com"
                val testPassword = "password123"
                val testName = "Direct Test User"
                
                Log.d(TAG, "📧 Direct test email: $testEmail")
                
                val registerResult = LocalAuthManager.registerUser(context, testName, testEmail, testPassword)
                
                if (registerResult.isSuccess) {
                    Log.d(TAG, "✅ LocalAuthManager registration successful!")
                    
                    val loginResult = LocalAuthManager.loginUser(context, testEmail, testPassword)
                    
                    if (loginResult.isSuccess) {
                        val user = loginResult.getOrNull()
                        Log.d(TAG, "✅ LocalAuthManager login successful!")
                        Log.d(TAG, "   User: ${user?.name} (${user?.email})")
                    } else {
                        Log.e(TAG, "❌ LocalAuthManager login failed: ${loginResult.exceptionOrNull()?.message}")
                    }
                } else {
                    Log.e(TAG, "❌ LocalAuthManager registration failed: ${registerResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 LocalAuthManager test failed with exception", e)
            }
        }
    }
}
