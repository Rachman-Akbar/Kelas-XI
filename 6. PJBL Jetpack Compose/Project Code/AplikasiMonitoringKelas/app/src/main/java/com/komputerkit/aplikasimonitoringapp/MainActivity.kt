package com.komputerkit.aplikasimonitoringapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import com.komputerkit.aplikasimonitoringapp.ui.login.LoginScreen
import com.komputerkit.aplikasimonitoringapp.ui.theme.AplikasiMonitoringAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen { role ->
                        navigateToRoleActivity(role)
                    }
                }
            }
        }
    }

    private fun navigateToRoleActivity(role: String) {
        val intent = when (role.lowercase()) {
            "siswa" -> Intent(this, SiswaActivity::class.java)
            "kurikulum" -> Intent(this, KurikulumActivity::class.java)
            "kepala_sekolah" -> Intent(this, KepalaSekolahActivity::class.java)
            "admin" -> Intent(this, AdminActivity::class.java)
            else -> Intent(this, SiswaActivity::class.java) // default
        }
        startActivity(intent)
        finish()
    }
}