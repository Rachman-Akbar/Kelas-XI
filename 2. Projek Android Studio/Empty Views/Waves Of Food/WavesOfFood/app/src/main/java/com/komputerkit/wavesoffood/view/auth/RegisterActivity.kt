package com.komputerkit.wavesoffood.view.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.wavesoffood.MainActivity
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.databinding.ActivityRegisterBinding
import com.komputerkit.wavesoffood.utils.AuthState
import com.komputerkit.wavesoffood.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupListeners()
        observeViewModel()
    }
    
    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            
            if (validateInput(name, email, password, address)) {
                authViewModel.signUpWithEmail(email, password, name)
            }
        }
        
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun validateInput(name: String, email: String, password: String, address: String): Boolean {
        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            return false
        }
        
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
        
        if (address.isEmpty()) {
            binding.tilAddress.error = "Address is required"
            return false
        }
        
        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilAddress.error = null
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
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
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
        binding.btnRegister.isEnabled = !isLoading
    }
}
