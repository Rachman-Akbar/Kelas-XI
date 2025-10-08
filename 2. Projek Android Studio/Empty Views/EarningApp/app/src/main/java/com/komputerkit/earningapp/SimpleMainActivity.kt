package com.komputerkit.earningapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Emergency Simple MainActivity
 * Use this if the main MainActivity has issues
 */
class SimpleMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d("SimpleMainActivity", "Simple MainActivity started successfully")
        
        // Create a simple layout programmatically
        val textView = TextView(this).apply {
            text = "Welcome to EarningApp!\n\nLogin berhasil!\nHalaman utama sederhana ini menunjukkan bahwa navigasi setelah login berfungsi dengan baik."
            textSize = 16f
            setPadding(50, 100, 50, 50)
        }
        
        setContentView(textView)
        
        supportActionBar?.title = "EarningApp - Main"
        
        Log.d("SimpleMainActivity", "Simple MainActivity setup completed")
    }
}
