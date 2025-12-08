package com.komputerkit.aplikasimonitoringkelas.kurikulum

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
fun KurikulumTeacherAttendanceScreen(
    navController: NavController,
    kurikulumViewModel: KurikulumViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "kurikulum_attendance"
    val attendanceList by kurikulumViewModel.teacherAttendances.collectAsState(initial = emptyList())
    val classList by kurikulumViewModel.classList.collectAsState(initial = emptyList())
    val isLoading by kurikulumViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kurikulumViewModel.errorMessage.collectAsState(initial = null)

    var showStatusFilter by remember { mutableStateOf(false) }
    var showClassFilter by remember { mutableStateOf(false) }
    val statusFilter by kurikulumViewModel.statusFilter.collectAsState(initial = null)
    val kelasIdFilter by kurikulumViewModel.kelasIdFilter.collectAsState(initial = null)

    val selectedStatus = statusFilter ?: "Semua"
    val selectedClass = kelasIdFilter?.let { kelasId ->
        classList.find { it.id == kelasId }?.nama
    } ?: "Semua"

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
            kurikulumViewModel.setStatusFilter(null)
            kurikulumViewModel.setKelasIdFilter(null)
            kurikulumViewModel.applyAttendanceFilters()
            isInitialized = true
        }
    }

    val filteredList = attendanceList

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
            title = "Daftar Kehadiran Guru",
            icon = Icons.Default.People
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
                    label = "Status Kehadiran",
                    selectedValue = selectedStatus,
                    icon = Icons.Default.CheckCircle,
                    onClick = { showStatusFilter = true },
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

            // Attendance list
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
            } else if (attendanceList.isEmpty() && !isLoading) {
                ModernEmptyState(
                    icon = Icons.Default.PersonOff,
                    title = "Belum Ada Data",
                    message = "Belum ada data kehadiran guru",
                    onResetFilters = null
                )
            } else if (attendanceList.isEmpty() && !isLoading) {
                ModernEmptyState(
                    icon = Icons.Default.FilterList,
                    title = "Tidak Ada Hasil",
                    message = "Tidak ada data kehadiran untuk filter yang dipilih",
                    onResetFilters = {
                        kurikulumViewModel.setStatusFilter(null)
                        kurikulumViewModel.setKelasIdFilter(null)
                        kurikulumViewModel.applyAttendanceFilters()
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(attendanceList) { attendance ->
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
                                                text = formatDate(attendance.tanggal),
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
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = ModernColors.WarningAmber
                                                )
                                            }
                                        }

                                        // Late Duration (if late)
                                        attendance.durasiKeterlambatan?.let { durasi ->
                                            if (durasi > 0) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.WatchLater,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = ModernColors.WarningAmber
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Terlambat: $durasi menit",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = ModernColors.WarningAmber,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }

                                        // Keterangan
                                        attendance.keterangan?.let { notes ->
                                            if (notes.isNotBlank()) {
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
                                                            text = notes,
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

        KurikulumFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }

    // Status Filter Dialog
    if (showStatusFilter) {
        val statusOptions = listOf("Semua", "hadir", "telat", "tidak_hadir", "izin", "sakit")
        ModernFilterDialog(
            title = "Pilih Status Kehadiran",
            options = statusOptions,
            selectedOption = selectedStatus,
            onDismiss = { showStatusFilter = false },
            onSelect = { status ->
                // Set filter and reload data
                kurikulumViewModel.setStatusFilter(if (status == "Semua") null else status)
                kurikulumViewModel.applyAttendanceFilters()
                showStatusFilter = false
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
                kurikulumViewModel.applyAttendanceFilters()
                showClassFilter = false
            }
        )
    }
}
