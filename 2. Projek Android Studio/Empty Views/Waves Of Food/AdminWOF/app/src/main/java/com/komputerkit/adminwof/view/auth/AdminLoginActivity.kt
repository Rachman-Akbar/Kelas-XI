package com.komputerkit.adminwof.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.adminwof.AdminWOFApp
import com.komputerkit.adminwof.databinding.ActivityAdminLoginBinding
import com.komputerkit.adminwof.utils.AuthState
import com.komputerkit.adminwof.view.main.AdminMainActivity
import com.komputerkit.adminwof.viewmodel.AuthViewModel

/**
 * Admin Login Activity dengan validation
 */
class AdminLoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminLoginBinding
    private val authViewModel: AuthViewModel by viewModels { 
        (application as AdminWOFApp).viewModelFactory 
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupListeners()
        observeViewModel()
    }
    
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(email, password)) {
                authViewModel.signIn(email, password)
            }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return false
        }
        
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        }
        
        if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            return false
        }
        
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        return true
    }
    
    private fun observeViewModel() {
        authViewModel.authState.observe(this) { state ->
            when (state) {
                AuthState.Idle -> {
                    showLoading(false)
                }
                AuthState.Loading -> {
                    showLoading(true)
                }
                AuthState.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is AuthState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, AdminMainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
