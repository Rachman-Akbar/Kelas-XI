package com.komputerkit.aplikasimonitoringkelas.guru

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.komputerkit.aplikasimonitoringkelas.common.getStatusPenggantiOptions
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruSubstituteTeacherScreen(
    navController: NavController,
    guruViewModel: GuruViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "guru_substitute"
    val allSubstitutes by guruViewModel.substitutes.collectAsState(initial = emptyList())
    val guruList by guruViewModel.guruList.collectAsState(initial = emptyList())
    val schedules by guruViewModel.schedules.collectAsState(initial = emptyList())
    val isLoading by guruViewModel.isLoading.collectAsState(initial = false)
    val errorMessage by guruViewModel.errorMessage.collectAsState(initial = null)
    val infoMessage by guruViewModel.infoMessage.collectAsState(initial = null)
    val currentGuruId by guruViewModel.currentGuruId.collectAsState(initial = null)
    val substituteRoleFilter by guruViewModel.substituteRoleFilter.collectAsState(initial = null)
    val substituteStatusFilter by guruViewModel.substituteStatusFilter.collectAsState(initial = null)

    var showAddSubstituteDialog by remember { mutableStateOf(false) }
    var showRoleFilter by remember { mutableStateOf(false) }
    var showStatusFilter by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(substituteRoleFilter ?: "Semua") }
    var selectedStatus by remember { mutableStateOf(substituteStatusFilter ?: "Semua") }

    // Update local state when ViewModel filters change
    LaunchedEffect(substituteRoleFilter, substituteStatusFilter) {
        selectedRole = substituteRoleFilter ?: "Semua"
        selectedStatus = substituteStatusFilter ?: "Semua"
    }

    // Client-side filtering with optimized recomputation
    val filteredSubstitutes by remember(allSubstitutes, selectedRole, selectedStatus, currentGuruId) {
        derivedStateOf {
            allSubstitutes.filter { substitute ->
                val roleMatch = selectedRole == "Semua" ||
                    (selectedRole == "Guru Asli" && substitute.guruAsliId == currentGuruId) ||
                    (selectedRole == "Guru Pengganti" && substitute.guruPenggantiId == currentGuruId) ||
                    selectedRole == substitute.statusPenggantian

                val statusMatch = selectedStatus == "Semua" ||
                    selectedStatus.equals(substitute.statusPenggantian, ignoreCase = true)

                roleMatch && statusMatch
            }
        }
    }

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
            guruViewModel.applySubstitutesFilter()
            guruViewModel.loadGurus()
            isInitialized = true
        }
    }

    Scaffold(
        floatingActionButton = {
            ModernFAB(
                icon = Icons.Default.Add,
                onClick = { showAddSubstituteDialog = true },
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
                title = "Guru Pengganti",
                icon = Icons.Default.People
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

                // Filter dropdowns
                StandardSectionHeader("Filter Data")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StandardFilterButton(
                        label = "Peran",
                        selectedValue = selectedRole,
                        icon = Icons.Default.SwapHoriz,
                        onClick = { showRoleFilter = true },
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

                // Substitutes list
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
                } else if (allSubstitutes.isEmpty() && !isLoading) {
                    ModernEmptyState(
                        icon = Icons.Default.People,
                        title = "Belum Ada Data",
                        message = "Belum ada data guru pengganti",
                        onResetFilters = null
                    )
                } else if (filteredSubstitutes.isEmpty()) {
                    ModernEmptyState(
                        icon = Icons.Default.FilterList,
                        title = "Tidak Ada Hasil",
                        message = "Tidak ada guru pengganti untuk filter yang dipilih",
                        onResetFilters = {
                            selectedRole = "Semua"
                            selectedStatus = "Semua"
                        }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredSubstitutes) { substitute ->
                            SubstituteTeacherCard(
                                substitute = substitute,
                                currentGuruId = currentGuruId
                            )
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

    // Dialog Filter Peran
    if (showRoleFilter) {
        val roleOptions = listOf("Semua", "Guru Asli", "Guru Pengganti")
        ModernFilterDialog(
            title = "Pilih Peran",
            options = roleOptions,
            selectedOption = selectedRole,
            onDismiss = { showRoleFilter = false },
            onSelect = { role ->
                selectedRole = role
                showRoleFilter = false
            }
        )
    }
    
    // Dialog Filter Status
    if (showStatusFilter) {
        val statusOptions = listOf("Semua") + getStatusPenggantiOptions()
        ModernFilterDialog(
            title = "Pilih Status",
            options = statusOptions,
            selectedOption = selectedStatus,
            onDismiss = { showStatusFilter = false },
            onSelect = { status ->
                selectedStatus = status
                showStatusFilter = false
            }
        )
    }

    // Dialog Add Substitute (with optional schedule selection)
    if (showAddSubstituteDialog) {
        AddSubstituteDialog(
            guruList = guruList,
            currentGuruId = currentGuruId ?: 0,
            allSchedules = schedules,
            initialDate = guruViewModel.getCurrentDate(),
            onDismiss = { showAddSubstituteDialog = false },
            onSubmit = { selectedSchedule, guruPenggantiId, tanggal, alasan ->
                if (selectedSchedule != null) {
                    guruViewModel.requestSubstituteTeacherWithSchedule(selectedSchedule, guruPenggantiId, tanggal, alasan)
                } else {
                    guruViewModel.requestSubstituteTeacher(guruPenggantiId, tanggal, alasan)
                }
                showAddSubstituteDialog = false
            }
        )
    }
}

@Composable
fun SubstituteTeacherCard(
    substitute: SubstituteTeacher,
    currentGuruId: Int?
) {
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
                    text = formatDate(substitute.tanggal),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ModernColors.TextPrimary
                )
            }
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

        // Guru Asli
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (substitute.guruAsliId == currentGuruId) 
                    ModernColors.PrimaryBlue 
                else 
                    ModernColors.InfoBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Guru Asli",
                    style = MaterialTheme.typography.labelSmall,
                    color = ModernColors.TextSecondary
                )
                Text(
                    text = substitute.namaGuruAsli,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (substitute.guruAsliId == currentGuruId) 
                        FontWeight.Bold 
                    else 
                        FontWeight.Normal,
                    color = ModernColors.TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Guru Pengganti
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (substitute.guruPenggantiId == currentGuruId) 
                    ModernColors.PrimaryBlue 
                else 
                    ModernColors.InfoBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Guru Pengganti",
                    style = MaterialTheme.typography.labelSmall,
                    color = ModernColors.TextSecondary
                )
                Text(
                    text = substitute.namaGuruPengganti,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (substitute.guruPenggantiId == currentGuruId) 
                        FontWeight.Bold 
                    else 
                        FontWeight.Normal,
                    color = ModernColors.TextPrimary
                )
            }
        }
        
        // Keterangan dari Guru (saat entry)
        substitute.keterangan?.let { ket ->
            if (ket.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                
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
        if (substitute.approverName != null && substitute.statusPenggantian.lowercase() in listOf("dijadwalkan", "disetujui", "selesai", "ditolak")) {
            Spacer(modifier = Modifier.height(8.dp))
            
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
            Spacer(modifier = Modifier.height(8.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubstituteDialog(
    guruList: List<com.komputerkit.aplikasimonitoringkelas.data.models.Guru>,
    currentGuruId: Int,
    allSchedules: List<Schedule>,
    initialDate: String,
    onDismiss: () -> Unit,
    onSubmit: (selectedSchedule: Schedule?, guruPenggantiId: Int, tanggal: String, alasan: String) -> Unit
) {
    var selectedGuru by remember { mutableStateOf<com.komputerkit.aplikasimonitoringkelas.data.models.Guru?>(null) }
    var tanggal by remember { mutableStateOf(initialDate) }
    var alasan by remember { mutableStateOf("") }
    var showGuruDialog by remember { mutableStateOf(false) }
    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Filter out current guru from the list
    val availableGurus = guruList.filter { it.id != currentGuruId }

    // Filter schedules based on selected date's day (and optionally kelas/guru)
    val schedulesForDay = remember(tanggal, allSchedules) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale("id"))
        val parsedDate = try { dateFormat.parse(tanggal) } catch (e: Exception) { Date() }
        val dayName = dayFormat.format(parsedDate ?: Date())
        allSchedules.filter { it.hari.equals(dayName, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Ajukan Guru Pengganti",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Guru Pengganti
                OutlinedButton(
                    onClick = { showGuruDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedGuru?.nama ?: "Pilih Guru Pengganti",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Tanggal (click to pick)
                OutlinedTextField(
                    value = tanggal,
                    onValueChange = { },
                    label = { Text("Tanggal") },
                    placeholder = { Text("yyyy-MM-dd") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Schedule selection (wajib)
                OutlinedButton(
                    onClick = { if (schedulesForDay.isNotEmpty()) showScheduleDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = schedulesForDay.isNotEmpty()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(selectedSchedule?.let { "${it.mapel} - ${it.kelasName}" } ?: "Pilih Jadwal")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                if (schedulesForDay.isEmpty()) {
                    Text(
                        text = "Tidak ada jadwal untuk tanggal yang dipilih. Pilih tanggal lain.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Alasan
                OutlinedTextField(
                    value = alasan,
                    onValueChange = { alasan = it },
                    label = { Text("Alasan Penggantian") },
                    placeholder = { Text("Masukkan alasan penggantian") },
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
                    selectedGuru?.let { guru ->
                        // Selected schedule must be present now
                        if (tanggal.isNotEmpty() && alasan.isNotEmpty() && selectedSchedule != null) {
                            onSubmit(selectedSchedule, guru.id, tanggal, alasan)
                        }
                    }
                },
                enabled = selectedGuru != null && tanggal.isNotEmpty() && alasan.isNotEmpty() && selectedSchedule != null
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

    // Guru Selection Dialog
    if (showGuruDialog) {
        AlertDialog(
            onDismissRequest = { showGuruDialog = false },
            title = {
                Text(
                    text = "Pilih Guru Pengganti",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableGurus) { guru ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedGuru?.id == guru.id)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            onClick = {
                                selectedGuru = guru
                                showGuruDialog = false
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = guru.nama,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "NIP: ${guru.nip}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (selectedGuru?.id == guru.id) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGuruDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Schedule Selection Dialog (optional)
    if (showScheduleDialog) {
        val options = schedulesForDay.map { schedule -> "${schedule.mapel} - ${schedule.kelasName}" }
        ModernFilterDialog(
            title = "Pilih Jadwal",
            options = options,
            selectedOption = selectedSchedule?.let { "${it.mapel} - ${it.kelasName}" } ?: "",
            onSelect = { selectedOption ->
                val found = schedulesForDay.find { "${it.mapel} - ${it.kelasName}" == selectedOption }
                selectedSchedule = found
                showScheduleDialog = false
            },
            onDismiss = { showScheduleDialog = false }
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try { calendar.time = dateFormat.parse(tanggal) ?: Date() } catch (e: Exception) { calendar.time = Date() }

        val datePickerDialog = android.app.DatePickerDialog(
            androidx.compose.ui.platform.LocalContext.current,
            { _, year, month, dayOfMonth ->
                val newDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                tanggal = newDate
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        DisposableEffect(Unit) {
            datePickerDialog.show()
            onDispose { datePickerDialog.dismiss() }
        }
    }
}
