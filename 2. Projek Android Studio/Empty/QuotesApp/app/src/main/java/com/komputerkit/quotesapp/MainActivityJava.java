package com.komputerkit.quotesapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivityJava extends AppCompatActivity {
    
    private TextView tvQuote;
    private TextView tvAuthor;
    private Button btnShare;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        
        // Set status bar color (simplified)
        setupStatusBar();
        
        // Initialize views
        initializeViews();
        
        // Set up click listeners
        setupClickListeners();
    }
    
    private void setupStatusBar() {
        // Status bar customization is optional for this simple app
        // Keeping minimal implementation to avoid deprecated warnings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }
    
    private void initializeViews() {
        tvQuote = findViewById(R.id.tv_quote);
        tvAuthor = findViewById(R.id.tv_author);
        btnShare = findViewById(R.id.btn_share);
    }
    
    private void setupClickListeners() {
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQuote();
            }
        });
    }
    
    private void shareQuote() {
        // Get quote text from TextView
        String quoteText = tvQuote.getText().toString();
        String authorText = tvAuthor.getText().toString();
        String fullQuote = quoteText + "\n\n" + authorText;
        
        // Create share intent
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, fullQuote);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Inspirational Quote");
        
        // Start activity with chooser
        Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
        startActivity(chooserIntent);
    }
}