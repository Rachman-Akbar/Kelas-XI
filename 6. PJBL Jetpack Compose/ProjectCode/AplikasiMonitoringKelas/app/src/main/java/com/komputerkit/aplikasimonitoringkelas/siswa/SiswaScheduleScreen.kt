package com.komputerkit.aplikasimonitoringkelas.siswa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaScheduleScreen(
    navController: NavController,
    siswaViewModel: SiswaViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "siswa_schedule"
    val schedules by siswaViewModel.schedules.collectAsState()
    val userKelasId by siswaViewModel.userKelasId.collectAsState()
    val userKelasName by siswaViewModel.userKelasName.collectAsState()
    val dayFilter by siswaViewModel.dayFilter.collectAsState()
    val isLoading by siswaViewModel.isLoading.collectAsState()
    val errorMessage by siswaViewModel.errorMessage.collectAsState()
    
    var showDayFilter by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }

    // Get user info
    val context = androidx.compose.ui.platform.LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            try {
                val authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
                userName = authRepo.getUserName()
                userRole = authRepo.getUserRole()
                isInitialized = true
            } catch (e: Exception) {
                android.util.Log.e("SiswaScheduleScreen", "Error loading user info", e)
            }
        }
    }

    // Load data when userKelasId becomes available
    LaunchedEffect(userKelasId) {
        val localKelasId = userKelasId
        if (localKelasId != null) {
            android.util.Log.d("SiswaScheduleScreen", "Loading schedules for kelas_id: $localKelasId")
            siswaViewModel.loadSchedules(localKelasId)
        } else {
            android.util.Log.e("SiswaScheduleScreen", "userKelasId is NULL - waiting for it to be loaded from DataStore")
        }
    }

    // Filter schedules by kelas_id AND day
    val filteredSchedules = remember(schedules, dayFilter, userKelasId) {
        var filtered = schedules

        // Filter by kelas_id first - capture userKelasId in a local variable for the lambda
        val localKelasId = userKelasId
        if (localKelasId != null) {
            filtered = filtered.filter { it.kelasId == localKelasId }
            android.util.Log.d("SiswaScheduleScreen", "Filtered by kelasId $localKelasId: ${filtered.size} schedules")
        }

        // Then filter by day
        if (!dayFilter.isNullOrEmpty() && dayFilter != "Semua") {
            filtered = filtered.filter { it.hari.equals(dayFilter, ignoreCase = true) }
            android.util.Log.d("SiswaScheduleScreen", "Filtered by day $dayFilter: ${filtered.size} schedules")
        }

        filtered
    }

    // Group schedules by day
    val groupedSchedules = remember(filteredSchedules) {
        filteredSchedules.groupBy { it.hari }
            .toSortedMap(compareBy { 
                when(it) {
                    "Senin" -> 1
                    "Selasa" -> 2
                    "Rabu" -> 3
                    "Kamis" -> 4
                    "Jumat" -> 5
                    "Sabtu" -> 6
                    "Minggu" -> 7
                    else -> 8
                }
            })
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
            showHomeButton = true,
            onHomeClick = { navController.navigate("siswa_home") }
        )

        StandardPageTitle(
            title = if (userKelasName != null) "Jadwal Pelajaran - $userKelasName" else if (userKelasId != null) "Jadwal Pelajaran - Kelas ID: $userKelasId" else "Jadwal Pelajaran",
            icon = Icons.Default.CalendarToday
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            StandardSectionHeader(title = "Filter")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Filter Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardFilterButton(
                    label = "Hari",
                    selectedValue = dayFilter ?: "Semua",
                    icon = Icons.Default.CalendarToday,
                    onClick = { showDayFilter = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error message
            errorMessage?.let { error ->
                StandardInfoCard(
                    message = error,
                    icon = Icons.Default.Error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Schedule List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = ModernColors.PrimaryBlue)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Memuat jadwal...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }
            } else if (groupedSchedules.isEmpty()) {
                ModernEmptyState(
                    icon = Icons.Default.CalendarToday,
                    title = "Belum Ada Jadwal",
                    message = "Belum ada jadwal pelajaran tersedia",
                    onResetFilters = null
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedSchedules.forEach { (hari, scheduleList) ->
                        item {
                            StandardSectionHeader(title = hari)
                        }
                        items(scheduleList.sortedBy { it.jamKe }) { schedule ->
                            ScheduleCardItem(schedule = schedule)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        SiswaFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }

    // Day Filter Dialog
    if (showDayFilter) {
        ModernFilterDialog(
            title = "Pilih Hari",
            options = listOf("Semua", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"),
            selectedOption = dayFilter ?: "Semua",
            onSelect = { selected ->
                siswaViewModel.setDayFilter(if (selected == "Semua") null else selected)
                showDayFilter = false
            },
            onDismiss = { showDayFilter = false }
        )
    }
}

@Composable
fun ScheduleCardItem(schedule: Schedule) {
    StandardDataCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header - Kelas dan Hari - same as kepsek
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = schedule.kelasName ?: "Kelas ${schedule.kelasId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ModernColors.TextPrimary
                )

                // Hari badge - same as kepsek
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = ModernColors.SuccessGreen.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = ModernColors.SuccessGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = schedule.hari ?: "-",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ModernColors.SuccessGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = ModernColors.BorderGray)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mata Pelajaran - same as kepsek
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ModernColors.PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = schedule.mapel,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ModernColors.TextPrimary
                    )
                }

                // Teacher Name - same as kepsek
                schedule.guruName?.let { guru ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = ModernColors.InfoBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = guru,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextPrimary
                        )
                    }
                }

                // Time Info - same as kepsek
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ModernColors.WarningAmber
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${schedule.jamMulai} - ${schedule.jamSelesai}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ModernColors.TextSecondary
                    )
                }

                // Ruangan Info
                schedule.ruangan?.let { ruangan ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MeetingRoom,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = ModernColors.InfoBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ruangan,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }
            }

            // Jam Ke Badge - same as kepsek
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = ModernColors.LightBlue,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Jam Ke-${schedule.jamKe}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = ModernColors.TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = ModernColors.TextSecondary,
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = ModernColors.TextPrimary,
            modifier = Modifier.weight(0.7f)
        )
    }
}
