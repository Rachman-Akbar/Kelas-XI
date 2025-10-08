package com.komputerkit.earningapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.R
import com.komputerkit.earningapp.data.repository.AuthRepository
import com.komputerkit.earningapp.data.model.AuthUser
import com.komputerkit.earningapp.databinding.ActivityRegisterBinding
import com.komputerkit.earningapp.MainActivity
import com.komputerkit.earningapp.utils.Resource
import com.komputerkit.earningapp.utils.NetworkUtils
import com.komputerkit.earningapp.utils.DebugUtils
import com.komputerkit.earningapp.utils.FirebaseConfigValidator
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authRepository: AuthRepository
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "Starting onCreate")
            
            // Set theme programmatically to ensure Material Components compatibility
            setTheme(R.style.Theme_EarningApp)
            
            // Inflate binding with error handling
            try {
                binding = ActivityRegisterBinding.inflate(layoutInflater)
                setContentView(binding.root)
                Log.d(TAG, "Layout inflated successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error inflating layout", e)
                handleError("Error loading registration page layout")
                return
            }

            // Initialize Repository with error handling
            try {
                authRepository = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
                Log.d(TAG, "Repository initialized")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing repository", e)
                handleError("Error initializing registration system: ${e.message}")
                return
            }

            // Setup UI with error handling
            try {
                setupUI()
                Log.d(TAG, "UI setup completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up UI", e)
                handleError("Error setting up user interface")
                return
            }
            
            // Debug Firebase status
            try {
                DebugUtils.checkFirebaseStatus(this)
            } catch (e: Exception) {
                Log.w(TAG, "Error checking Firebase status", e)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Critical error in onCreate", e)
            handleError("Critical error loading registration page: ${e.message}")
        }
    }
    
    private fun handleError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        
        // Try fallback to SimpleRegisterActivity
        try {
            val intent = Intent(this, SimpleRegisterActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to SimpleRegisterActivity", e)
        }
        finish()
    }
    private fun setupUI() {
        try {
            binding.btnRegister.setOnClickListener {
                Log.d(TAG, "Register button clicked")
                validateAndRegister()
            }

            binding.tvLoginLink.setOnClickListener {
                Log.d(TAG, "Login link clicked")
                try {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e(TAG, "Error navigating to Login", e)
                    finish()
                }
            }
            
            Log.d(TAG, "All click listeners set successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in setupUI", e)
        }
    }

    private fun validateAndRegister() {
        try {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            Log.d(TAG, "Validating registration data: name=$name, email=$email")

            // Reset errors
            binding.tilFullName.error = null
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            var isValid = true

            if (name.isEmpty()) {
                binding.tilFullName.error = "Nama tidak boleh kosong"
                isValid = false
            } else if (name.length < 2) {
                binding.tilFullName.error = "Nama minimal 2 karakter"
                isValid = false
            }

            if (email.isEmpty()) {
                binding.tilEmail.error = "Email tidak boleh kosong"
                isValid = false
            } else if (!validateEmail(email)) {
                binding.tilEmail.error = "Format email tidak valid"
                isValid = false
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Password tidak boleh kosong"
                isValid = false
            } else if (password.length < 6) {
                binding.tilPassword.error = "Password minimal 6 karakter"
                isValid = false
            }

            if (confirmPassword.isEmpty()) {
                binding.tilConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
                isValid = false
            } else if (password != confirmPassword) {
                binding.tilConfirmPassword.error = "Password tidak sama"
                isValid = false
            }

            if (isValid) {
                Log.d(TAG, "Validation passed, starting registration")
                DebugUtils.logRegistrationAttempt(email, name)
                performRegistration(email, password, name)
            } else {
                Log.d(TAG, "Validation failed")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in validateAndRegister", e)
            Toast.makeText(this, "Error validating form", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun performRegistration(email: String, password: String, name: String) {
        try {
            // First validate Firebase configuration
            Log.d(TAG, "Validating Firebase configuration before registration")
            val validationResult = FirebaseConfigValidator.validateFirebaseConfiguration(this)
            
            // Don't block registration on configuration issues, just log them
            if (!validationResult.isValid) {
                Log.w(TAG, "Firebase configuration has issues:")
                validationResult.issues.forEach { issue ->
                    Log.w(TAG, issue)
                }
            } else {
                Log.d(TAG, "Firebase configuration is valid")
                validationResult.successes.forEach { success ->
                    Log.d(TAG, success)
                }
            }
            
            // Check Firebase status first
            DebugUtils.checkFirebaseStatus(this)
            
            // Check network connection
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Tidak ada koneksi internet. Periksa koneksi Anda dan coba lagi.", Toast.LENGTH_LONG).show()
                return
            }
            
            Log.d(TAG, "Network available: ${NetworkUtils.getNetworkType(this)}")
            showLoading(true)
            
            lifecycleScope.launch {
                try {
                    Log.d(TAG, "Starting Firebase registration for email: $email")
                    
                    // Skip the problematic Firebase Auth test for now
                    // TODO: Re-enable when Firebase configuration is properly set up
                    /*
                    val authTest = FirebaseConfigValidator.testFirebaseAuth("test@example.com", "testpass123")
                    if (!authTest.success) {
                        showLoading(false)
                        Log.e(TAG, "Firebase Auth test failed: ${authTest.message}")
                        Toast.makeText(this@RegisterActivity, authTest.message, Toast.LENGTH_LONG).show()
                        return@launch
                    }
                    */
                    
                    Log.d(TAG, "Proceeding with registration")
                    
                    // First check if email is already in use
                    try {
                        @Suppress("DEPRECATION")
                        val signInMethods = FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).await()
                        if (signInMethods.signInMethods?.isNotEmpty() == true) {
                            showLoading(false)
                            Toast.makeText(this@RegisterActivity, "Email sudah terdaftar. Silakan gunakan email lain atau login.", Toast.LENGTH_LONG).show()
                            return@launch
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Could not check existing email, proceeding with registration", e)
                    }
                    
                    val result = authRepository.signUp(email, password, name)
                    
                    showLoading(false)
                    
                    when (result) {
                        is Resource.Success -> {
                            Log.d(TAG, "Registration successful for user: ${result.data?.uid}")
                            
                            // Save user info to SharedPreferences
                            try {
                                val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                sharedPrefs.edit().apply {
                                    putString("user_uid", result.data?.uid)
                                    putString("user_email", result.data?.email)
                                    putString("user_name", result.data?.displayName)
                                    putBoolean("is_logged_in", true)
                                    apply()
                                }
                                Log.d(TAG, "User info saved to SharedPreferences")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error saving user to SharedPreferences", e)
                            }
                            
                            Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Selamat datang!", Toast.LENGTH_SHORT).show()
                            
                            try {
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } catch (e: Exception) {
                                Log.e(TAG, "Error navigating to MainActivity", e)
                                finish()
                            }
                        }
                        is Resource.Error -> {
                            Log.e(TAG, "Registration failed: ${result.message}")
                            
                            // Handle specific Firebase errors
                            val errorMessage = when {
                                result.message?.contains("network", ignoreCase = true) == true -> 
                                    "Tidak ada koneksi internet. Periksa koneksi Anda."
                                result.message?.contains("email", ignoreCase = true) == true -> 
                                    "Format email tidak valid atau email sudah terdaftar."
                                result.message?.contains("password", ignoreCase = true) == true -> 
                                    "Password tidak memenuhi persyaratan. Minimal 6 karakter."
                                result.message?.contains("user-disabled", ignoreCase = true) == true -> 
                                    "Akun telah dinonaktifkan. Hubungi administrator."
                                result.message?.contains("operation-not-allowed", ignoreCase = true) == true -> 
                                    "Pendaftaran tidak diizinkan. Hubungi administrator."
                                result.message?.contains("weak-password", ignoreCase = true) == true -> 
                                    "Password terlalu lemah. Gunakan kombinasi huruf, angka, dan simbol."
                                else -> "Registrasi gagal: ${result.message}"
                            }
                            
                            Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                        }
                        is Resource.Loading -> {
                            // Already handled above
                        }
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    Log.e(TAG, "Exception during registration", e)
                    
                    val errorMessage = when {
                        e.message?.contains("network", ignoreCase = true) == true -> 
                            "Tidak ada koneksi internet. Periksa koneksi Anda."
                        e.message?.contains("timeout", ignoreCase = true) == true -> 
                            "Koneksi timeout. Coba lagi dalam beberapa saat."
                        else -> "Terjadi kesalahan: ${e.message}"
                    }
                    
                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in performRegistration", e)
            showLoading(false)
            Toast.makeText(this, "Terjadi kesalahan internal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showLoading(show: Boolean) {
        try {
            binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !show
            
            if (show) {
                binding.btnRegister.text = "Mendaftar..."
            } else {
                binding.btnRegister.text = "Daftar"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in showLoading", e)
        }
    }
}
