package com.komputerkit.socialmedia.ui.debug

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.komputerkit.socialmedia.databinding.ActivityDebugBinding
import com.komputerkit.socialmedia.utils.CrashLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class DebugActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        loadDebugInfo()
    }

    private fun setupClickListeners() {
        binding.btnRefresh.setOnClickListener {
            loadDebugInfo()
        }

        binding.btnClearLogs.setOnClickListener {
            CrashLogger.clearLogs(this)
            Toast.makeText(this, "Logs cleared", Toast.LENGTH_SHORT).show()
            loadDebugInfo()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadDebugInfo() {
        val debugInfo = StringBuilder()

        // Firebase info
        try {
            val firebaseApp = FirebaseApp.getInstance()
            debugInfo.append("=== FIREBASE INFO ===\n")
            debugInfo.append("Firebase App Name: ${firebaseApp.name}\n")
            debugInfo.append("Firebase Options: ${firebaseApp.options}\n")
            
            val auth = FirebaseAuth.getInstance()
            debugInfo.append("Firebase Auth Current User: ${auth.currentUser?.uid ?: "No user"}\n")
            debugInfo.append("Firebase Auth App: ${auth.app.name}\n\n")
        } catch (e: Exception) {
            debugInfo.append("=== FIREBASE ERROR ===\n")
            debugInfo.append("Firebase Error: ${e.message}\n\n")
        }

        // App info
        debugInfo.append("=== APP INFO ===\n")
        debugInfo.append("Package Name: ${packageName}\n")
        debugInfo.append("Version Name: ${packageManager.getPackageInfo(packageName, 0).versionName}\n")
        debugInfo.append("Version Code: ${packageManager.getPackageInfo(packageName, 0).versionCode}\n\n")

        // Crash logs
        debugInfo.append("=== CRASH LOGS ===\n")
        debugInfo.append(CrashLogger.readLogs(this))

        binding.tvDebugInfo.text = debugInfo.toString()
    }
}