package com.komputerkit.earningapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.R
import com.komputerkit.earningapp.data.repository.AuthRepository
import com.komputerkit.earningapp.databinding.ActivityLoginBinding
import com.komputerkit.earningapp.MainActivity
import com.komputerkit.earningapp.SimpleMainActivity
import com.komputerkit.earningapp.utils.Resource
import com.komputerkit.earningapp.utils.CoinManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("LoginActivity", "=== LOGIN ACTIVITY STARTING ===")
        Log.d("LoginActivity", "onCreate() called - Starting LoginActivity initialization")
        Log.d("LoginActivity", "Thread: ${Thread.currentThread().name}")
        Log.d("LoginActivity", "Process ID: ${android.os.Process.myPid()}")
        Log.d("LoginActivity", "Intent extras: ${intent.extras}")
        
        try {
            Log.d("LoginActivity", "Inflating layout...")
            binding = ActivityLoginBinding.inflate(layoutInflater)
            Log.d("LoginActivity", "Layout inflated successfully")
            
            Log.d("LoginActivity", "Setting content view...")
            setContentView(binding.root)
            Log.d("LoginActivity", "Content view set successfully")
            Log.d("LoginActivity", "Layout set successfully")

            // Initialize ViewModel with error handling
            try {
                Log.d("LoginActivity", "Initializing AuthRepository...")
                val authRepository = AuthRepository(
                    FirebaseAuth.getInstance(), 
                    FirebaseFirestore.getInstance()
                )
                Log.d("LoginActivity", "AuthRepository created successfully")
                
                authViewModel = AuthViewModel(authRepository, this)
                Log.d("LoginActivity", "AuthViewModel initialized successfully")
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error initializing ViewModel", e)
                // Try fallback initialization
                try {
                    Log.d("LoginActivity", "Trying fallback AuthViewModel initialization...")
                    authViewModel = AuthViewModel()
                    Log.d("LoginActivity", "Fallback AuthViewModel initialized")
                } catch (e2: Exception) {
                    Log.e("LoginActivity", "Error with fallback ViewModel initialization", e2)
                    Toast.makeText(this, "Error initializing app. Please restart.", Toast.LENGTH_LONG).show()
                    finish()
                    return
                }
            }

            setupUI()
            observeViewModel()
            checkCurrentUser()
            
            Log.d("LoginActivity", "onCreate() completed successfully")
            
        } catch (e: Exception) {
            Log.e("LoginActivity", "Critical error in onCreate", e)
            Toast.makeText(this, "Failed to load login page", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            validateAndLogin()
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        authViewModel.authState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    if (resource.data != null) {
                        // Login successful
                        try {
                            Log.d("LoginActivity", "Login successful for user: ${resource.data.email}")
                            
                            // Initialize coins for the user
                            try {
                                val coinManager = CoinManager.getInstance(this@LoginActivity)
                                lifecycleScope.launch {
                                    try {
                                        coinManager.initializeUserCoins(resource.data.uid)
                                        Log.d("LoginActivity", "User coins initialized successfully")
                                    } catch (e: Exception) {
                                        Log.e("LoginActivity", "Error initializing user coins", e)
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("LoginActivity", "Error setting up coin initialization", e)
                            }
                            
                            // Save user info to SharedPreferences
                            try {
                                val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                                sharedPrefs.edit().apply {
                                    putString("user_uid", resource.data.uid)
                                    putString("user_email", resource.data.email)
                                    putString("user_name", resource.data.displayName)
                                    putBoolean("is_logged_in", true)
                                    apply()
                                }
                                Log.d("LoginActivity", "User info saved to SharedPreferences successfully")
                            } catch (e: Exception) {
                                Log.e("LoginActivity", "Error saving user to SharedPreferences", e)
                            }
                            
                            Log.d("LoginActivity", "Creating intent for MainActivity")
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            
                            Log.d("LoginActivity", "Starting MainActivity with flags: ${intent.flags}")
                            startActivity(intent)
                            
                            Log.d("LoginActivity", "Finishing LoginActivity")
                            finish()
                            
                            Log.d("LoginActivity", "Navigation to MainActivity completed successfully")
                            
                        } catch (e: Exception) {
                            Log.e("LoginActivity", "Error navigating to MainActivity", e)
                            Log.e("LoginActivity", "Exception details: ${e.message}")
                            Log.e("LoginActivity", "Stack trace: ${android.util.Log.getStackTraceString(e)}")
                            
                            // Try fallback to SimpleMainActivity
                            try {
                                Log.d("LoginActivity", "Trying fallback to SimpleMainActivity")
                                val fallbackIntent = Intent(this@LoginActivity, SimpleMainActivity::class.java)
                                fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(fallbackIntent)
                                finish()
                                Log.d("LoginActivity", "Fallback to SimpleMainActivity successful")
                            } catch (e2: Exception) {
                                Log.e("LoginActivity", "Fallback also failed", e2)
                                Toast.makeText(this, "Login berhasil, tetapi gagal membuka halaman utama. Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        // Password reset email sent
                        Toast.makeText(this, "Email reset password telah dikirim", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkCurrentUser() {
        try {
            if (authViewModel.currentUser.value != null) {
                Log.d("LoginActivity", "User is already logged in, navigating to main page")
                
                try {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("LoginActivity", "Error navigating to main page", e)
                    // If navigation fails, give user option to logout and try again
                    showNavigationErrorDialog()
                }
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error checking current user", e)
            // Continue to login screen if check fails
        }
    }
    
    private fun showNavigationErrorDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Navigation Error")
            .setMessage("There was an error loading your account. Would you like to logout and try again?")
            .setPositiveButton("Logout") { _, _ ->
                logoutUser()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun logoutUser() {
        try {
            authViewModel.signOut()
            
            Toast.makeText(this, "Logged out successfully. Please login again.", Toast.LENGTH_SHORT).show()
            
            // Stay on login activity and clear fields
            binding.etEmail.text?.clear()
            binding.etPassword.text?.clear()
            
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error during logout", e)
            Toast.makeText(this, "Error during logout. Please restart the app.", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateAndLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Reset errors
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email tidak boleh kosong"
            isValid = false
        } else if (!authViewModel.validateEmail(email)) {
            binding.tilEmail.error = "Format email tidak valid"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password tidak boleh kosong"
            isValid = false
        } else if (!authViewModel.validatePassword(password)) {
            binding.tilPassword.error = "Password minimal 6 karakter"
            isValid = false
        }

        if (isValid) {
            authViewModel.signIn(email, password)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !show
    }
}
