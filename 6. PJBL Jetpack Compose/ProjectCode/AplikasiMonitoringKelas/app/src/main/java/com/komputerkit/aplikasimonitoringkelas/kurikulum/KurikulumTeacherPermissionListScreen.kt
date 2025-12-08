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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*

// Helper functions
fun getPermissionTypeOptions(): List<String> {
    return listOf("sakit", "izin", "cuti", "dinas_luar", "lainnya")
}

fun getPermissionStatusOptions(): List<String> {
    return listOf("pending", "disetujui", "ditolak")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumTeacherPermissionListScreen(
    navController: NavController,
    kurikulumViewModel: KurikulumViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "kurikulum_permission"
    val permissionsList by kurikulumViewModel.teacherPermissions.collectAsState(initial = emptyList())
    val isLoading by kurikulumViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kurikulumViewModel.errorMessage.collectAsState(initial = null)
    val infoMessage by kurikulumViewModel.infoMessage.collectAsState(initial = null)

    val jenisIzinFilter by kurikulumViewModel.jenisIzinFilter.collectAsState(initial = null)
    val statusApprovalFilter by kurikulumViewModel.statusApprovalFilter.collectAsState(initial = null)

    var showJenisIzinFilter by remember { mutableStateOf(false) }
    var showStatusFilter by remember { mutableStateOf(false) }
    var showApprovalDialog by remember { mutableStateOf(false) }
    var selectedPermission by remember { mutableStateOf<TeacherPermission?>(null) }
    var selectedAction by remember { mutableStateOf("disetujui") }

    val selectedJenisIzin = jenisIzinFilter ?: "Semua"
    val selectedStatus = statusApprovalFilter ?: "Semua"

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
            kurikulumViewModel.loadTeacherPermissions()
            isInitialized = true
        }
    }

    // Apply filters when they change
    LaunchedEffect(jenisIzinFilter, statusApprovalFilter) {
        kurikulumViewModel.applyPermissionFilters()
    }

    // Show info message
    LaunchedEffect(infoMessage) {
        if (infoMessage != null) {
            kotlinx.coroutines.delay(3000)
            kurikulumViewModel.clearInfo()
        }
    }

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
            title = "Daftar Izin Guru",
            icon = Icons.Default.Event
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
                    label = "Jenis Izin",
                    selectedValue = selectedJenisIzin,
                    icon = Icons.Default.Category,
                    onClick = { showJenisIzinFilter = true },
                    modifier = Modifier.weight(1f)
                )
                StandardFilterButton(
                    label = "Status",
                    selectedValue = selectedStatus,
                    icon = Icons.Default.CheckCircle,
                    onClick = { showStatusFilter = true },
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

            // Info message
            infoMessage?.let { info ->
                StandardInfoCard(
                    message = info,
                    icon = Icons.Default.CheckCircle
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

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
            } else if (permissionsList.isEmpty() && !isLoading) {
                ModernEmptyState(
                    icon = Icons.Default.EventBusy,
                    title = "Belum Ada Data",
                    message = "Belum ada data izin guru",
                    onResetFilters = {
                        kurikulumViewModel.setJenisIzinFilter(null)
                        kurikulumViewModel.setStatusApprovalFilter(null)
                        kurikulumViewModel.applyPermissionFilters()
                    }
                )
            } else if (permissionsList.isEmpty()) {
                ModernEmptyState(
                    icon = Icons.Default.FilterList,
                    title = "Tidak Ada Hasil",
                    message = "Tidak ada izin guru untuk filter yang dipilih",
                    onResetFilters = {
                        kurikulumViewModel.setJenisIzinFilter(null)
                        kurikulumViewModel.setStatusApprovalFilter(null)
                        kurikulumViewModel.applyPermissionFilters()
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(permissionsList) { permission ->
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
                                        text = permission.guruName ?: "Guru ID: ${permission.guruId}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ModernColors.TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    // Status Badge
                                    StandardStatusBadge(status = permission.statusApproval)
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
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = ModernColors.PrimaryBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${permission.tanggalMulai} s/d ${permission.tanggalSelesai}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ModernColors.TextPrimary
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = ModernColors.InfoBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Durasi: ${permission.durasiHari} hari",
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
                                            imageVector = Icons.Default.Category,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = ModernColors.InfoBlue
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = permission.jenisIzin.replaceFirstChar { it.uppercase() },
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextPrimary
                                        )
                                    }
                                }

                                // Keterangan
                                if (permission.keterangan.isNotBlank()) {
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
                                                    text = permission.keterangan,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = ModernColors.TextPrimary
                                                )
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
                                        if (permission.approverName?.isNotBlank() == true) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (permission.statusApproval.lowercase() in listOf("disetujui", "dijadwalkan")) 
                                                        ModernColors.SuccessGreen
                                                    else
                                                        Color(0xFFF44336)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = if (permission.statusApproval.lowercase() in listOf("disetujui", "dijadwalkan")) 
                                                            "Disetujui oleh" 
                                                        else 
                                                            "Ditolak oleh",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = ModernColors.TextSecondary
                                                    )
                                                    Text(
                                                        text = permission.approverName ?: "Tidak ada",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = ModernColors.TextPrimary
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Tanggal Approval
                                        if (!permission.tanggalApproval.isNullOrEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DateRange,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = ModernColors.TextSecondary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "Tanggal Approval",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = ModernColors.TextSecondary
                                                    )
                                                    Text(
                                                        text = permission.tanggalApproval,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = ModernColors.TextPrimary
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Catatan Approval
                                        if (!permission.catatanApproval.isNullOrBlank()) {
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
                                                            text = "Catatan Approval",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ModernColors.InfoBlue
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
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

                                // Action buttons for pending permissions
                                if (permission.statusApproval.equals("pending", ignoreCase = true)) {
                                    Divider(color = ModernColors.BorderGray)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                selectedPermission = permission
                                                selectedAction = "disetujui"
                                                showApprovalDialog = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = ModernColors.SuccessGreen
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Setujui")
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                selectedPermission = permission
                                                selectedAction = "ditolak"
                                                showApprovalDialog = true
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = Color(0xFFD32F2F)
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tolak")
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

    // Jenis Izin Filter Dialog
    if (showJenisIzinFilter) {
        val jenisIzinOptions = listOf("Semua") + getPermissionTypeOptions()
        ModernFilterDialog(
            title = "Pilih Jenis Izin",
            options = jenisIzinOptions,
            selectedOption = selectedJenisIzin,
            onDismiss = { showJenisIzinFilter = false },
            onSelect = { jenis ->
                kurikulumViewModel.setJenisIzinFilter(if (jenis == "Semua") null else jenis)
                showJenisIzinFilter = false
            }
        )
    }

    // Status Filter Dialog
    if (showStatusFilter) {
        val statusOptions = listOf("Semua") + getPermissionStatusOptions()
        ModernFilterDialog(
            title = "Pilih Status",
            options = statusOptions,
            selectedOption = selectedStatus,
            onDismiss = { showStatusFilter = false },
            onSelect = { status ->
                kurikulumViewModel.setStatusApprovalFilter(if (status == "Semua") null else status)
                showStatusFilter = false
            }
        )
    }

    // Approval/Rejection Dialog with reason input
    if (showApprovalDialog && selectedPermission != null) {
        var reasonInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                showApprovalDialog = false
                selectedPermission = null
            },
            icon = { 
                Icon(
                    imageVector = if(selectedAction == "disetujui") Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null
                ) 
            },
            title = {
                Text(if(selectedAction == "disetujui") "Setujui Izin Guru" else "Tolak Izin Guru")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Info card
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if(selectedAction == "disetujui") 
                            ModernColors.SuccessGreen.copy(alpha = 0.1f) 
                        else 
                            Color(0xFFD32F2F).copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if(selectedAction == "disetujui") ModernColors.SuccessGreen else Color(0xFFD32F2F)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if(selectedAction == "disetujui")
                                    "Anda akan menyetujui izin guru ini"
                                else
                                    "Anda akan menolak izin guru ini",
                                style = MaterialTheme.typography.bodySmall,
                                color = ModernColors.TextPrimary
                            )
                        }
                    }

                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Catatan (Wajib)") },
                        placeholder = { Text("Masukkan catatan approval...") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = reasonInput.isBlank(),
                        supportingText = { 
                            Text(
                                text = if (reasonInput.isBlank()) "Catatan wajib diisi" else "Catatan ini akan dicatat dalam sistem",
                                color = if (reasonInput.isBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reasonInput.isBlank()) {
                            kurikulumViewModel.updateInfo("Catatan wajib diisi")
                        } else {
                            selectedPermission?.let { perm ->
                                kurikulumViewModel.updateTeacherPermissionStatus(perm.id, selectedAction, reasonInput)
                            }
                            showApprovalDialog = false
                            selectedPermission = null
                            reasonInput = ""
                        }
                    },
                    enabled = reasonInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(selectedAction == "disetujui") ModernColors.SuccessGreen else Color(0xFFD32F2F)
                    )
                ) {
                    Icon(
                        imageVector = if(selectedAction == "disetujui") Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if(selectedAction == "disetujui") "Setujui" else "Tolak")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showApprovalDialog = false
                    selectedPermission = null
                    reasonInput = ""
                }) {
                    Text("Batal")
                }
            }
        )
    }
}
