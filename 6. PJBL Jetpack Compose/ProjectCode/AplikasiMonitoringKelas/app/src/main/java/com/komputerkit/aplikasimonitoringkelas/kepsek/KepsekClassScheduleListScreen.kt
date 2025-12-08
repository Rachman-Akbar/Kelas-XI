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
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.common.getDayOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekClassScheduleListScreen(
    navController: NavController,
    kepsekViewModel: KepsekViewModel
) {
    val jadwalList by kepsekViewModel.filteredJadwalList.collectAsState(initial = emptyList())
    val isLoading by kepsekViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kepsekViewModel.errorMessage.collectAsState(initial = null)
    val classes by kepsekViewModel.classList.collectAsState(initial = emptyList())

    var showDayFilter by remember { mutableStateOf(false) }
    var showClassFilter by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf("Semua") }
    var selectedClass by remember { mutableStateOf("Semua") }
    var isInitialized by remember { mutableStateOf(false) }

    // Synchronize UI state with ViewModel filter state
    val viewModelHariFilter by kepsekViewModel.hariFilter.collectAsState()
    val viewModelKelasIdFilter by kepsekViewModel.kelasIdFilter.collectAsState()

    // Update UI state when ViewModel filters change (e.g. from other screens)
    LaunchedEffect(viewModelHariFilter, viewModelKelasIdFilter, classes) {
        // Update selectedDay when ViewModel filter changes
        selectedDay = if (viewModelHariFilter == null) "Semua"
                     else {
                         val dayOptions = listOf("Semua") + getDayOptions()
                         val matchedDay = dayOptions.find { it.equals(viewModelHariFilter, ignoreCase = true) }
                         matchedDay ?: viewModelHariFilter.toString()
                     }

        // Update selectedClass when ViewModel filter changes
        selectedClass = if (viewModelKelasIdFilter == null) "Semua"
                       else {
                           val foundClass = classes.find { it.id == viewModelKelasIdFilter }
                           if (foundClass != null && !foundClass.nama.isNullOrBlank()) {
                               foundClass.nama!!
                           } else {
                               "Kelas ${viewModelKelasIdFilter.toString()}"
                           }
                       }
    }

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            kepsekViewModel.loadEnumOptions() // Load enum options from database
            kepsekViewModel.loadClasses()
            kepsekViewModel.loadJadwal(null, null) // Load all schedules without filtering
            isInitialized = true
        }
    }

    // Update ViewModel filters when UI selections change
    LaunchedEffect(selectedDay, selectedClass, classes) {
        val effectiveDay = if (selectedDay == "Semua") null else selectedDay
        val selectedKelasId = if (selectedClass == "Semua") null else classes.find { it.nama == selectedClass }?.id
        kepsekViewModel.setHariFilter(effectiveDay)
        kepsekViewModel.setKelasIdFilter(selectedKelasId)
    }

    val filteredList = jadwalList // Use the ViewModel's filtered list

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernColors.BackgroundWhite)
    ) {
        StandardPageTitle(
            title = "Jadwal Kelas",
            icon = Icons.Default.CalendarToday
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
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

            // Loading indicator or filtered list with improved empty state
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
            } else if (jadwalList.isEmpty() && !isLoading) {
                // Hanya tampilkan empty state jika data asli kosong (bukan hasil filter)
                ModernEmptyState(
                    icon = Icons.Default.EventBusy,
                    title = "Belum Ada Data",
                    message = "Belum ada jadwal yang tersedia",
                    onResetFilters = null
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(jadwalList) { jadwal ->
                        StandardDataCard {
                            // Title - Kelas dan Hari
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = jadwal.kelasName ?: "Kelas ${jadwal.kelasId}",
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
                                            text = jadwal.hari ?: "-",
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
                                        text = jadwal.mapel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ModernColors.TextPrimary
                                    )
                                }

                                // Teacher Name
                                jadwal.guruName?.let { guru ->
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
                                        text = "${jadwal.jamMulai} - ${jadwal.jamSelesai}",
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
                                        text = "Jam ke-${jadwal.jamKe}",
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
        val classOptions = listOf("Semua") + classes.map { it.nama }
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