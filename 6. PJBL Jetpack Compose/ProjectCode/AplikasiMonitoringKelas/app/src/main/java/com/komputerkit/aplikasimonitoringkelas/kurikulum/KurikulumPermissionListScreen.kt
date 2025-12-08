package com.komputerkit.aplikasimonitoringkelas.kurikulum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.Permission
import com.komputerkit.aplikasimonitoringkelas.common.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumPermissionListScreen(
    navController: NavController,
    kurikulumViewModel: KurikulumViewModel
) {
    val permissions by kurikulumViewModel.permissions.collectAsState(initial = emptyList())
    val dayFilter by kurikulumViewModel.dayFilter.collectAsState(initial = null)
    val infoMessage by kurikulumViewModel.infoMessage.collectAsState(initial = null)

    var showDayFilter by remember { mutableStateOf(false) }
    var showStatusFilter by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableStateOf(dayFilter ?: "Hari Ini") }
    var selectedStatusFilter by remember { mutableStateOf("Semua Status") }
    var hideOldPermissions by remember { mutableStateOf(false) }
    var customDate by remember { mutableStateOf("") }
    var showCustomDateInput by remember { mutableStateOf(false) }
    var showApprovalDialog by remember { mutableStateOf(false) }
    var selectedPermission by remember { mutableStateOf<Permission?>(null) }
    var approvalAction by remember { mutableStateOf("disetujui") }

    LaunchedEffect(Unit) {
        kurikulumViewModel.loadPermissions()
    }
    
    // Show info message
    LaunchedEffect(infoMessage) {
        if (infoMessage != null) {
            kotlinx.coroutines.delay(3000)
            kurikulumViewModel.clearInfo()
        }
    }

    // Filter permissions based on selected status
    val filteredPermissions = if (selectedStatusFilter == "Semua Status") {
        permissions
    } else {
        permissions.filter { it.status == selectedStatusFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Info message
        infoMessage?.let { info ->
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
                        text = info,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Filter chips and options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.material3.FilterChip(
                selected = selectedDay != "Hari Ini",
                onClick = { showDayFilter = true },
                enabled = true,
                label = { Text(selectedDay) }
            )
            
            androidx.compose.material3.FilterChip(
                selected = selectedStatusFilter != "Semua Status",
                onClick = { showStatusFilter = true },
                enabled = true,
                label = { Text(selectedStatusFilter) }
            )
            
            // Hide old permissions toggle
            androidx.compose.material3.FilterChip(
                selected = hideOldPermissions,
                onClick = { hideOldPermissions = !hideOldPermissions },
                enabled = true,
                label = { Text("Sembunyikan") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Permissions list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredPermissions) { permission ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Guru ID: ${permission.guruId}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tanggal: ${permission.tanggal}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Alasan: ${permission.alasan}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Status: ${Utils.getStatusText(permission.status)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = when (permission.status) {
                                    "approved" -> MaterialTheme.colorScheme.primary
                                    "pending" -> MaterialTheme.colorScheme.secondary
                                    "rejected" -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                            
                            // Action buttons for pending permissions
                            if (permission.status == "pending") {
                                Row {
                                    Button(
                                        onClick = { 
                                            selectedPermission = permission
                                            approvalAction = "disetujui"
                                            showApprovalDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Setujui")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { 
                                            selectedPermission = permission
                                            approvalAction = "ditolak"
                                            showApprovalDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                    ) {
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

    // Day Filter Dialog
    if (showDayFilter) {
        AlertDialog(
            onDismissRequest = { showDayFilter = false },
            title = { Text("Filter Hari") },
            text = {
                Column {
                    listOf("Hari Ini", "Kemarin", "Minggu Ini", "Bulan Ini", "Pilih Tanggal").forEach { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    if (day == "Pilih Tanggal") {
                                        showCustomDateInput = true
                                    } else {
                                        selectedDay = day
                                        kurikulumViewModel.setDayFilter(if (day == "Hari Ini") null else day)
                                        showCustomDateInput = false
                                        showDayFilter = false
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDay == day,
                                onClick = {
                                    if (day == "Pilih Tanggal") {
                                        showCustomDateInput = true
                                    } else {
                                        selectedDay = day
                                        kurikulumViewModel.setDayFilter(if (day == "Hari Ini") null else day)
                                        showCustomDateInput = false
                                        showDayFilter = false
                                    }
                                }
                            )
                            Text(text = day)
                        }
                    }
                    if (showCustomDateInput) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customDate,
                            onValueChange = { customDate = it },
                            label = { Text("Tanggal (YYYY-MM-DD)") },
                            placeholder = { Text("2025-11-26") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    if (showCustomDateInput && customDate.isNotEmpty()) {
                        selectedDay = customDate
                        kurikulumViewModel.setDayFilter(customDate)
                    }
                    showDayFilter = false
                    showCustomDateInput = false
                }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Status Filter Dialog
    if (showStatusFilter) {
        AlertDialog(
            onDismissRequest = { showStatusFilter = false },
            title = { Text("Filter Status") },
            text = {
                Column {
                    listOf("Semua Status", "approved", "pending", "rejected").forEach { statusOption ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    selectedStatusFilter = statusOption
                                    showStatusFilter = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedStatusFilter == statusOption,
                                onClick = {
                                    selectedStatusFilter = statusOption
                                    showStatusFilter = false
                                }
                            )
                            Text(text = when(statusOption) {
                                "Semua Status" -> statusOption
                                "approved" -> "Disetujui"
                                "pending" -> "Menunggu"
                                "rejected" -> "Ditolak"
                                else -> statusOption
                            })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStatusFilter = false }) {
                    Text("Tutup")
                }
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
            icon = { Icon(Icons.Default.FilterList, contentDescription = null) },
            title = {
                Text(if (approvalAction == "disetujui") "Setujui Izin" else "Tolak Izin")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "ID Izin: ${selectedPermission!!.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tanggal: ${selectedPermission!!.tanggal}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Alasan: ${selectedPermission!!.alasan}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = { reasonInput = it },
                        label = { Text("Catatan *") },
                        placeholder = { Text("Masukkan catatan ${if (approvalAction == "disetujui") "persetujuan" else "penolakan"}...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        supportingText = { Text("Catatan wajib diisi dan akan dicatat dalam sistem") }
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
                                kurikulumViewModel.updateTeacherPermissionStatus(perm.id, approvalAction, reasonInput)
                            }
                            showApprovalDialog = false
                            selectedPermission = null
                            reasonInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if(approvalAction == "disetujui") 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(if(approvalAction == "disetujui") "Setujui" else "Tolak")
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

fun getStatusText(status: String): String {
    return when (status) {
        "approved" -> "Disetujui"
        "pending" -> "Menunggu"
        "rejected" -> "Ditolak"
        else -> status
    }
}