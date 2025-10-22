package com.komputerkit.wavesoffood.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.wavesoffood.WavesOfFoodApp
import com.komputerkit.wavesoffood.databinding.ActivityProfileBinding
import com.komputerkit.wavesoffood.view.auth.AuthActivity
import com.komputerkit.wavesoffood.view.order.OrdersActivity
import com.komputerkit.wavesoffood.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels { (application as WavesOfFoodApp).viewModelFactory }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupListeners()
        observeViewModel()
        
        // Fetch user profile
        profileViewModel.fetchUserProfile()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
    
    private fun setupListeners() {
        binding.btnSaveAddress.setOnClickListener {
            val newAddress = binding.etAddress.text.toString().trim()
            
            if (newAddress.isEmpty()) {
                binding.tilAddress.error = "Address is required"
                return@setOnClickListener
            }
            
            binding.tilAddress.error = null
            profileViewModel.updateAddress(newAddress)
        }
        
        binding.btnViewOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }
        
        binding.btnSignOut.setOnClickListener {
            showSignOutDialog()
        }
    }
    
    private fun observeViewModel() {
        profileViewModel.user.observe(this) { user ->
            user?.let {
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
                binding.etAddress.setText(it.address)
            }
        }
        
        profileViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSaveAddress.isEnabled = !isLoading
        }
        
        profileViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
        
        profileViewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Address updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showSignOutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                profileViewModel.signOut()
                
                // Navigate to AuthActivity
                val intent = Intent(this, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
