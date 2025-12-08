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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import com.komputerkit.aplikasimonitoringkelas.common.getStatusApprovalOptions
import com.komputerkit.aplikasimonitoringkelas.common.getJenisIzinOptions
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPermissionScreen(
    navController: NavController,
    guruViewModel: GuruViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "guru_permission"
    val permissions by guruViewModel.teacherPermissions.collectAsState(initial = emptyList())
    val isLoading by guruViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by guruViewModel.errorMessage.collectAsState(initial = null)
    val infoMessage by guruViewModel.infoMessage.collectAsState(initial = null)

    var showAddPermissionDialog by remember { mutableStateOf(false) }
    var showStatusFilter by remember { mutableStateOf(false) }
    var showJenisIzinFilter by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Semua") }
    var selectedJenisIzin by remember { mutableStateOf("Semua") }

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
            guruViewModel.applyPermissionsFilter()
            isInitialized = true
        }
    }

    // Now both filters are applied via API, no need for client-side filtering
    val filteredPermissions = permissions

    Scaffold(
        floatingActionButton = {
            ModernFAB(
                icon = Icons.Default.Add,
                onClick = { showAddPermissionDialog = true },
                modifier = Modifier.padding(bottom = 70.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppHeader(
                title = "Dashboard Guru",
                userName = userName.ifEmpty { userRole },
                userRole = userRole,
                navController = navController,
                onLogout = onLogout,
                showHomeButton = true,
                onHomeClick = { navController.navigate("guru_home") }
            )

            StandardPageTitle(
                title = "Daftar Izin",
                icon = Icons.Default.EventNote
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(ModernColors.BackgroundWhite)
                    .padding(16.dp)
            ) {
                // Info message
                infoMessage?.let { msg ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LaunchedEffect(msg) {
                        kotlinx.coroutines.delay(3000)
                        guruViewModel.clearInfo()
                    }
                }

                // Error message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Filter Row
                StandardSectionHeader("Filter Data")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StandardFilterButton(
                        label = "Status",
                        selectedValue = selectedStatus,
                        icon = Icons.Default.CheckCircle,
                        onClick = { showStatusFilter = true },
                        modifier = Modifier.weight(1f)
                    )

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
                } else if (permissions.isEmpty() && !isLoading) {
                    ModernEmptyState(
                        icon = Icons.Default.EventBusy,
                        title = "Belum Ada Data",
                        message = "Belum ada data izin",
                        onResetFilters = null
                    )
                } else if (filteredPermissions.isEmpty()) {
                    ModernEmptyState(
                        icon = Icons.Default.FilterList,
                        title = "Tidak Ada Hasil",
                        message = "Tidak ada izin yang sesuai dengan filter",
                        onResetFilters = {
                            selectedStatus = "Semua"
                            selectedJenisIzin = "Semua"
                        }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredPermissions) { permission ->
                            PermissionCard(permission = permission)
                        }
                    }
                }
            }

            GuruFooter(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    }

    // Dialog Filter Status
    if (showStatusFilter) {
        val statusOptions = getStatusApprovalOptions()
        ModernFilterDialog(
            title = "Pilih Status",
            options = statusOptions,
            selectedOption = selectedStatus,
            onDismiss = { showStatusFilter = false },
            onSelect = { selected ->
                selectedStatus = selected
                val statusValue = if (selected == "Semua") null else selected.lowercase().replace(" ", "_")
                guruViewModel.setStatusApprovalFilter(statusValue)
                guruViewModel.applyPermissionsFilter()
                showStatusFilter = false
            }
        )
    }

    // Dialog Filter Jenis Izin
    if (showJenisIzinFilter) {
        val jenisOptions = getJenisIzinOptions()
        ModernFilterDialog(
            title = "Pilih Jenis Izin",
            options = jenisOptions,
            selectedOption = selectedJenisIzin,
            onDismiss = { showJenisIzinFilter = false },
            onSelect = { selected ->
                selectedJenisIzin = selected
                val jenisValue = if (selected == "Semua") null else selected.lowercase().replace(" ", "_")
                guruViewModel.setJenisIzinFilter(jenisValue)
                guruViewModel.applyPermissionsFilter()
                showJenisIzinFilter = false
            }
        )
    }

    // Dialog Add Permission
    if (showAddPermissionDialog) {
        AddPermissionDialog(
            onDismiss = { showAddPermissionDialog = false },
            onSubmit = { alasan, tanggalMulai, tanggalSelesai, jenisIzin ->
                guruViewModel.requestPermission(alasan, tanggalMulai, tanggalSelesai, jenisIzin)
                showAddPermissionDialog = false
            }
        )
    }
}

@Composable
fun PermissionCard(permission: TeacherPermission) {
    StandardDataCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Nama Guru & Status
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
                        text = "${formatDate(permission.tanggalMulai)} s/d ${formatDate(permission.tanggalSelesai)}",
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
                        text = permission.jenisIzin.replace("_", " ").replaceFirstChar { it.uppercase() },
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
            if (permission.statusApproval.lowercase() in listOf("disetujui", "ditolak")) {
                Divider(color = ModernColors.BorderGray)
                
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Informasi Approval",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ModernColors.TextSecondary
                    )
                    
                    // Disetujui/Ditolak Oleh
                    if (permission.approverName != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
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
                                    text = if (permission.statusApproval.lowercase() == "disetujui") 
                                        "Disetujui oleh" 
                                    else 
                                        "Ditolak oleh",
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
                    
                    // Tanggal Approval
                    permission.disetujuiOleh?.let {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = ModernColors.InfoBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Disetujui Oleh ID",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ModernColors.TextSecondary
                                )
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodySmall,
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
                                        text = "Catatan Approval",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPermissionDialog(
    onDismiss: () -> Unit,
    onSubmit: (alasan: String, tanggalMulai: String, tanggalSelesai: String, jenisIzin: String) -> Unit
) {
    var alasan by remember { mutableStateOf("") }
    var tanggalMulai by remember { mutableStateOf("") }
    var tanggalSelesai by remember { mutableStateOf("") }
    var jenisIzin by remember { mutableStateOf("izin") }
    var showJenisIzinDialog by remember { mutableStateOf(false) }

    // Auto-fill today's date
    LaunchedEffect(Unit) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        tanggalMulai = today
        tanggalSelesai = today
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ajukan Izin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Jenis Izin
                OutlinedButton(
                    onClick = { showJenisIzinDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Jenis: ${jenisIzin.replace("_", " ").uppercase()}")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Tanggal Mulai
                OutlinedTextField(
                    value = tanggalMulai,
                    onValueChange = { tanggalMulai = it },
                    label = { Text("Tanggal Mulai") },
                    placeholder = { Text("yyyy-MM-dd") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                )

                // Tanggal Selesai
                OutlinedTextField(
                    value = tanggalSelesai,
                    onValueChange = { tanggalSelesai = it },
                    label = { Text("Tanggal Selesai") },
                    placeholder = { Text("yyyy-MM-dd") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    }
                )

                // Alasan
                OutlinedTextField(
                    value = alasan,
                    onValueChange = { alasan = it },
                    label = { Text("Keterangan/Alasan") },
                    placeholder = { Text("Masukkan alasan izin") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (alasan.isNotEmpty() && tanggalMulai.isNotEmpty() && tanggalSelesai.isNotEmpty()) {
                        onSubmit(alasan, tanggalMulai, tanggalSelesai, jenisIzin)
                    }
                },
                enabled = alasan.isNotEmpty() && tanggalMulai.isNotEmpty() && tanggalSelesai.isNotEmpty()
            ) {
                Text("Kirim")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )

    // Jenis Izin Dialog
    if (showJenisIzinDialog) {
        val jenisOptions = getJenisIzinOptions().filter { it != "Semua" }
        ModernFilterDialog(
            title = "Pilih Jenis Izin",
            options = jenisOptions,
            selectedOption = jenisIzin,
            onDismiss = { showJenisIzinDialog = false },
            onSelect = { selected ->
                jenisIzin = selected
                showJenisIzinDialog = false
            }
        )
    }
}
