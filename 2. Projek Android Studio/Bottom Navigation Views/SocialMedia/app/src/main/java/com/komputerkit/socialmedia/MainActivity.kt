package com.komputerkit.socialmedia

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.komputerkit.socialmedia.data.manager.AuthManager
import com.komputerkit.socialmedia.databinding.ActivityMainBinding
import com.komputerkit.socialmedia.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            authManager = AuthManager()

            // Check if user is logged in
            if (!authManager.isUserLoggedIn) {
                navigateToLogin()
                return
            }

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            hideActionBar()
            setupNavigation()
            setupFab()
        } catch (e: Exception) {
            e.printStackTrace()
            // If there's an error, go to login
            navigateToLogin()
        }
    }
    
    private fun setupNavigation() {
        try {
            val navView: BottomNavigationView = binding.navView
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            
            // Setup Bottom Navigation with NavController - No ActionBar needed
            navView.setupWithNavController(navController)
            
            // Set custom navigation listener to handle navigation properly
            navView.setOnItemSelectedListener { item ->
                try {
                    when (item.itemId) {
                        R.id.navigation_home -> {
                            if (navController.currentDestination?.id != R.id.navigation_home) {
                                navController.navigate(R.id.navigation_home)
                            }
                            true
                        }
                        R.id.navigation_dashboard -> {
                            if (navController.currentDestination?.id != R.id.navigation_dashboard) {
                                navController.navigate(R.id.navigation_dashboard)
                            }
                            true
                        }
                        R.id.navigation_notifications -> {
                            if (navController.currentDestination?.id != R.id.navigation_notifications) {
                                navController.navigate(R.id.navigation_notifications)
                            }
                            true
                        }
                        R.id.navigation_profile -> {
                            if (navController.currentDestination?.id != R.id.navigation_profile) {
                                navController.navigate(R.id.navigation_profile)
                            }
                            true
                        }
                        else -> false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Navigation failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Navigation setup failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupFab() {
        try {
            binding.fabUpload.setOnClickListener {
                showUploadOptionsDialog()
            }
            
            // Setup header interactions
            binding.ivHeaderHeart.setOnClickListener {
                // Navigate to notifications tab
                binding.navView.selectedItemId = R.id.navigation_notifications
            }
            
            binding.ivHeaderDm.setOnClickListener {
                Toast.makeText(this, "Direct Messages coming soon!", Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun showUploadOptionsDialog() {
        val options = arrayOf("Upload Post", "Upload Story")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Choose Upload Type")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> navigateToUpload()
                1 -> navigateToUploadStory()
            }
        }
        builder.show()
    }
    
    private fun navigateToUpload() {
        val intent = Intent(this, com.komputerkit.socialmedia.ui.upload.UploadPostActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToUploadStory() {
        // For now, we'll use the same upload activity but with story flag
        val intent = Intent(this, com.komputerkit.socialmedia.ui.upload.UploadPostActivity::class.java)
        intent.putExtra("upload_type", "story")
        startActivity(intent)
    }
    
    private fun hideActionBar() {
        // Hide the ActionBar/Toolbar for cleaner look
        supportActionBar?.hide()
    }
}