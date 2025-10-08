package com.komputerkit.blogapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.komputerkit.blogapp.databinding.ActivitySplashBinding
import com.komputerkit.blogapp.ui.home.HomeActivity
import com.komputerkit.blogapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Hide action bar
        supportActionBar?.hide()
        
        lifecycleScope.launch {
            // Delay 3 detik
            delay(3000)
            
            // Cek status autentikasi dan navigasi
            if (viewModel.isUserLoggedIn()) {
                navigateToHome()
            } else {
                navigateToWelcome()
            }
        }
    }
    
    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
    
    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}