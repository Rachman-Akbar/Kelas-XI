package com.komputerkit.aplikasimonitoringkelas.guru

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
fun GuruScheduleScreen(
    navController: NavController,
    guruViewModel: GuruViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "guru_schedule"
    val allSchedules by guruViewModel.schedules.collectAsState(initial = emptyList())
    val classList by guruViewModel.classList.collectAsState(initial = emptyList())
    val isLoading by guruViewModel.isLoading.collectAsState(initial = false)
    val currentGuruId by guruViewModel.currentGuruId.collectAsState(initial = null)

    var showDayFilter by remember { mutableStateOf(false) }
    var showClassFilter by remember { mutableStateOf(false) }
    var selectedDay: String? by remember { mutableStateOf("Semua") }
    var selectedClass by remember { mutableStateOf("Semua") }
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    // Client-side filtering with optimized recomputation
    val filteredSchedules by remember(allSchedules, selectedDay, selectedClass, classList) {
        derivedStateOf {
            allSchedules.filter { schedule ->
                val dayMatch = selectedDay == "Semua" || selectedDay.equals(schedule.hari, ignoreCase = true)
                val classMatch = selectedClass == "Semua" ||
                    classList.any { it.nama == selectedClass && it.id == schedule.kelasId }
                dayMatch && classMatch
            }
        }
    }

    // Get user info
    val context = androidx.compose.ui.platform.LocalContext.current
    var authRepo by remember { mutableStateOf<com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository?>(null) }

    LaunchedEffect(Unit) {
        try {
            authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
            userName = authRepo?.getUserName() ?: ""
            userRole = authRepo?.getUserRole() ?: ""
        } catch (e: Exception) {
            // Handle error if needed
        }
    }

    // Initial data load when currentGuruId becomes available (without filters)
    LaunchedEffect(currentGuruId) {
        if (currentGuruId != null) {
            guruViewModel.loadClasses()
            // Load all schedules for current user's guru_id without filtering on server side
            guruViewModel.setKelasFilter(null)  // No class filter initially
            guruViewModel.setDayFilter(null)     // No day filter initially
            guruViewModel.loadSchedules(currentGuruId, null, null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernColors.BackgroundWhite)
    ) {
        AppHeader(
            title = "Dashboard Guru",
            userName = "", // This should be populated from auth
            userRole = "Guru",
            navController = navController,
            onLogout = onLogout,
            showHomeButton = true,
            onHomeClick = { navController.navigate("guru_home") }
        )

        StandardPageTitle(
            title = "Jadwal Mengajar Saya",
            icon = Icons.Default.CalendarToday
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ModernColors.BackgroundWhite)
                .padding(16.dp)
        ) {
            // Filter dropdowns dengan komponen konsisten
            StandardSectionHeader("Filter Data")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filter Hari
                StandardFilterButton(
                    label = "Hari",
                    selectedValue = selectedDay,
                    icon = Icons.Default.CalendarToday,
                    onClick = { showDayFilter = true },
                    modifier = Modifier.weight(1f)
                )

                // Filter Kelas
                StandardFilterButton(
                    label = "Kelas",
                    selectedValue = selectedClass,
                    icon = Icons.Default.Class,
                    onClick = { showClassFilter = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator or schedule list with improved empty state
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
                            text = "Memuat data...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }
            } else if (allSchedules.isEmpty() && !isLoading) {
                // Hanya tampilkan empty state jika data asli kosong (bukan hasil filter)
                ModernEmptyState(
                    icon = Icons.Default.EventBusy,
                    title = "Belum Ada Data",
                    message = "Belum ada jadwal mengajar yang tersedia",
                    onResetFilters = null
                )
            } else if (filteredSchedules.isEmpty()) {
                // Tampilkan ini ketika filter tidak cocok dengan data
                ModernEmptyState(
                    icon = Icons.Default.FilterList,
                    title = "Tidak Ada Hasil",
                    message = "Tidak ada jadwal mengajar untuk filter yang dipilih",
                    onResetFilters = {
                        selectedDay = "Semua"
                        selectedClass = "Semua"
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSchedules) { schedule ->
                        StandardDataCard {
                            // Title - Kelas dan Hari
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

                                // Hari di samping
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
                                // Mata Pelajaran
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

                                // Teacher Name
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

                                // Time Info
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
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ModernColors.TextSecondary
                                    )
                                }

                                // Ruangan
                                schedule.ruangan?.let { ruangan ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Room,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = ModernColors.InfoBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = ruangan,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ModernColors.TextSecondary
                                        )
                                    }
                                }
                            }

                            // Jam Ke Badge
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
                                        text = "Jam ke-${schedule.jamKe}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = ModernColors.PrimaryBlue,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        GuruFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }

    // Dialog Filter Hari
    if (showDayFilter) {
        val dayOptions = listOf("Semua") + getDayOptions()
        ModernFilterDialog(
            title = "Pilih Hari",
            options = dayOptions,
            selectedOption = selectedDay,
            onDismiss = { showDayFilter = false },
            onSelect = { hari ->
                selectedDay = hari
                showDayFilter = false
            }
        )
    }

    // Dialog Filter Kelas
    if (showClassFilter) {
        val classOptions = listOf("Semua") + classList.map { it.nama ?: "Kelas ${it.id}" }
        ModernFilterDialog(
            title = "Pilih Kelas",
            options = classOptions,
            selectedOption = selectedClass,
            onDismiss = { showClassFilter = false },
            onSelect = { kelasName ->
                selectedClass = kelasName
                showClassFilter = false
            }
        )
    }
}