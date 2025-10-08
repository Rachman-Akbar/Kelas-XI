package com.komputerkit.earningapp.ui.debug

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.R
import com.komputerkit.earningapp.data.repository.AuthRepository
import com.komputerkit.earningapp.utils.DebugUtils
import com.komputerkit.earningapp.utils.FirebaseErrorHandler
import com.komputerkit.earningapp.utils.NetworkUtils
import com.komputerkit.earningapp.utils.Resource
import com.komputerkit.earningapp.utils.FirebaseConfigValidator
import com.komputerkit.earningapp.utils.RegistrationTester
import com.komputerkit.earningapp.utils.FirebaseDatabaseTester
import com.komputerkit.earningapp.utils.RegistrationFlowTester
import kotlinx.coroutines.launch

class DebugActivity : AppCompatActivity() {
    
    private lateinit var etTestEmail: EditText
    private lateinit var etTestPassword: EditText
    private lateinit var etTestName: EditText
    private lateinit var btnTestFirebase: Button
    private lateinit var btnTestRegistration: Button
    private lateinit var btnTestDatabase: Button
    private lateinit var tvDebugOutput: TextView
    
    private val authRepository = AuthRepository()
    
    companion object {
        private const val TAG = "DebugActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_debug)
            
            initViews()
            setupClickListeners()
            
            // Run initial tests
            runInitialTests()
        } catch (e: Exception) {
            Log.e(TAG, "Error in DebugActivity onCreate", e)
            finish()
        }
    }
    
    private fun initViews() {
        try {
            etTestEmail = findViewById(R.id.etTestEmail)
            etTestPassword = findViewById(R.id.etTestPassword)
            etTestName = findViewById(R.id.etTestName)
            btnTestFirebase = findViewById(R.id.btnTestFirebase)
            btnTestRegistration = findViewById(R.id.btnTestRegistration)
            btnTestDatabase = findViewById(R.id.btnTestDatabase)
            tvDebugOutput = findViewById(R.id.tvDebugOutput)
            
            // Set default test values
            etTestEmail.setText("test.user@example.com")
            etTestPassword.setText("testpass123")
            etTestName.setText("Test User")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            appendToOutput("Error initializing views: ${e.message}")
        }
    }
    
    private fun setupClickListeners() {
        btnTestFirebase.setOnClickListener {
            runFirebaseTests()
        }
        
        btnTestDatabase.setOnClickListener {
            runDatabaseTests()
        }
        
        btnTestRegistration.setOnClickListener {
            val email = etTestEmail.text.toString().trim()
            val password = etTestPassword.text.toString().trim()
            val name = etTestName.text.toString().trim()
            
            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                appendToOutput("❌ Please fill all test fields")
                return@setOnClickListener
            }
            
            // Test both original registration and local fallback
            testRegistration(email, password, name)
            
            // Also test the new local auth system
            appendToOutput("\n🧪 Testing Local Authentication System...")
            com.komputerkit.earningapp.utils.RegistrationTestUtils.testRegistrationFlow(this@DebugActivity)
            com.komputerkit.earningapp.utils.RegistrationTestUtils.testLocalAuthManager(this@DebugActivity)
        }
    }
    
    private fun runInitialTests() {
        appendToOutput("=== INITIAL DIAGNOSTIC TESTS ===")
        
        // Test Firebase initialization
        appendToOutput("\n1. Firebase App Status:")
        try {
            val app = FirebaseApp.getInstance()
            appendToOutput("✓ Firebase App initialized: ${app.name}")
            appendToOutput("✓ App ID: ${app.options.applicationId}")
        } catch (e: Exception) {
            appendToOutput("❌ Firebase App error: ${e.message}")
        }
        
        // Test Firebase Auth
        appendToOutput("\n2. Firebase Auth Status:")
        try {
            val auth = FirebaseAuth.getInstance()
            appendToOutput("✓ Firebase Auth initialized")
            appendToOutput("✓ Current user: ${auth.currentUser?.email ?: "None"}")
        } catch (e: Exception) {
            appendToOutput("❌ Firebase Auth error: ${e.message}")
        }
        
        // Test Firestore
        appendToOutput("\n3. Firestore Status:")
        try {
            val firestore = FirebaseFirestore.getInstance()
            appendToOutput("✓ Firestore initialized")
        } catch (e: Exception) {
            appendToOutput("❌ Firestore error: ${e.message}")
        }
        
        // Test Network
        appendToOutput("\n4. Network Status:")
        try {
            val isConnected = NetworkUtils.isNetworkAvailable(this)
            appendToOutput("✓ Network available: $isConnected")
        } catch (e: Exception) {
            appendToOutput("❌ Network check error: ${e.message}")
        }
        
        appendToOutput("\n=== INITIAL TESTS COMPLETE ===\n")
    }
    
    private fun runDatabaseTests() {
        appendToOutput("\n=== FIREBASE DATABASE TESTS ===")
        
        lifecycleScope.launch {
            try {
                // Test Firebase database connectivity
                appendToOutput("🔍 Testing Firebase database connectivity...")
                val dbTestResult = FirebaseDatabaseTester.testFirebaseDatabase(this@DebugActivity)
                appendToOutput(dbTestResult.getFormattedReport())
                
                if (dbTestResult.success) {
                    // Test user data operations
                    appendToOutput("\n🔍 Testing user data operations...")
                    val userTestResult = FirebaseDatabaseTester.testUserDataOperations()
                    appendToOutput(userTestResult.getFormattedReport())
                }
                
                // Test authentication repository
                appendToOutput("\n🔍 Testing AuthRepository...")
                testAuthRepository()
                
            } catch (e: Exception) {
                Log.e(TAG, "Error during Firebase tests", e)
                appendToOutput("❌ Firebase tests failed: ${e.message}")
            }
        }
    }
    
    private suspend fun testAuthRepository() {
        try {
            val authRepo = AuthRepository()
            
            // Test getting current user
            val currentUser = authRepo.getCurrentUser()
            if (currentUser != null) {
                appendToOutput("✓ Current user: ${currentUser.email}")
                
                // Test getting user profile
                val profileResult = authRepo.getUserProfile(currentUser.uid)
                when (profileResult) {
                    is Resource.Success -> {
                        appendToOutput("✓ User profile loaded: ${profileResult.data?.displayName}")
                    }
                    is Resource.Error -> {
                        appendToOutput("⚠ User profile not found (this is normal for new users)")
                    }
                    is Resource.Loading -> {
                        appendToOutput("🔄 Loading user profile...")
                    }
                }
            } else {
                appendToOutput("ℹ No user currently logged in")
            }
            
        } catch (e: Exception) {
            appendToOutput("❌ AuthRepository test failed: ${e.message}")
        }
    }
    
    private fun runFirebaseTests() {
        appendToOutput("\n=== FIREBASE DIAGNOSTIC TESTS ===")
        
        lifecycleScope.launch {
            try {
                // Run comprehensive Firebase validation
                val validationResult = FirebaseConfigValidator.validateFirebaseConfiguration(this@DebugActivity)
                
                appendToOutput("\n📋 FIREBASE CONFIGURATION VALIDATION:")
                
                if (validationResult.isValid) {
                    appendToOutput("✅ Overall Status: VALID")
                } else {
                    appendToOutput("❌ Overall Status: INVALID")
                }
                
                appendToOutput("\n✅ Successes:")
                validationResult.successes.forEach { success: String ->
                    appendToOutput(success)
                }
                
                if (validationResult.issues.isNotEmpty()) {
                    appendToOutput("\n❌ Issues Found:")
                    validationResult.issues.forEach { issue: String ->
                        appendToOutput(issue)
                    }
                }
                
                appendToOutput("\n✓ Firebase tests completed - check logcat for details")
                appendToOutput("=== FIREBASE TESTS COMPLETE ===\n")
            } catch (e: Exception) {
                appendToOutput("❌ Firebase tests failed: ${e.message}")
                Log.e(TAG, "Firebase tests error", e)
            }
        }
    }
    
    private fun testRegistration(email: String, password: String, name: String) {
        appendToOutput("\n=== COMPREHENSIVE REGISTRATION TEST ===")
        appendToOutput("Testing with:")
        appendToOutput("Email: $email")
        appendToOutput("Password: ${password.replace(Regex("."), "*")}")
        appendToOutput("Name: $name")
        
        lifecycleScope.launch {
            try {
                // Test complete registration flow
                appendToOutput("\n🔍 Testing complete registration and login flow...")
                val flowResult = RegistrationFlowTester.testCompleteFlow(this@DebugActivity)
                appendToOutput(flowResult.getFormattedReport())
                
                // Test authentication validation
                appendToOutput("\n🔍 Testing authentication validation...")
                val validationResult = RegistrationFlowTester.testAuthValidation()
                appendToOutput(validationResult.getFormattedReport())
                
                // Test individual registration with provided data
                appendToOutput("\n🔍 Testing registration with provided data...")
                val authRepository = AuthRepository()
                val individualResult = authRepository.signUp(email, password, name)
                
                when (individualResult) {
                    is Resource.Success -> {
                        appendToOutput("✅ Individual registration test PASSED!")
                        appendToOutput("✅ User created: ${individualResult.data?.email}")
                        appendToOutput("✅ Firebase integration is working correctly!")
                        
                        // Test user profile retrieval
                        val profileResult = authRepository.getUserProfile(individualResult.data?.uid ?: "")
                        when (profileResult) {
                            is Resource.Success -> {
                                appendToOutput("✅ User profile retrieved successfully")
                                appendToOutput("✅ Data imported to Firebase Firestore!")
                            }
                            is Resource.Error -> {
                                appendToOutput("⚠ Profile retrieval failed: ${profileResult.message}")
                            }
                            is Resource.Loading -> {
                                appendToOutput("🔄 Loading profile...")
                            }
                        }
                        
                    }
                    is Resource.Error -> {
                        appendToOutput("❌ Individual registration test FAILED!")
                        appendToOutput("❌ Error: ${individualResult.message}")
                        
                        // Check if it's due to existing email
                        if (individualResult.message?.contains("already", ignoreCase = true) == true) {
                            appendToOutput("ℹ This error is expected if the email is already registered")
                            appendToOutput("✅ Firebase validation is working properly")
                        }
                    }
                    is Resource.Loading -> {
                        appendToOutput("🔄 Testing individual registration...")
                    }
                }
                
                appendToOutput("\n=== REGISTRATION TESTS COMPLETE ===")
                
            } catch (e: Exception) {
                appendToOutput("❌ Registration tests failed: ${e.message}")
                Log.e(TAG, "Registration tests error", e)
            }
        }
    }
    
    private fun appendToOutput(text: String) {
        try {
            runOnUiThread {
                try {
                    val currentText = tvDebugOutput.text?.toString() ?: ""
                    tvDebugOutput.text = "$currentText\n$text"
                    
                    // Scroll to bottom (if scrollable)
                    tvDebugOutput.post {
                        try {
                            val scrollY = tvDebugOutput.layout?.height ?: 0
                            (tvDebugOutput.parent as? android.widget.ScrollView)?.smoothScrollTo(0, scrollY)
                        } catch (e: Exception) {
                            Log.w(TAG, "Error scrolling debug output", e)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error updating debug output", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in appendToOutput", e)
        }
    }
}
