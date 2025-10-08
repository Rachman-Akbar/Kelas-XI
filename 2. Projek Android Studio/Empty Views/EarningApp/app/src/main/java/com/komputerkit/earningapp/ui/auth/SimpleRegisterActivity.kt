package com.komputerkit.earningapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komputerkit.earningapp.R
import com.komputerkit.earningapp.data.repository.AuthRepository
import com.komputerkit.earningapp.MainActivity
import com.komputerkit.earningapp.utils.Resource
import kotlinx.coroutines.launch

class SimpleRegisterActivity : AppCompatActivity() {
    
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnBack: Button
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var authRepository: AuthRepository
    
    private val TAG = "SimpleRegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "Starting SimpleRegisterActivity")
            
            // Set content view with error handling
            try {
                setContentView(R.layout.activity_simple_register)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting content view", e)
                Toast.makeText(this, "Error loading page layout", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            // Initialize Firebase with error handling
            try {
                authRepository = AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing AuthRepository", e)
                Toast.makeText(this, "Error initializing authentication", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            // Initialize views with error handling
            try {
                initViews()
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing views", e)
                Toast.makeText(this, "Error setting up user interface", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            // Setup click listeners with error handling
            try {
                setupClickListeners()
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up click listeners", e)
                Toast.makeText(this, "Error setting up interactions", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            
            Log.d(TAG, "SimpleRegisterActivity setup completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Critical error in onCreate", e)
            Toast.makeText(this, "Critical loading error: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun initViews() {
        try {
            etName = findViewById(R.id.etName)
            etEmail = findViewById(R.id.etEmail)
            etPassword = findViewById(R.id.etPassword)
            etConfirmPassword = findViewById(R.id.etConfirmPassword)
            btnRegister = findViewById(R.id.btnRegister)
            btnBack = findViewById(R.id.btnBack)
            btnLogin = findViewById(R.id.btnLogin)
            progressBar = findViewById(R.id.progressBar)
        } catch (e: Exception) {
            Log.e(TAG, "Error finding views", e)
            throw e // Re-throw to be caught by caller
        }
    }
    
    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            validateAndRegister()
        }
        
        btnBack.setOnClickListener {
            finish()
        }
        
        btnLogin.setOnClickListener {
            try {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Log.e(TAG, "Error navigating to LoginActivity", e)
                Toast.makeText(this, "Navigation error", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun validateAndRegister() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()
        
        if (name.isEmpty()) {
            etName.error = "Nama tidak boleh kosong"
            etName.requestFocus()
            return
        }
        
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            etEmail.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return
        }
        
        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            etPassword.requestFocus()
            return
        }
        
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Konfirmasi password tidak boleh kosong"
            etConfirmPassword.requestFocus()
            return
        }
        
        if (password != confirmPassword) {
            etConfirmPassword.error = "Password tidak sama"
            etConfirmPassword.requestFocus()
            return
        }
        
        performRegistration(email, password, name)
    }
    
    private fun performRegistration(email: String, password: String, name: String) {
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Starting registration for: $email")
                
                val result = authRepository.signUp(email, password, name)
                
                showLoading(false)
                
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Registration successful")
                        Toast.makeText(this@SimpleRegisterActivity, "Registrasi berhasil! Selamat datang!", Toast.LENGTH_SHORT).show()
                        
                        val intent = Intent(this@SimpleRegisterActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    is Resource.Error -> {
                        Log.e(TAG, "Registration error: ${result.message}")
                        
                        val errorMessage = when {
                            result.message?.contains("network", ignoreCase = true) == true -> 
                                "Tidak ada koneksi internet. Periksa koneksi Anda."
                            result.message?.contains("email", ignoreCase = true) == true -> 
                                "Masalah dengan email. Periksa format atau gunakan email lain."
                            result.message?.contains("password", ignoreCase = true) == true -> 
                                "Password tidak memenuhi persyaratan. Minimal 6 karakter."
                            else -> result.message ?: "Registrasi gagal"
                        }
                        
                        Toast.makeText(this@SimpleRegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        // Already handled
                    }
                }
            } catch (e: Exception) {
                showLoading(false)
                Log.e(TAG, "Registration exception", e)
                
                val errorMessage = when {
                    e.message?.contains("network", ignoreCase = true) == true -> 
                        "Tidak ada koneksi internet. Periksa koneksi Anda."
                    e.message?.contains("timeout", ignoreCase = true) == true -> 
                        "Koneksi timeout. Coba lagi dalam beberapa saat."
                    else -> "Terjadi kesalahan: ${e.message}"
                }
                
                Toast.makeText(this@SimpleRegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.isEnabled = !show
        btnRegister.text = if (show) "Mendaftar..." else "Daftar"
    }
}
