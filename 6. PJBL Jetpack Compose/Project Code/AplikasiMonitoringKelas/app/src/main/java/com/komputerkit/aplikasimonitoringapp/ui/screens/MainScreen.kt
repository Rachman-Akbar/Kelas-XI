package com.komputerkit.aplikasimonitoringapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.komputerkit.aplikasimonitoringapp.navigation.Screen
import com.komputerkit.aplikasimonitoringapp.ui.components.BottomNavigationBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }

    if (!isLoggedIn) {
        // Login screen sudah dipindahkan ke MainActivity
        // File ini untuk navigation setelah login
        // TODO: Implement proper navigation after login
    } else {
        MainScreenWithNavigation(navController = navController)
    }
}

@Composable
fun MainScreenWithNavigation(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Jadwal.route) {
                JadwalPage()
            }
            composable(Screen.Entry.route) {
                EntryScreen()
            }
            composable(Screen.List.route) {
                ListScreen()
            }
        }
    }
}