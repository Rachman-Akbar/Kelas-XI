package com.komputerkit.aplikasimonitoringkelas.siswa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaHomeScreen(
    navController: NavController,
    siswaViewModel: SiswaViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "siswa_home"
    val userKelasName by siswaViewModel.userKelasName.collectAsState()
    
    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
            userName = authRepo.getUserName()
            userRole = authRepo.getUserRole()
        } catch (e: Exception) {
            // Ignore error
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernColors.BackgroundWhite)
    ) {
        AppHeader(
            title = "Dashboard Siswa",
            userName = userName.ifEmpty { userRole },
            userRole = userRole,
            navController = navController,
            onLogout = onLogout,
            showHomeButton = false // No home button on home screen
        )

        StandardPageTitle(
            title = "Selamat Datang",
            icon = Icons.Default.Home
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StandardSectionHeader(title = "Menu Utama")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Jadwal Pelajaran",
                    description = "Lihat jadwal",
                    icon = Icons.Default.CalendarMonth,
                    onClick = { navController.navigate("siswa_schedule") },
                    modifier = Modifier.weight(1f)
                )
                StandardDashboardCard(
                    title = "Kehadiran Siswa",
                    description = "Monitor kehadiran",
                    icon = Icons.Default.PersonSearch,
                    onClick = { navController.navigate("siswa_student_attendance") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Kehadiran Guru",
                    description = "Monitor guru",
                    icon = Icons.Default.Person,
                    onClick = { navController.navigate("siswa_teacher_attendance") },
                    modifier = Modifier.weight(1f)
                )
                StandardDashboardCard(
                    title = "Izin Guru",
                    description = "Lihat izin guru",
                    icon = Icons.Default.EventBusy,
                    onClick = { navController.navigate("siswa_permission") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Guru Pengganti",
                    description = "Lihat pengganti",
                    icon = Icons.Default.SwapHoriz,
                    onClick = { navController.navigate("siswa_substitute") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        SiswaFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }
}