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
import com.komputerkit.socialmedia.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityRegisterBinding.inflate(layoutInflater)
            setContentView(binding.root)

            authManager = AuthManager()
            
            setupClickListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnRegister.setOnClickListener {
                registerUser()
            }

            tvLogin.setOnClickListener {
                navigateToLogin()
            }
        }
    }

    private fun registerUser() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (!validateInput(username, email, password, confirmPassword)) {
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            val result = authManager.registerUser(email, password, username)
            
            showLoading(false)
            
            when (result) {
                is Result.Success -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Account created successfully! Welcome ${result.data.username}!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToMain()
                }
                is Result.Error -> {
                    showError("Registration failed: ${result.exception.message}")
                }
                is Result.Loading -> {
                    // Already handled
                }
            }
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Username validation
        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        } else if (username.length < 3) {
            binding.tilUsername.error = "Username must be at least 3 characters"
            isValid = false
        } else if (!username.matches(Regex("^[a-zA-Z0-9._]+$"))) {
            binding.tilUsername.error = "Username can only contain letters, numbers, dots, and underscores"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }

        // Email validation
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        // Password validation
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Confirm password validation
        if (confirmPassword.isEmpty()) {
            binding.tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }

    private fun showLoading(show: Boolean) {
        binding.apply {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            btnRegister.isEnabled = !show
            etUsername.isEnabled = !show
            etEmail.isEnabled = !show
            etPassword.isEnabled = !show
            etConfirmPassword.isEnabled = !show
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

    private fun navigateToLogin() {
        finish() // Just go back to login activity
    }
}