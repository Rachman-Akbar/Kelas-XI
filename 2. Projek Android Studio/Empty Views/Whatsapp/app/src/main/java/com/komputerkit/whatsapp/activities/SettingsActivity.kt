package com.komputerkit.whatsapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.komputerkit.whatsapp.R
import com.komputerkit.whatsapp.databinding.ActivitySettingsBinding
import com.komputerkit.whatsapp.models.User
import com.komputerkit.whatsapp.utils.CircularTransformation

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    
    private var selectedImageUri: Uri? = null
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                binding.ivProfilePic.setImageURI(uri)
                uploadProfilePicture(uri)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        
        setupToolbar()
        loadUserData()
        setupClickListeners()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun loadUserData() {
        auth.currentUser?.let { user ->
            database.reference.child("users").child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(User::class.java)
                        userData?.let {
                            binding.tvUsername.text = it.username
                            binding.tvEmail.text = it.email
                            binding.etUsername.setText(it.username)
                            binding.etStatus.setText(it.status)
                            
                            if (it.profilePic.isNotEmpty()) {
                                Glide.with(this@SettingsActivity)
                                    .load(it.profilePic)
                                    .transform(CircularTransformation())
                                    .placeholder(R.drawable.ic_person)
                                    .into(binding.ivProfilePic)
                            }
                        }
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@SettingsActivity, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
    
    private fun setupClickListeners() {
        binding.btnChangeProfilePic.setOnClickListener {
            openImagePicker()
        }
        
        binding.btnUpdateStatus.setOnClickListener {
            updateStatus()
        }
        
        binding.btnUpdateUsername.setOnClickListener {
            updateUsername()
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }
    
    private fun uploadProfilePicture(imageUri: Uri) {
        auth.currentUser?.let { user ->
            showLoading(true)
            
            val storageRef = storage.reference.child("profile_pictures/${user.uid}.jpg")
            
            storageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        updateProfilePictureUrl(downloadUri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    showLoading(false)
                    Toast.makeText(this, "Failed to upload image: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
    
    private fun updateProfilePictureUrl(imageUrl: String) {
        auth.currentUser?.let { user ->
            database.reference.child("users").child(user.uid).child("profilePic")
                .setValue(imageUrl)
                .addOnCompleteListener { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profile picture updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    
    private fun updateStatus() {
        val newStatus = binding.etStatus.text.toString().trim()
        if (newStatus.isEmpty()) {
            binding.tilStatus.error = "Status cannot be empty"
            return
        }
        
        auth.currentUser?.let { user ->
            showLoading(true)
            
            database.reference.child("users").child(user.uid).child("status")
                .setValue(newStatus)
                .addOnCompleteListener { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Status updated successfully!", Toast.LENGTH_SHORT).show()
                        binding.tilStatus.error = null
                    } else {
                        Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    
    private fun updateUsername() {
        val newUsername = binding.etUsername.text.toString().trim()
        if (newUsername.isEmpty()) {
            binding.tilUsername.error = "Username cannot be empty"
            return
        }
        
        auth.currentUser?.let { user ->
            showLoading(true)
            
            database.reference.child("users").child(user.uid).child("username")
                .setValue(newUsername)
                .addOnCompleteListener { task ->
                    showLoading(false)
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Username updated successfully!", Toast.LENGTH_SHORT).show()
                        binding.tilUsername.error = null
                        binding.tvUsername.text = newUsername
                    } else {
                        Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}