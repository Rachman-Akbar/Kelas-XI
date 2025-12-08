package com.komputerkit.aplikasimonitoringkelas.kurikulum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.common.getDayOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumScheduleScreen(
    navController: NavController,
    kurikulumViewModel: KurikulumViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "kurikulum_schedule"
    val scheduleList by kurikulumViewModel.scheduleList.collectAsState(initial = emptyList())
    val classList by kurikulumViewModel.classList.collectAsState(initial = emptyList())
    val isLoading by kurikulumViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kurikulumViewModel.errorMessage.collectAsState(initial = null)

    var showDayFilter by remember { mutableStateOf(false) }
    var showClassFilter by remember { mutableStateOf(false) }
    val selectedDay: String = (kurikulumViewModel.hariFilter.value ?: "Semua")
    val selectedClass by derivedStateOf {
        (kurikulumViewModel.kelasIdFilter.value?.let { kelasId ->
            classList.find { it.id == kelasId }?.nama
        }) ?: "Semua"
    }

    // Update variables when ViewModel filters change
    LaunchedEffect(kurikulumViewModel.hariFilter.collectAsState().value) {
        // The selectedDay will automatically update via derivedStateOf
    }

    // Get user info for header
    val context = androidx.compose.ui.platform.LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            try {
                val authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
                userName = authRepo.getUserName()
                userRole = authRepo.getUserRole()
            } catch (e: Exception) {
                // Ignore error
            }
            kurikulumViewModel.loadClasses()
            // Initialize with default filters (no filters applied)
            kurikulumViewModel.setHariFilter(null)
            kurikulumViewModel.setKelasIdFilter(null)
            kurikulumViewModel.applySchedulesFilters()
            isInitialized = true
        }
    }

    // Map data model to common model (already filtered by API)
    val mappedList: List<Schedule> = remember(scheduleList) {
        scheduleList.map { dbSchedule ->
            Schedule(
                id = dbSchedule.id,
                guruId = dbSchedule.guru_id,
                kelasId = dbSchedule.kelas_id,
                hari = dbSchedule.hari,
                jam = dbSchedule.jam_mulai ?: "",
                mapel = dbSchedule.mata_pelajaran?.nama ?: "Mata Pelajaran ${dbSchedule.mata_pelajaran_id}",
                guruName = dbSchedule.guru?.nama ?: "",
                kelasName = dbSchedule.kelas?.nama ?: "",
                kelas = null,
                nipGuru = dbSchedule.guru?.nip,
                kodeMapel = dbSchedule.mata_pelajaran?.kode,
                tahunAjaran = dbSchedule.tahun_ajaran,
                jamKe = dbSchedule.jam_ke ?: 0,
                jamMulai = dbSchedule.jam_mulai,
                jamSelesai = dbSchedule.jam_selesai,
                ruangan = dbSchedule.ruangan
            )
        }
    }
    // Use the mapped list directly (filtered by API)
    val filteredList = mappedList

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
            onLogout = onLogout,
            showHomeButton = true,
            onHomeClick = { navController.navigate("kurikulum_home") }
        )

        StandardPageTitle(
            title = "Jadwal Mengajar",
            icon = Icons.Default.CalendarToday
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ModernColors.BackgroundWhite)
                .padding(16.dp)
        ) {
            StandardSectionHeader(title = "Filter")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StandardFilterButton(
                    label = "Hari",
                    selectedValue = selectedDay,
                    icon = Icons.Default.CalendarToday,
                    onClick = { showDayFilter = true },
                    modifier = Modifier.weight(1f)
                )
                StandardFilterButton(
                    label = "Kelas",
                    selectedValue = selectedClass,
                    icon = Icons.Default.Class,
                    onClick = { showClassFilter = true },
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

            // Schedule list
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
            } else if (scheduleList.isEmpty() && !isLoading) {
                // Hanya tampilkan empty state jika data asli kosong (bukan hasil filter)
                ModernEmptyState(
                    icon = Icons.Default.EventBusy,
                    title = "Belum Ada Data",
                    message = "Belum ada jadwal yang tersedia",
                    onResetFilters = null
                )
            } else if (filteredList.isEmpty()) {
                // Data ada tapi hasil filter kosong
                ModernEmptyState(
                    icon = Icons.Default.FilterList,
                    title = "Tidak Ada Hasil",
                    message = "Tidak ada jadwal untuk filter yang dipilih",
                    onResetFilters = {
                        kurikulumViewModel.setHariFilter(null)
                        kurikulumViewModel.setKelasIdFilter(null)
                        kurikulumViewModel.applySchedulesFilters()
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { schedule ->
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
                                            text = schedule.hari,
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

        KurikulumFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }

    // Day Filter Dialog
    if (showDayFilter) {
        val dayOptions = listOf("Semua") + getDayOptions()
        ModernFilterDialog(
            title = "Pilih Hari",
            options = dayOptions,
            selectedOption = selectedDay,
            onDismiss = { showDayFilter = false },
            onSelect = { day ->
                // Set filter and reload data
                kurikulumViewModel.setHariFilter(if (day == "Semua") null else day)
                kurikulumViewModel.applySchedulesFilters()
                showDayFilter = false
            }
        )
    }

    // Class Filter Dialog
    if (showClassFilter) {
        val classOptions = listOf("Semua") + classList.map { it.nama }
        ModernFilterDialog(
            title = "Pilih Kelas",
            options = classOptions,
            selectedOption = selectedClass,
            onDismiss = { showClassFilter = false },
            onSelect = { kelas ->
                // Set filter and reload data
                val kelasId = if (kelas == "Semua") null else classList.find { it.nama == kelas }?.id
                kurikulumViewModel.setKelasIdFilter(kelasId)
                kurikulumViewModel.applySchedulesFilters()
                showClassFilter = false
            }
        )
    }
}
