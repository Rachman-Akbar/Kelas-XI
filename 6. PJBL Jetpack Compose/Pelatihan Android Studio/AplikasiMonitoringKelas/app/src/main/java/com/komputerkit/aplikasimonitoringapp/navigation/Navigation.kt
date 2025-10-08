package com.komputerkit.aplikasimonitoringapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Schedule)
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Jadwal : Screen("jadwal", "Jadwal", Icons.Default.Schedule)
    object Entry : Screen("entry", "Entry", Icons.Default.Create)
    object List : Screen("list", "List", Icons.AutoMirrored.Filled.List)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Jadwal,
    Screen.Entry,
    Screen.List
)