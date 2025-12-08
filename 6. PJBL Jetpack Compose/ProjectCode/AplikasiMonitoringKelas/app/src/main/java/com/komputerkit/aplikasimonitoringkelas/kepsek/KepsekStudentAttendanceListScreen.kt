package com.komputerkit.aplikasimonitoringkelas.kepsek

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
import com.komputerkit.aplikasimonitoringkelas.common.formatDate
import androidx.compose.foundation.background
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.common.getKehadiranSiswaStatusOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekStudentAttendanceListScreen(
    navController: NavController,
    kepsekViewModel: KepsekViewModel
) {
    val studentAttendances by kepsekViewModel.filteredKehadiranSiswaList.collectAsState(initial = emptyList())
    val isLoading by kepsekViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kepsekViewModel.errorMessage.collectAsState(initial = null)
    val classes by kepsekViewModel.classList.collectAsState(initial = emptyList())
    val statusKehadiranSiswaOptions by kepsekViewModel.statusKehadiranSiswaOptions.collectAsState()

    var showClassFilter by remember { mutableStateOf(false) }
    var showStatusFilter by remember { mutableStateOf(false) }
    var selectedClass by remember { mutableStateOf("Semua") }
    var selectedStatus by remember { mutableStateOf("Semua") }

    // Update selected values when filters change (reactive)
    LaunchedEffect(kepsekViewModel.kelasIdFilter.collectAsState().value, classes) {
        val kelasId = kepsekViewModel.kelasIdFilter.value
        selectedClass = if (kelasId != null) {
            classes.find { it.id == kelasId }?.nama ?: "Semua"
        } else "Semua"
    }
    
    LaunchedEffect(kepsekViewModel.statusFilter.collectAsState().value) {
        val status = kepsekViewModel.statusFilter.value
        // Display original format from filter, no conversion needed
        selectedStatus = status ?: "Semua"
    }

    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            kepsekViewModel.loadEnumOptions()
            kepsekViewModel.loadClasses()
            // Load all student attendance data once - filtering is reactive via combine() flow
            kepsekViewModel.loadKehadiranSiswa(null, null, null)
            isInitialized = true
        }
    }

    // Update ViewModel filters when selections change (for reactive filtering)
    LaunchedEffect(selectedClass, selectedStatus, classes) {
        val kelasId = if (selectedClass == "Semua") null else classes.find { it.nama == selectedClass }?.id
        val statusValue = if (selectedStatus == "Semua") null else selectedStatus
        kepsekViewModel.setKelasIdFilter(kelasId)
        kepsekViewModel.setStatusFilter(statusValue)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardPageTitle(
            title = "Kehadiran Siswa",
            icon = Icons.Default.Group
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ModernColors.BackgroundWhite)
                .padding(16.dp)
        ) {
            // Filters
            StandardSectionHeader("Filter Data")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filter Kelas
                StandardFilterButton(
                    label = "Kelas",
                    selectedValue = selectedClass,
                    icon = Icons.Default.Class,
                    onClick = { showClassFilter = true },
                    modifier = Modifier.weight(1f)
                )

                // Filter Status
                StandardFilterButton(
                    label = "Status",
                    selectedValue = selectedStatus,
                    icon = Icons.Default.CheckCircle,
                    onClick = { showStatusFilter = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Use filtered list from ViewModel
            val effectiveList = studentAttendances

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
            } else {
                if (effectiveList.isEmpty()) {
                    ModernEmptyState(
                        icon = Icons.Default.PersonOff,
                        title = "Belum Ada Data",
                        message = "Belum ada data kehadiran siswa",
                        onResetFilters = null
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(effectiveList) { attendance ->
                            StandardDataCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = attendance.siswa?.nama ?: "Siswa ID: ${attendance.siswaId}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ModernColors.TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "NIS: ${attendance.siswa?.nis ?: "-"}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ModernColors.TextSecondary
                                        )
                                    }
                                    
                                    StandardStatusBadge(status = attendance.status ?: "hadir")
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
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
                                        color = ModernColors.TextPrimary
                                    )
                                }
                                
                                // Nama Kelas
                                attendance.kelasName?.let { kelas ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Class,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = ModernColors.InfoBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = kelas,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ModernColors.TextSecondary
                                        )
                                    }
                                }
                                
                                // Mata Pelajaran
                                attendance.mataPelajaran?.let { mapel ->
                                    Spacer(modifier = Modifier.height(8.dp))
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
                                
                                // Guru
                                attendance.guruName?.let { guru ->
                                    Spacer(modifier = Modifier.height(8.dp))
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
                                            text = "Guru: $guru",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ModernColors.TextSecondary
                                        )
                                    }
                                }
                                
                                // Keterangan
                                if (!attendance.keterangan.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    StandardInfoCard(
                                        message = "Keterangan: ${attendance.keterangan}",
                                        icon = Icons.Default.Info
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Filter Dialog Kelas
    if (showClassFilter) {
        val classOptions = listOf("Semua") + classes.map { it.nama }
        ModernFilterDialog(
            title = "Pilih Kelas",
            options = classOptions,
            selectedOption = selectedClass,
            onSelect = { selected ->
                selectedClass = selected
                val kelasId = if (selected == "Semua") null else classes.find { it.nama == selected }?.id
                kepsekViewModel.setKelasIdFilter(kelasId)
                // Reactive filtering will update automatically via combine() flow
                showClassFilter = false
            },
            onDismiss = { showClassFilter = false }
        )
    }

    // Filter Dialog Status dari database
    if (showStatusFilter) {
        val statusOptions = if (statusKehadiranSiswaOptions.isNotEmpty()) {
            statusKehadiranSiswaOptions
        } else {
            getKehadiranSiswaStatusOptions()
        }
        ModernFilterDialog(
            title = "Pilih Status Kehadiran",
            options = statusOptions,
            selectedOption = selectedStatus,
            onSelect = { selected ->
                selectedStatus = selected
                // Keep original format from database, only convert if it's "Semua"
                val statusValue = if (selected == "Semua") null else selected
                kepsekViewModel.setStatusFilter(statusValue)
                // Reactive filtering will update automatically via combine() flow
                showStatusFilter = false
            },
            onDismiss = { showStatusFilter = false }
        )
    }
}