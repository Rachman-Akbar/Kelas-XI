package com.komputerkit.aplikasimonitoringkelas.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.AppHeader
import com.komputerkit.aplikasimonitoringkelas.common.GuruFooter
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruHomeScreen(
    navController: NavController,
    guruViewModel: GuruViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "guru_home"
    
    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernColors.BackgroundWhite)
    ) {
        LaunchedEffect(Unit) {
            try {
                val authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
                userName = authRepo.getUserName()
                userRole = authRepo.getUserRole()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        AppHeader(
            title = "Dashboard Guru",
            userName = userName.ifEmpty { userRole },
            userRole = userRole,
            navController = navController,
            onLogout = onLogout,
            showHomeButton = false // No home button on home screen
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Menu Utama
            StandardSectionHeader(title = "Menu Utama")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Jadwal Mengajar",
                    description = "Lihat jadwal",
                    icon = Icons.Default.CalendarToday,
                    onClick = { navController.navigate("guru_schedule") },
                    modifier = Modifier.weight(1f)
                )
                StandardDashboardCard(
                    title = "Kehadiran Saya",
                    description = "Riwayat kehadiran",
                    icon = Icons.Default.People,
                    onClick = { navController.navigate("guru_attendance") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Izin Guru",
                    description = "Kelola izin",
                    icon = Icons.Default.EventNote,
                    onClick = { navController.navigate("guru_permission") },
                    modifier = Modifier.weight(1f)
                )
                StandardDashboardCard(
                    title = "Guru Pengganti",
                    description = "Lihat pengganti",
                    icon = Icons.Default.SwapHoriz,
                    onClick = { navController.navigate("guru_substitute") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Ajukan Guru Pengganti",
                    description = "Buat permintaan",
                    icon = Icons.Default.PersonAdd,
                    onClick = { navController.navigate("guru_substitute_entry") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        GuruFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }
}

