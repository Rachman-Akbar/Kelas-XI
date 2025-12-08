package com.komputerkit.aplikasimonitoringkelas.kurikulum

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
fun KurikulumHomeScreen(
    navController: NavController,
    kurikulumViewModel: KurikulumViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "kurikulum_home"
    
    // Get user info from DataStore
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
            title = "Dashboard Kurikulum",
            userName = userName.ifEmpty { userRole },
            userRole = userRole,
            navController = navController,
            onLogout = onLogout
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
            // Menu Section
            StandardSectionHeader(title = "Menu Utama")
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Izin Guru",
                    description = "Kelola izin guru",
                    icon = Icons.Default.Event,
                    onClick = { navController.navigate("kurikulum_permission") },
                    modifier = Modifier.weight(1f)
                )
                StandardDashboardCard(
                    title = "Kehadiran Guru",
                    description = "Monitor kehadiran",
                    icon = Icons.Default.People,
                    onClick = { navController.navigate("kurikulum_attendance") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardDashboardCard(
                    title = "Guru Pengganti",
                    description = "Kelola pengganti",
                    icon = Icons.Default.PersonAddAlt1,
                    onClick = { navController.navigate("kurikulum_substitute") },
                    modifier = Modifier.weight(1f)
                )
                StandardDashboardCard(
                    title = "Jadwal Mengajar",
                    description = "Lihat jadwal",
                    icon = Icons.Default.CalendarToday,
                    onClick = { navController.navigate("kurikulum_schedule") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        KurikulumFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }
}
