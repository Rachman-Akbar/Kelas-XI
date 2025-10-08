package com.komputerkit.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.komputerkit.whatsapp.MainActivity
import com.komputerkit.whatsapp.databinding.ActivitySignUpBinding
import com.komputerkit.whatsapp.models.User

class SignUpActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            signUpUser()
        }
        
        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }
    
    private fun signUpUser() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (!validateInput(username, email, password)) {
            return
        }
        
        showLoading(true)
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { firebaseUser ->
                        val userModel = User(
                            uid = firebaseUser.uid,
                            username = username,
                            email = email,
                            profilePic = "",
                            status = "Available"
                        )
                        
                        // Save user data to Realtime Database
                        database.reference.child("users").child(firebaseUser.uid)
                            .setValue(userModel)
                            .addOnCompleteListener { dbTask ->
                                showLoading(false)
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to save user data: ${dbTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    
    private fun validateInput(username: String, email: String, password: String): Boolean {
        var isValid = true
        
        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        } else {
            binding.tilUsername.error = null
        }
        
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
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
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSignUp.isEnabled = !show
    }
}