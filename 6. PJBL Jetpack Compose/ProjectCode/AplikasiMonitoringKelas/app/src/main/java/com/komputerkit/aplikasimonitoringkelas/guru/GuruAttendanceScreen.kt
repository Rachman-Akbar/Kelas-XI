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
fun GuruAttendanceScreen(
    navController: NavController,
    guruViewModel: GuruViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "guru_attendance"
    val allAttendances by guruViewModel.kehadiranGuruList.collectAsState(initial = emptyList())
    val classList by guruViewModel.classList.collectAsState(initial = emptyList())
    val isLoading by guruViewModel.isLoading.collectAsState(initial = false)
    val currentGuruId by guruViewModel.currentGuruId.collectAsState(initial = null)

    var showStatusFilter by remember { mutableStateOf(false) }
    var showClassFilter by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Semua") }
    var selectedClass by remember { mutableStateOf("Semua") }
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    // Client-side filtering with optimized recomputation
    val filteredAttendances by remember(allAttendances, selectedStatus, selectedClass, classList) {
        derivedStateOf {
            allAttendances.filter { attendance ->
                val statusMatch = selectedStatus == "Semua" ||
                    selectedStatus.equals(attendance.statusKehadiran, ignoreCase = true)
                val classMatch = selectedClass == "Semua" ||
                    attendance.kelasName?.equals(selectedClass) == true
                statusMatch && classMatch
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

    // Load data when currentGuruId becomes available
    LaunchedEffect(Unit) {
        // Try to ensure the currentGuruId is loaded by triggering the filter application
        kotlinx.coroutines.delay(300) // Give a little time for initialization
        guruViewModel.applyKehadiranGuruFilter()
    }

    // Also react to currentGuruId changes
    LaunchedEffect(currentGuruId) {
        if (currentGuruId != null) {
            guruViewModel.loadClasses()
            // Load all teacher attendance data for current user
            guruViewModel.setKelasIdKehadiranFilter(null)  // No class filter initially
            guruViewModel.setStatusKehadiranFilter(null)   // No status filter initially
            guruViewModel.loadKehadiranGuru(null)
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
            title = "Kehadiran Saya",
            icon = Icons.Default.People
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ModernColors.BackgroundWhite)
                .padding(16.dp)
        ) {
            // Filter dropdowns
            StandardSectionHeader("Filter Data")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filter Status
                StandardFilterButton(
                    label = "Status",
                    selectedValue = selectedStatus,
                    icon = Icons.Default.CheckCircle,
                    onClick = { showStatusFilter = true },
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

            // Loading indicator or attendance list with improved empty state
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
            } else if (allAttendances.isEmpty() && !isLoading) {
                ModernEmptyState(
                    icon = Icons.Default.EventBusy,
                    title = "Belum Ada Data",
                    message = "Belum ada data kehadiran yang tersedia",
                    onResetFilters = null
                )
            } else if (filteredAttendances.isEmpty()) {
                ModernEmptyState(
                    icon = Icons.Default.FilterList,
                    title = "Tidak Ada Hasil",
                    message = "Tidak ada data kehadiran untuk filter yang dipilih",
                    onResetFilters = {
                        selectedStatus = "Semua"
                        selectedClass = "Semua"
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAttendances) { attendance ->
                        StandardDataCard {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Header: Kelas dan Status
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = attendance.kelasName ?: "Kelas -",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ModernColors.TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    StandardStatusBadge(status = attendance.statusKehadiran ?: "hadir")
                                }

                                Divider(color = ModernColors.BorderGray)

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Tanggal
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = ModernColors.PrimaryBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = formatDate(attendance.tanggal ?: "-"),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextPrimary
                                        )
                                    }

                                    // Nama Guru
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
                                            text = attendance.guruName ?: "Guru ID: ${attendance.guruId}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ModernColors.TextPrimary
                                        )
                                    }

                                    // Mata Pelajaran
                                    attendance.mataPelajaran?.let { mapel ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Book,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = ModernColors.InfoBlue
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = mapel,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ModernColors.TextSecondary
                                            )
                                        }
                                    }

                                    // Waktu Datang
                                    attendance.waktuDatang?.let { waktu ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Schedule,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = ModernColors.InfoBlue
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Waktu Datang: $waktu",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ModernColors.TextSecondary
                                            )
                                        }
                                    }

                                    // Keterangan
                                    attendance.keterangan?.let { ket ->
                                        if (ket.isNotBlank()) {
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = ModernColors.LightGray.copy(alpha = 0.3f),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalAlignment = Alignment.Top
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = ModernColors.TextSecondary
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = ket,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = ModernColors.TextPrimary
                                                    )
                                                }
                                            }
                                        }
                                    }
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

    // Filter Dialog Status
    if (showStatusFilter) {
        val statusOptions = listOf("Semua") + getKehadiranGuruStatusOptions()
        ModernFilterDialog(
            title = "Pilih Status Kehadiran",
            options = statusOptions,
            selectedOption = selectedStatus,
            onDismiss = { showStatusFilter = false },
            onSelect = { status ->
                selectedStatus = status
                showStatusFilter = false
            }
        )
    }

    // Filter Dialog Kelas
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