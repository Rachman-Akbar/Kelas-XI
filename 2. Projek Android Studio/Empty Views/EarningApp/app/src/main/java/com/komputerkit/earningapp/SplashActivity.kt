package com.komputerkit.earningapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.earningapp.ui.auth.LoginActivity

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"
    private val SPLASH_DELAY = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "=== SPLASH ACTIVITY STARTED ===")
        
        try {
            // Set simple layout or no layout for splash
            setContentView(R.layout.activity_splash)
            
            // Hide action bar
            supportActionBar?.hide()
            
            Log.d(TAG, "Starting splash delay timer")
            
            // Delay then check authentication and navigate
            Handler(Looper.getMainLooper()).postDelayed({
                checkAuthAndNavigate()
            }, SPLASH_DELAY)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            // If there's any error, go directly to login
            navigateToLogin()
        }
    }
    
    private fun checkAuthAndNavigate() {
        try {
            Log.d(TAG, "Checking authentication status")
            
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser
            
            if (currentUser != null) {
                Log.d(TAG, "User is logged in: ${currentUser.email}")
                // User is logged in, go to MainActivity
                navigateToMain()
            } else {
                Log.d(TAG, "User is not logged in, going to LoginActivity")
                // User is not logged in, go to LoginActivity
                navigateToLogin()
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking authentication", e)
            // If there's any error, go to login
            navigateToLogin()
        }
    }
    
    private fun navigateToMain() {
        try {
            Log.d(TAG, "Navigating to MainActivity")
            
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            startActivity(intent)
            finish()
            
            Log.d(TAG, "Navigation to MainActivity completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error navigating to MainActivity", e)
            
            // Try fallback to SimpleMainActivity
            try {
                Log.d(TAG, "Trying fallback to SimpleMainActivity")
                val fallbackIntent = Intent(this, SimpleMainActivity::class.java)
                fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(fallbackIntent)
                finish()
                Log.d(TAG, "Fallback to SimpleMainActivity successful")
            } catch (e2: Exception) {
                Log.e(TAG, "Fallback also failed, going to login", e2)
                // If both fail, go to login
                navigateToLogin()
            }
        }
    }
    
    private fun navigateToLogin() {
        try {
            Log.d(TAG, "Navigating to LoginActivity")
            
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            startActivity(intent)
            finish()
            
            Log.d(TAG, "Navigation to LoginActivity completed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Critical error: Cannot navigate to LoginActivity", e)
            // As last resort, just finish this activity
            finish()
        }
    }
    
    override fun onBackPressed() {
        // Disable back button during splash
        // Do nothing
    }
}
