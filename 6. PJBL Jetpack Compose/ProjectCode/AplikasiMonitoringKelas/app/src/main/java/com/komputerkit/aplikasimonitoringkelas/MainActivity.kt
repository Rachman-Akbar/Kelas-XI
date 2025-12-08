package com.komputerkit.aplikasimonitoringkelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthViewModel
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthViewModelFactory
import com.komputerkit.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigationWithLogout(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

// Wrapper function for navigation with modifier
@Composable
fun AppNavigationWithModifier(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    AppNavigationWithLogout(modifier = modifier, authViewModel = authViewModel)
}

// Main navigation function with logout capability
@Composable
fun AppNavigationWithLogout(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel
) {
    AppNavigation(
        modifier = modifier,
        authViewModel = authViewModel
    )
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AplikasiMonitoringKelasTheme {
        // Note: Preview won't work properly with AndroidViewModel
        // This is just a placeholder for preview purposes
    }
}