package com.komputerkit.aplikasimonitoringkelas.kepsek

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.EventNote
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
import androidx.compose.material.icons.filled.Info
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.common.getStatusApprovalOptions
import com.komputerkit.aplikasimonitoringkelas.common.getJenisIzinOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekTeacherPermissionListScreen(
    navController: NavController,
    kepsekViewModel: KepsekViewModel
) {
    val permissions by kepsekViewModel.filteredIzinGuruList.collectAsState(initial = emptyList())
    val isLoading by kepsekViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kepsekViewModel.errorMessage.collectAsState(initial = null)
    val statusApprovalOptions by kepsekViewModel.statusApprovalOptions.collectAsState()
    val jenisIzinOptions by kepsekViewModel.jenisIzinOptions.collectAsState()

    var showJenisIzinFilter by remember { mutableStateOf(false) }
    var showStatusFilter by remember { mutableStateOf(false) }
    var selectedJenisIzin by remember { mutableStateOf("Semua") }
    var selectedStatus by remember { mutableStateOf("Semua") }

    // Update selected values when filters change (reactive)
    LaunchedEffect(kepsekViewModel.statusFilter.collectAsState().value) {
        val status = kepsekViewModel.statusFilter.value
        // Display original format from filter, no conversion needed
        selectedStatus = status ?: "Semua"
    }
    
    LaunchedEffect(kepsekViewModel.jenisIzinFilter.collectAsState().value) {
        val jenis = kepsekViewModel.jenisIzinFilter.value
        // Display original format from filter, no conversion needed
        selectedJenisIzin = jenis ?: "Semua"
    }

    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            kepsekViewModel.loadEnumOptions()
            // Load all permissions data once - filtering is reactive via combine() flow
            kepsekViewModel.loadIzinGuru(null, null, null)
            isInitialized = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        StandardPageTitle(
            title = "Izin Guru",
            icon = Icons.Default.EventBusy
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
                // Filter Status
                StandardFilterButton(
                    label = "Status",
                    selectedValue = selectedStatus,
                    icon = Icons.Default.CheckCircle,
                    onClick = { showStatusFilter = true },
                    modifier = Modifier.weight(1f)
                )

                // Filter Jenis Izin
                StandardFilterButton(
                    label = "Jenis Izin",
                    selectedValue = selectedJenisIzin,
                    icon = Icons.Default.EventNote,
                    onClick = { showJenisIzinFilter = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Permissions list
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
                if (permissions.isEmpty()) {
                    ModernEmptyState(
                        icon = Icons.Default.EventBusy,
                        title = "Belum Ada Data",
                        message = "Belum ada data izin guru",
                        onResetFilters = null
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(permissions) { permission ->
                            StandardDataCard {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Header: Nama Guru dan Status
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = permission.guruName ?: "Guru ${permission.guruId}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = ModernColors.TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        // Status Badge
                                        StandardStatusBadge(status = permission.statusApproval ?: "pending")
                                    }
                                    
                                    Divider(color = ModernColors.BorderGray)
                                    
                                    // Periode Izin
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Periode Izin",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextSecondary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = ModernColors.PrimaryBlue
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "${formatDate(permission.tanggalMulai ?: "-")} s/d ${formatDate(permission.tanggalSelesai ?: "-")}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ModernColors.TextPrimary
                                            )
                                        }
                                    }

                                    // Jenis Izin
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Jenis Izin",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextSecondary
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.EventNote,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = ModernColors.InfoBlue
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = permission.jenisIzin ?: "-",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ModernColors.TextPrimary
                                            )
                                        }
                                    }
                                    
                                // Keterangan/Alasan
                                permission.keterangan?.let { ket ->
                                    if (ket.isNotBlank()) {
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
                                
                                // Informasi Approval (jika sudah disetujui/ditolak)
                                if (permission.statusApproval.lowercase() in listOf("disetujui", "ditolak", "dijadwalkan")) {
                                    Divider(color = ModernColors.BorderGray)
                                    
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Informasi Approval",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextSecondary
                                        )
                                        
                                        // Disetujui/Ditolak Oleh
                                        if (permission.approverName != null && permission.statusApproval.lowercase() in listOf("disetujui", "ditolak")) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (permission.statusApproval.lowercase() == "disetujui") 
                                                        ModernColors.SuccessGreen
                                                    else
                                                        Color(0xFFF44336)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = if (permission.statusApproval.lowercase() == "disetujui") "Disetujui oleh" else "Ditolak oleh",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = ModernColors.TextSecondary
                                                    )
                                                    Text(
                                                        text = permission.approverName,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = ModernColors.TextPrimary
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Catatan Approval
                                        if (!permission.catatanApproval.isNullOrEmpty()) {
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
                                                            text = permission.catatanApproval,
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
        }
    }

    // Filter Dialog Status Approval dari database
    if (showStatusFilter) {
        val statusOptions = if (statusApprovalOptions.isNotEmpty()) {
            statusApprovalOptions
        } else {
            getStatusApprovalOptions()
        }
        ModernFilterDialog(
            title = "Pilih Status Approval",
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

    // Filter Dialog Jenis Izin dari database
    if (showJenisIzinFilter) {
        val jenisOptions = if (jenisIzinOptions.isNotEmpty()) {
            jenisIzinOptions
        } else {
            getJenisIzinOptions()
        }
        ModernFilterDialog(
            title = "Pilih Jenis Izin",
            options = jenisOptions,
            selectedOption = selectedJenisIzin,
            onSelect = { selected ->
                selectedJenisIzin = selected
                // Keep original format from database, only convert if it's "Semua"
                val jenisValue = if (selected == "Semua") null else selected
                kepsekViewModel.setJenisIzinFilter(jenisValue)
                // Reactive filtering will update automatically via combine() flow
                showJenisIzinFilter = false
            },
            onDismiss = { showJenisIzinFilter = false }
        )
    }
}