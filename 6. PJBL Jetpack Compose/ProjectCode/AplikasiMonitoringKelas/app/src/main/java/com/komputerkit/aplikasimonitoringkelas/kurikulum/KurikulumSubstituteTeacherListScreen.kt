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

// Helper function
fun getSubstituteStatusOptions(): List<String> {
    return listOf("pending", "dijadwalkan", "selesai", "tidak_hadir")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumSubstituteTeacherListScreen(
    navController: NavController,
    kurikulumViewModel: KurikulumViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "kurikulum_substitute"
    val substituteList by kurikulumViewModel.substituteTeachers.collectAsState(initial = emptyList())
    val classList by kurikulumViewModel.classList.collectAsState(initial = emptyList())
    val isLoading by kurikulumViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by kurikulumViewModel.errorMessage.collectAsState(initial = null)
    val infoMessage by kurikulumViewModel.infoMessage.collectAsState(initial = null)

    var showStatusFilter by remember { mutableStateOf(false) }
    var showClassFilter by remember { mutableStateOf(false) }
    val statusFilter by kurikulumViewModel.statusFilter.collectAsState(initial = null)
    val kelasIdFilter by kurikulumViewModel.kelasIdFilter.collectAsState(initial = null)

    val selectedStatus = statusFilter ?: "Semua"
    val selectedClass = kelasIdFilter?.let { kelasId ->
        classList.find { it.id == kelasId }?.nama
    } ?: "Semua"
    var showStatusUpdateDialog by remember { mutableStateOf(false) }
    var selectedSubstitute by remember { mutableStateOf<SubstituteTeacher?>(null) }
    var selectedAction by remember { mutableStateOf("dijadwalkan") } // "dijadwalkan" or "ditolak"

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
            kurikulumViewModel.loadSubstituteTeachers()
            isInitialized = true
        }
    }

    // Use the substituteList directly as it's already filtered by the ViewModel
    val filteredList = substituteList
    
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
            title = "Daftar Guru Pengganti",
            icon = Icons.Default.PersonAddAlt1
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
                    label = "Status ",
                    selectedValue = selectedStatus,
                    icon = Icons.Default.SwapHoriz,
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
            
            // Info message
            infoMessage?.let { info ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ModernColors.SuccessGreen.copy(alpha = 0.1f))
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
                            tint = ModernColors.SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = info,
                            color = ModernColors.TextPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Substitute list
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
            } else if (substituteList.isEmpty() && !isLoading) {
                ModernEmptyState(
                    icon = Icons.Default.PersonOff,
                    title = "Belum Ada Data",
                    message = "Belum ada data guru pengganti",
                    onResetFilters = null
                )
            } else if (filteredList.isEmpty() && selectedStatus != "Semua") {
                ModernEmptyState(
                    icon = Icons.Default.FilterList,
                    title = "Tidak Ada Hasil",
                    message = "Tidak ada data guru pengganti untuk filter yang dipilih",
                    onResetFilters = {
                        kurikulumViewModel.setStatusFilter(null)
                        kurikulumViewModel.setKelasIdFilter(null)
                        kurikulumViewModel.applySubstituteFilters()
                    }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { substitute ->
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
                                        text = substitute.kelasName ?: "Kelas ${substitute.jadwalId}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ModernColors.TextPrimary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    StandardStatusBadge(status = substitute.statusPenggantian)
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
                                        text = substitute.tanggal,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ModernColors.TextPrimary
                                    )
                                }
                            
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                // Original Teacher
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color(0xFFD32F2F)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Guru Asli:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ModernColors.TextSecondary
                                        )
                                        Text(
                                            text = substitute.namaGuruAsli,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextPrimary
                                        )
                                    }
                                }

                                Divider(color = ModernColors.LightBlue.copy(alpha = 0.3f))

                                // Substitute Teacher
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PersonAddAlt1,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = ModernColors.SuccessGreen
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Guru Pengganti:",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = ModernColors.TextSecondary
                                        )
                                        Text(
                                            text = substitute.namaGuruPengganti,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextPrimary
                                        )
                                    }
                                }

                                // Class Info
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Class,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = ModernColors.PrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = substitute.kelasName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = ModernColors.TextPrimary
                                    )
                                }

                                // Keterangan dari Guru (saat entry)
                                if (substitute.keterangan.isNotBlank()) {
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
                                                    text = substitute.keterangan,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = ModernColors.TextPrimary
                                                )
                                            }
                                        }
                                    }
                                }

                                // Informasi Approval (jika sudah disetujui/ditolak)
                                if (substitute.statusPenggantian.lowercase() in listOf("dijadwalkan", "selesai", "ditolak", "tidak_hadir")) {
                                    Divider(color = ModernColors.LightBlue.copy(alpha = 0.3f))
                                    
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = "Informasi Approval",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ModernColors.TextSecondary
                                        )
                                        
                                        // Disetujui/Ditolak Oleh
                                        if (!substitute.approverName.isNullOrEmpty()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = if (substitute.statusPenggantian.lowercase() in listOf("dijadwalkan", "selesai")) 
                                                        ModernColors.SuccessGreen
                                                    else
                                                        Color(0xFFF44336)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = if (substitute.statusPenggantian.lowercase() in listOf("dijadwalkan", "selesai")) 
                                                            "Disetujui oleh" 
                                                        else 
                                                            "Ditolak oleh",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = ModernColors.TextSecondary
                                                    )
                                                    Text(
                                                        text = substitute.approverName ?: "Tidak ada",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = ModernColors.TextPrimary
                                                    )
                                                }
                                            }
                                        }
                                        
                                        // Catatan Approval dari Kurikulum
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
                                                            text = "Catatan Approval",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ModernColors.InfoBlue
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
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

                                // Action buttons for approval (only for pending status)
                                if (substitute.statusPenggantian == "pending") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                selectedSubstitute = substitute
                                                selectedAction = "dijadwalkan"
                                                showStatusUpdateDialog = true
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
                                                selectedSubstitute = substitute
                                                selectedAction = "ditolak"
                                                showStatusUpdateDialog = true
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

    // Status Update Dialog for pending substitute teachers (approval only)
    selectedSubstitute?.let { sub ->
        if (showStatusUpdateDialog && sub.statusPenggantian == "pending") {
            var reasonInput by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = {
                    showStatusUpdateDialog = false
                    selectedSubstitute = null
                },
                icon = { 
                    Icon(
                        imageVector = if(selectedAction == "dijadwalkan") Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if(selectedAction == "dijadwalkan") ModernColors.SuccessGreen else Color(0xFFD32F2F)
                    )
                },
                title = {
                    Text(if(selectedAction == "dijadwalkan") "Setujui Guru Pengganti" else "Tolak Guru Pengganti")
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Info card
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = if(selectedAction == "dijadwalkan") 
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
                                    tint = if(selectedAction == "dijadwalkan") ModernColors.SuccessGreen else Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if(selectedAction == "dijadwalkan")
                                        "Anda akan menyetujui jadwal penggantian ini"
                                    else
                                        "Anda akan menolak jadwal penggantian ini",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ModernColors.TextPrimary
                                )
                            }
                        }

                        OutlinedTextField(
                            value = reasonInput,
                            onValueChange = { reasonInput = it },
                            label = { Text("Catatan (Wajib)") },
                            placeholder = { Text("Masukkan catatan...") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = reasonInput.isBlank(),
                            supportingText = { 
                                val message = if (reasonInput.isBlank()) "Catatan wajib diisi" else "Catatan ini akan dicatat dalam sistem"
                                val color = if (reasonInput.isBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                Text(text = message, color = color)
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
                                kurikulumViewModel.updateSubstituteTeacherStatus(sub.id, selectedAction, reasonInput)
                                showStatusUpdateDialog = false
                                selectedSubstitute = null
                                reasonInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(selectedAction == "dijadwalkan") ModernColors.SuccessGreen else Color(0xFFD32F2F)
                        )
                    ) {
                        Icon(
                            imageVector = if(selectedAction == "dijadwalkan") Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if(selectedAction == "dijadwalkan") "Setujui" else "Tolak")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showStatusUpdateDialog = false
                        selectedSubstitute = null
                        reasonInput = ""
                    }) {
                        Text("Batal")
                    }
                }
            )
        }
    }

    // Status Filter Dialog
    if (showStatusFilter) {
        val statusOptions = listOf("Semua", "pending", "dijadwalkan", "selesai", "tidak_hadir", "ditolak")
        ModernFilterDialog(
            title = "Pilih Status",
            options = statusOptions,
            selectedOption = selectedStatus,
            onDismiss = { showStatusFilter = false },
            onSelect = { status ->
                // Set filter and reload data through ViewModel
                kurikulumViewModel.setStatusFilter(if (status == "Semua") null else status)
                kurikulumViewModel.applySubstituteFilters()
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
            onSelect = { kelasName ->
                // Set filter and reload data through ViewModel
                val kelasId = if (kelasName == "Semua") null else classList.find { it.nama == kelasName }?.id
                kurikulumViewModel.setKelasIdFilter(kelasId)
                kurikulumViewModel.applySubstituteFilters()
                showClassFilter = false
            }
        )
    }
}
