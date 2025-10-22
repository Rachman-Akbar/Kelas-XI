package com.komputerkit.wavesoffood.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.wavesoffood.MainActivity
import com.komputerkit.wavesoffood.databinding.ActivitySplashBinding
import com.komputerkit.wavesoffood.view.auth.AuthActivity

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Delay 2 seconds before checking auth
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthState()
        }, 2000)
    }
    
    private fun checkAuthState() {
        val currentUser = auth.currentUser
        
        if (currentUser != null) {
            // User is logged in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not logged in, go to AuthActivity
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }
}
