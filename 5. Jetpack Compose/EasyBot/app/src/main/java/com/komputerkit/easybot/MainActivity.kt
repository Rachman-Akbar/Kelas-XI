package com.komputerkit.easybot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.komputerkit.easybot.ui.screen.ChatScreen
import com.komputerkit.easybot.ui.theme.EasyBotTheme
import com.komputerkit.easybot.utils.QuickAPITester

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Test API key saat aplikasi dimulai
        QuickAPITester.testAPIKeyOnStartup()
        
        setContent {
            EasyBotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen()
                }
            }
        }
    }
}