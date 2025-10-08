package com.komputerkit.whatsapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.whatsapp.MainActivity
import com.komputerkit.whatsapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        auth = FirebaseAuth.getInstance()
        
        // Delay for 2 seconds then check authentication status
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthenticationStatus()
        }, 2000)
    }
    
    private fun checkAuthenticationStatus() {
        val currentUser = auth.currentUser
        
        if (currentUser != null) {
            // User is signed in, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not signed in, go to SignInActivity
            startActivity(Intent(this, SignInActivity::class.java))
        }
        
        finish()
    }
}