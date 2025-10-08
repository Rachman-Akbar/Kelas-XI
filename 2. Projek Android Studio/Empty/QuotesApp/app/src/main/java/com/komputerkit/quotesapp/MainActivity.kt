package com.komputerkit.quotesapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    
    private lateinit var tvQuote: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var btnShare: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Hide action bar
        supportActionBar?.hide()
        
        // Set status bar color (simplified)
        setupStatusBar()
        
        // Initialize views
        initializeViews()
        
        // Set up click listeners
        setupClickListeners()
    }
    
    private fun setupStatusBar() {
        // Status bar customization is optional for this simple app
        // Keeping minimal implementation to avoid deprecated warnings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
    }
    
    private fun initializeViews() {
        tvQuote = findViewById(R.id.tv_quote)
        tvAuthor = findViewById(R.id.tv_author)
        btnShare = findViewById(R.id.btn_share)
    }
    
    private fun setupClickListeners() {
        btnShare.setOnClickListener {
            shareQuote()
        }
    }
    
    private fun shareQuote() {
        // Get quote text from TextView
        val quoteText = tvQuote.text.toString()
        val authorText = tvAuthor.text.toString()
        val fullQuote = "$quoteText\n\n$authorText"
        
        // Create share intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, fullQuote)
            putExtra(Intent.EXTRA_SUBJECT, "Inspirational Quote")
        }
        
        // Start activity with chooser
        val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title))
        startActivity(chooserIntent)
    }
}