package com.komputerkit.socialmedia.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.komputerkit.socialmedia.MainActivity
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.data.model.Result
import com.komputerkit.socialmedia.databinding.ActivityLoginBinding
import com.komputerkit.socialmedia.utils.CrashLogger
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            CrashLogger.logInfo(this, "LoginActivity onCreate started")
            
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            CrashLogger.logInfo(this, "LoginActivity binding and content view set")

            authManager = AuthManager()
            CrashLogger.logInfo(this, "AuthManager initialized")

            // Check if user is already logged in
            if (authManager.isUserLoggedIn) {
                CrashLogger.logInfo(this, "User already logged in, navigating to main")
                navigateToMain()
                return
            }

            setupClickListeners()
            CrashLogger.logInfo(this, "LoginActivity onCreate completed successfully")
        } catch (e: Exception) {
            CrashLogger.logError(this, "Error in LoginActivity onCreate", e)
            e.printStackTrace()
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnLogin.setOnClickListener {
                loginUser()
            }

            tvForgotPassword.setOnClickListener {
                forgotPassword()
            }

            tvSignup.setOnClickListener {
                navigateToRegister()
            }

            btnDebug.setOnClickListener {
                navigateToDebug()
            }
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validateInput(email, password)) {
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            val result = authManager.loginUser(email, password)
            
            showLoading(false)
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is Result.Error -> {
                    showError("Login failed: ${result.exception.message}")
                }
                is Result.Loading -> {
                    // Already handled
                }
            }
        }
    }

    private fun forgotPassword() {
        val email = binding.etEmail.text.toString().trim()
        
        if (email.isEmpty()) {
            binding.tilEmail.error = "Please enter your email first"
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            val result = authManager.sendPasswordResetEmail(email)
            
            showLoading(false)
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Password reset email sent to $email",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is Result.Error -> {
                    showError("Failed to send reset email: ${result.exception.message}")
                }
                is Result.Loading -> {
                    // Already handled
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        return isValid
    }

    private fun showLoading(show: Boolean) {
        binding.apply {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            btnLogin.isEnabled = !show
            etEmail.isEnabled = !show
            etPassword.isEnabled = !show
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToDebug() {
        val intent = Intent(this, com.komputerkit.socialmedia.ui.debug.DebugActivity::class.java)
        startActivity(intent)
    }
}