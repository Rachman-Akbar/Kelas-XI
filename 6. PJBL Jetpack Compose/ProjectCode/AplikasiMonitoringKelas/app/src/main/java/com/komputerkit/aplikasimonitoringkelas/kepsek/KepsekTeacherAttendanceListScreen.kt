package com.komputerkit.aplikasimonitoringkelas.kepsek

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
import com.komputerkit.aplikasimonitoringkelas.common.formatDate
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.common.getKehadiranGuruStatusOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekTeacherAttendanceListScreen(
    navController: NavController,
    kepsekViewModel: KepsekViewModel
) {
    val kehadiranList by kepsekViewModel.filteredKehadiranGuruList.collectAsState(initial = emptyList())
    val isLoading by kepsekViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kepsekViewModel.errorMessage.collectAsState(initial = null)
    val classes by kepsekViewModel.classList.collectAsState(initial = emptyList())
    val statusKehadiranGuruOptions by kepsekViewModel.statusKehadiranGuruOptions.collectAsState()

    var showStatusFilter by remember { mutableStateOf(false) }
    var showClassFilter by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Semua") }
    var selectedClass by remember { mutableStateOf("Semua") }

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
            // Load all teacher attendance data once - filtering is reactive via combine() flow
            kepsekViewModel.loadKehadiranGuru(null, null)
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
        modifier = Modifier
            .fillMaxSize()
            .background(ModernColors.BackgroundWhite)
    ) {
        StandardPageTitle(
            title = "Kehadiran Guru",
            icon = Icons.Default.People
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

            // Loading or Attendance list
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
                // Use filtered list provided by ViewModel
                val effectiveList = kehadiranList

                if (effectiveList.isEmpty()) {
                    ModernEmptyState(
                        icon = Icons.Default.PersonOff,
                        title = "Belum Ada Data",
                        message = "Belum ada data kehadiran guru",
                        onResetFilters = null
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(effectiveList) { item ->
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
                                            text = item.kelasName ?: "Kelas -",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ModernColors.TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        StandardStatusBadge(status = item.statusKehadiran ?: "hadir")
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
                                                text = formatDate(item.tanggal ?: "-"),
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
                                                text = item.guruName ?: "Guru ID: ${item.guruId}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ModernColors.TextPrimary
                                            )
                                        }

                                        // Mata Pelajaran
                                        item.mataPelajaran?.let { mapel ->
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
                                        item.waktuDatang?.let { waktu ->
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
                                        item.keterangan?.let { ket ->
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
                    val kelasId =
                        if (selected == "Semua") null else classes.find { it.nama == selected }?.id
                    kepsekViewModel.setKelasIdFilter(kelasId)
                    // Reactive filtering will update automatically via combine() flow
                    showClassFilter = false
                },
                onDismiss = { showClassFilter = false }
            )
        }

        // Filter Dialog Status dari database
        if (showStatusFilter) {
            val statusOptions = if (statusKehadiranGuruOptions.isNotEmpty()) {
                statusKehadiranGuruOptions
            } else {
                getKehadiranGuruStatusOptions()
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
}

