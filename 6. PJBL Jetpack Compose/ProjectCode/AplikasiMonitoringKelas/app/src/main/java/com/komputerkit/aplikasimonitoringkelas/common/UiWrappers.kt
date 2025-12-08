package com.komputerkit.aplikasimonitoringkelas.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.ui.components.AppBottomNavBar
import com.komputerkit.aplikasimonitoringkelas.ui.components.AppHeader as UiAppHeader
import com.komputerkit.aplikasimonitoringkelas.ui.components.BottomNavigationItemData
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Add

@Composable
fun AppHeader(
    title: String,
    userName: String = "",
    userRole: String = "",
    navController: NavController? = null,
    onLogout: () -> Unit = {},
    showBackButton: Boolean = false,
    showHomeButton: Boolean = false,
    onHomeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    UiAppHeader(
        title = title,
        userName = userName,
        userRole = userRole,
        navController = navController,
        onLogout = onLogout,
        showBackButton = showBackButton,
        showHomeButton = showHomeButton,
        onHomeClick = onHomeClick,
        modifier = modifier
    )
}

@Composable
fun GuruFooter(navController: NavController, currentRoute: String) {
    AppBottomNavBar(
        navController = navController,
        items = listOf(
            BottomNavigationItemData("guru_schedule", "Jadwal", Icons.Default.CalendarToday),
            BottomNavigationItemData("guru_attendance", "Kehadiran", Icons.Default.People),
            BottomNavigationItemData("guru_permission", "Izin", Icons.Default.EventNote),
            BottomNavigationItemData("guru_substitute", "Pengganti", Icons.Default.SwapHoriz)
        )
    )
}

@Composable
fun SiswaFooter(navController: NavController, currentRoute: String) {
    AppBottomNavBar(
        navController = navController,
        items = listOf(
            BottomNavigationItemData("siswa_schedule", "Jadwal", Icons.Default.CalendarToday),
            BottomNavigationItemData("siswa_student_attendance", "Siswa", Icons.Default.Group),
            BottomNavigationItemData("siswa_teacher_attendance", "Guru", Icons.Default.Person),
            BottomNavigationItemData("siswa_permission", "Izin", Icons.Default.EventNote),
            BottomNavigationItemData("siswa_substitute_teacher", "Pengganti", Icons.Default.SwapHoriz)
        )
    )
}

@Composable
fun KurikulumFooter(navController: NavController, currentRoute: String) {
    AppBottomNavBar(
        navController = navController,
        items = listOf(
            BottomNavigationItemData("kurikulum_schedule", "Jadwal", Icons.Default.CalendarToday),
            BottomNavigationItemData("kurikulum_attendance", "Kehadiran", Icons.Default.CheckCircle),
            BottomNavigationItemData("kurikulum_permission", "Izin", Icons.Default.EventNote),
            BottomNavigationItemData("kurikulum_substitute", "Pengganti", Icons.Default.SwapHoriz),
            BottomNavigationItemData("kurikulum_add_attendance", "Input", Icons.Default.Add)
        )
    )
}

@Composable
fun KepsekFooter(navController: NavController, currentRoute: String) {
    AppBottomNavBar(
        navController = navController,
        items = listOf(
            BottomNavigationItemData("kepsek_schedule", "Jadwal", Icons.Default.CalendarToday),
            BottomNavigationItemData("kepsek_attendance", "Kehadiran", Icons.Default.CheckCircle),
            BottomNavigationItemData("kepsek_student_attendance", "Siswa", Icons.Default.Group),
            BottomNavigationItemData("kepsek_substitute", "Pengganti", Icons.Default.SwapHoriz),
            BottomNavigationItemData("kepsek_permission", "Izin", Icons.Default.EventNote)
        )
    )
}

// Note: user status footer removed to keep UI minimal. Bottom navigation provides navigation.
