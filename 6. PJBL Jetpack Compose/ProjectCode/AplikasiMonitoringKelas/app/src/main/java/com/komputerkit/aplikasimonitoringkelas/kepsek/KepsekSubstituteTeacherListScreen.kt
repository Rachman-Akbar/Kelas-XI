package com.komputerkit.aplikasimonitoringkelas.kepsek

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.formatDate
import androidx.compose.foundation.background
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.common.getStatusPenggantiOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekSubstituteTeacherListScreen(
    navController: NavController,
    kepsekViewModel: KepsekViewModel
) {
    val substitutes by kepsekViewModel.filteredGuruPenggantiList.collectAsState(initial = emptyList())
    val isLoading by kepsekViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kepsekViewModel.errorMessage.collectAsState(initial = null)
    val classes by kepsekViewModel.classList.collectAsState(initial = emptyList())
    val statusPenggantiOptions by kepsekViewModel.statusPenggantiOptions.collectAsState()

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
            // Load all substitute teacher data once - filtering is reactive via combine() flow
            kepsekViewModel.loadGuruPengganti(null, null)
            isInitialized = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardPageTitle(
            title = "Guru Pengganti",
            icon = Icons.Default.SwapHoriz
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
            val effectiveList = substitutes

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
                        icon = Icons.Default.SwapHoriz,
                        title = "Belum Ada Data",
                        message = "Belum ada data guru pengganti",
                        onResetFilters = null
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(effectiveList) { substitute ->
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
                                            text = substitute.kelas ?: "Kelas -",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ModernColors.TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        StandardStatusBadge(
                                            status = substitute.statusPenggantian ?: "pending"
                                        )
                                    }

                                    Divider(color = ModernColors.BorderGray)

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
                                            text = formatDate(substitute.tanggal ?: "-"),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextPrimary
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {

                                        // Info Guru Asli
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
                                            Column {
                                                Text(
                                                    text = "Guru Asli",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = ModernColors.TextSecondary
                                                )
                                                Text(
                                                    text = substitute.namaGuruAsli ?: "-",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = ModernColors.TextPrimary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Info Guru Pengganti
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = ModernColors.InfoBlue
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Guru Pengganti",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = ModernColors.TextSecondary
                                                )
                                                Text(
                                                    text = substitute.namaGuruPengganti ?: "-",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = ModernColors.TextPrimary
                                                )
                                            }
                                        }

                                        // Keterangan dari Guru (saat entry)
                                        substitute.keterangan?.let { ket ->
                                            if (ket.isNotBlank()) {
                                                Divider(color = ModernColors.LightBlue.copy(alpha = 0.3f))
                                                
                                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Text(
                                                        text = "Keterangan",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = ModernColors.TextSecondary
                                                    )
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

                                        // Disetujui/Ditolak Oleh
                                        if (substitute.approverName != null && substitute.statusPenggantian.lowercase() in listOf(
                                                "dijadwalkan",
                                                "disetujui",
                                                "selesai",
                                                "ditolak"
                                            )
                                        ) {
                                            Divider(color = ModernColors.LightBlue.copy(alpha = 0.3f))
                                            
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Text(
                                                    text = "Informasi Approval",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = ModernColors.TextSecondary
                                                )
                                                
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Person,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = if (substitute.statusPenggantian.lowercase() in listOf("dijadwalkan", "disetujui", "selesai")) 
                                                            ModernColors.SuccessGreen
                                                        else
                                                            Color(0xFFF44336)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(
                                                            text = if (substitute.statusPenggantian.lowercase() in listOf("dijadwalkan", "disetujui", "selesai")) 
                                                                "Disetujui oleh" 
                                                            else 
                                                                "Ditolak oleh",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = ModernColors.TextSecondary
                                                        )
                                                        Text(
                                                            text = substitute.approverName,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = ModernColors.TextPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Catatan Approval
                                        if (!substitute.catatanApproval.isNullOrEmpty()) {
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = ModernColors.LightBlue.copy(alpha = 0.3f),
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
                                                        tint = ModernColors.InfoBlue
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(
                                                            text = "Catatan",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ModernColors.InfoBlue
                                                        )
                                                        Text(
                                                            text = substitute.catatanApproval,
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
                val statusOptions = if (statusPenggantiOptions.isNotEmpty()) {
                    statusPenggantiOptions
                } else {
                    getStatusPenggantiOptions()
                }
                ModernFilterDialog(
                    title = "Pilih Status Penggantian",
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
}
