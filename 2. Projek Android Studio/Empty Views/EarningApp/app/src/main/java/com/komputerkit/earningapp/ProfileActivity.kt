package com.komputerkit.earningapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.earningapp.ui.auth.LoginActivity

class ProfileActivity : BaseActivity() {

    private lateinit var etProfileName: EditText
    private lateinit var etProfileEmail: EditText
    private lateinit var etProfilePhone: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Hide action bar
        supportActionBar?.hide()

        initViews()
        setupHeader()
        setupFooter()
        setupClickListeners()
        loadUserData()
        updateUserInfo()
    }

    private fun initViews() {
        etProfileName = findViewById(R.id.etProfileName)
        etProfileEmail = findViewById(R.id.etProfileEmail)
        etProfilePhone = findViewById(R.id.etProfilePhone)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        btnLogout = findViewById(R.id.btnLogout)
        tvUserName = findViewById(R.id.tvUserName)
    }

    private fun updateUserInfo() {
        try {
            // Get username from Firebase Auth or SharedPreferences
            val currentUser = firebaseAuth.currentUser
            val userName = if (currentUser != null) {
                currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User"
            } else {
                sharedPreferences.getString("user_name", null) 
                    ?: sharedPreferences.getString("user_email", "")?.substringBefore("@") 
                    ?: "User"
            }
            
            tvUserName?.text = userName
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error updating user info", e)
            tvUserName?.text = "User"
        }
    }

    private fun setupClickListeners() {
        btnUpdateProfile.setOnClickListener {
            updateProfile()
        }
        
        btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserData() {
        try {
            // Load user data from Firebase or SharedPreferences
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Load from Firebase
                etProfileName.setText(currentUser.displayName ?: "")
                etProfileEmail.setText(currentUser.email ?: "")
                etProfilePhone.setText("") // Phone not available from Firebase Auth
            } else {
                // Load from SharedPreferences
                etProfileName.setText(sharedPreferences.getString("user_name", ""))
                etProfileEmail.setText(sharedPreferences.getString("user_email", ""))
                etProfilePhone.setText(sharedPreferences.getString("user_phone", ""))
            }
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error loading user data", e)
            // Set default values if loading fails
            etProfileName.setText("")
            etProfileEmail.setText("")
            etProfilePhone.setText("")
        }
    }

    private fun updateProfile() {
        val name = etProfileName.text.toString().trim()
        val email = etProfileEmail.text.toString().trim()
        val phone = etProfilePhone.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nama dan email harus diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Save to SharedPreferences
            sharedPreferences.edit().apply {
                putString("user_name", name)
                putString("user_email", email)
                putString("user_phone", phone)
                apply()
            }
            
            // Update Firebase profile if logged in
            firebaseAuth.currentUser?.let { user ->
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                
                user.updateProfile(profileUpdates)
                    .addOnSuccessListener {
                        Log.d("ProfileActivity", "Firebase profile updated successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.e("ProfileActivity", "Error updating Firebase profile", e)
                    }
            }
            
            Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error updating profile", e)
            Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    private fun performLogout() {
        try {
            // Sign out from Firebase
            firebaseAuth.signOut()
            
            // Clear SharedPreferences
            sharedPreferences.edit().apply {
                remove("user_uid")
                remove("user_email")
                remove("user_name")
                remove("user_phone")
                remove("profile_image_url")
                putBoolean("is_logged_in", false)
                apply()
            }
            
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()
            
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error during logout", e)
            Toast.makeText(this, "Gagal logout", Toast.LENGTH_SHORT).show()
        }
    }
}
