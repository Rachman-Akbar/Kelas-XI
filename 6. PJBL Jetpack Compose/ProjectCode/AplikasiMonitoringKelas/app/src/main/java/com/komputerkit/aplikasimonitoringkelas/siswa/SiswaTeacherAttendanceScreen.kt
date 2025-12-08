package com.komputerkit.aplikasimonitoringkelas.siswa

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*

private fun getCurrentDate(): String {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return dateFormat.format(java.util.Date())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaTeacherAttendanceScreen(
    navController: NavController,
    siswaViewModel: SiswaViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "siswa_teacher_attendance"
    val attendances by siswaViewModel.teacherAttendances.collectAsState()
    val userKelasId by siswaViewModel.userKelasId.collectAsState()
    val userKelasName by siswaViewModel.userKelasName.collectAsState()
    val statusFilter by siswaViewModel.statusFilter.collectAsState()
    val schedules by siswaViewModel.schedules.collectAsState()
    val isLoading by siswaViewModel.isLoading.collectAsState()
    val errorMessage by siswaViewModel.errorMessage.collectAsState()

    var showStatusFilter by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }
    var showAddAttendanceDialog by remember { mutableStateOf(false) }
    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }
    var selectedStatus by remember { mutableStateOf("hadir") }
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var keterangan by remember { mutableStateOf("") }
    var showScheduleSelectionDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    // Multi-selection state
    var selectedItems by remember { mutableStateOf<Set<Int>>(setOf()) }

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }

    // Get user info
    val context = androidx.compose.ui.platform.LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            try {
                val authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
                userName = authRepo.getUserName()
                userRole = authRepo.getUserRole()
                isInitialized = true
            } catch (e: Exception) {
                android.util.Log.e("SiswaTeacherAttendanceScreen", "Error loading user info", e)
            }
        }
    }

    // Load data when userKelasId becomes available
    LaunchedEffect(userKelasId) {
        val localKelasId = userKelasId
        if (localKelasId != null) {
            android.util.Log.d("SiswaTeacherAttendanceScreen", "Loading teacher attendance for kelas_id: $localKelasId")
            siswaViewModel.loadTeacherAttendances(localKelasId, forceRefresh = true)
            // Also load schedules for the class to show in the add attendance dialog
            siswaViewModel.loadSchedules(localKelasId, forceRefresh = true)
        } else {
            android.util.Log.e("SiswaTeacherAttendanceScreen", "userKelasId is NULL - waiting for it to be loaded from DataStore")
        }
    }

    // Show snackbar when there's a success message
    LaunchedEffect(showSuccessMessage, successMessage) {
        if (showSuccessMessage && successMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(
                message = successMessage,
                duration = SnackbarDuration.Short
            )
            showSuccessMessage = false
            successMessage = ""
        }
    }

    // Show error message as snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { error ->
            if (error.isNotEmpty()) {
                snackbarHostState.showSnackbar(
                    message = error,
                    duration = SnackbarDuration.Long
                )
                // Clear error after showing
                siswaViewModel.setErrorMessage(null)
            }
        }
    }

    // Filter attendances by kelas_id (via schedules) AND status
    val filteredAttendances = remember(attendances, statusFilter, schedules, userKelasId) {
        var filtered = attendances

        // Filter by kelas via schedule lookup - capture userKelasId in local variable for lambda
        val localKelasId = userKelasId
        if (localKelasId != null) {
            filtered = filtered.filter { att ->
                schedules.find { it.id == att.jadwalId }?.kelasId == localKelasId
            }
            android.util.Log.d("SiswaTeacherAttendanceScreen", "Attendances after kelas filter: ${filtered.size}")
        }

        // Then filter by status if requested
        if (statusFilter.isNullOrEmpty() || statusFilter == "Semua") {
            filtered
        } else {
            filtered.filter { it.statusKehadiran?.equals(statusFilter, ignoreCase = true) == true }
        }
    }

    // Toggle selection of an item
    fun toggleItemSelection(id: Int) {
        selectedItems = if (selectedItems.contains(id)) {
            selectedItems - id
        } else {
            selectedItems + id
        }
    }

    // Select all items in the current view
    fun selectAllItems() {
        selectedItems = filteredAttendances.map { it.id ?: 0 }.toSet()
    }

    // Clear all selections
    fun clearAllSelections() {
        selectedItems = setOf()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Show multi-selection toolbar when items are selected
            if (selectedItems.isNotEmpty()) {
                TopAppBar(
                    title = {
                        Text("${selectedItems.size} item dipilih")
                    },
                    navigationIcon = {
                        IconButton(onClick = { clearAllSelections() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (selectedItems.size == filteredAttendances.size) {
                                clearAllSelections()
                            } else {
                                selectAllItems()
                            }
                        }) {
                            Icon(
                                if (selectedItems.size == filteredAttendances.size) Icons.Default.SelectAll else Icons.Default.Checklist,
                                contentDescription = if (selectedItems.size == filteredAttendances.size) "Deselect All" else "Select All"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = ModernColors.PrimaryBlue,
                        titleContentColor = ModernColors.TextWhite,
                        navigationIconContentColor = ModernColors.TextWhite
                    )
                )
            }
        },
        floatingActionButton = {
            ModernFAB(
                icon = Icons.Default.Add,
                onClick = {
                    android.util.Log.d("SiswaTeacherAttendance", "FAB clicked - opening dialog")
                    showAddAttendanceDialog = true
                },
                modifier = Modifier.padding(bottom = 80.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ModernColors.BackgroundWhite)
        ) {
            if (selectedItems.isEmpty()) {
                AppHeader(
                    title = if (userKelasName != null) "Kehadiran Guru - $userKelasName" else if (userKelasId != null) "Kehadiran Guru - Kelas ID: $userKelasId" else "Kehadiran Guru",
                    userName = userName.ifEmpty { userRole },
                    userRole = userRole,
                    navController = navController,
                    onLogout = onLogout
                )
            }

            StandardPageTitle(
                title = "Kehadiran Guru",
                icon = Icons.Default.Person
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(paddingValues)
            ) {
                StandardSectionHeader(title = "Filter")

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StandardFilterButton(
                        label = "Status",
                        selectedValue = statusFilter ?: "Semua",
                        icon = Icons.Default.FilterList,
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

                // Teacher Attendance List
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
                                text = "Memuat kehadiran guru...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ModernColors.TextSecondary
                            )
                        }
                    }
                } else if (filteredAttendances.isEmpty()) {
                    ModernEmptyState(
                        icon = Icons.Default.EventBusy,
                        title = "Belum Ada Data",
                        message = "Belum ada data kehadiran guru",
                        onResetFilters = null
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredAttendances) { attendance ->
                            TeacherAttendanceCardItem(
                                attendance = attendance,
                                isSelected = selectedItems.contains(attendance.id ?: 0),
                                onItemSelection = { id ->
                                    toggleItemSelection(id)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }

            SiswaFooter(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    }

    // Status Filter Dialog
    if (showStatusFilter) {
        val statusOptions = listOf("Semua", "hadir", "tidak_hadir", "izin", "sakit", "telat")
        ModernFilterDialog(
            title = "Pilih Status",
            options = statusOptions,
            selectedOption = statusFilter ?: "Semua",
            onSelect = { selected ->
                siswaViewModel.setStatusFilter(if (selected == "Semua") null else selected)
                showStatusFilter = false
            },
            onDismiss = { showStatusFilter = false }
        )
    }

    // Add Teacher Attendance Dialog
    if (showAddAttendanceDialog) {
        android.util.Log.d("SiswaTeacherAttendance", "Dialog opened - schedules: ${schedules.size}")
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale("id"))
        val parsedDate = try {
            dateFormat.parse(selectedDate)
        } catch (e: Exception) {
            java.util.Date()
        }
        val dayName = dayFormat.format(parsedDate ?: java.util.Date())
        val todaySchedules = schedules.filter { it.hari.equals(dayName, ignoreCase = true) }

        android.util.Log.d("SiswaTeacherAttendance", "Selected date: $selectedDate, Day name: $dayName, Total schedules: ${schedules.size}, Filtered schedules: ${todaySchedules.size}")
        todaySchedules.forEach { schedule ->
            android.util.Log.d("SiswaTeacherAttendance", "Schedule: ${schedule.mapel} - ${schedule.hari}")
        }

        TeacherAttendanceAddDialog(
            kelasId = userKelasId,
            onDismiss = {
                showAddAttendanceDialog = false
                // Reset values
                selectedSchedule = null
                selectedStatus = "hadir"
                keterangan = ""
            },
            onSubmit = { schedule, status, date, desc ->
                android.util.Log.d("SiswaTeacherAttendance", "=== onSubmit CALLED ===")
                android.util.Log.d("SiswaTeacherAttendance", "schedule: ${schedule?.mapel}, status: '$status', date: $date")
                android.util.Log.d("SiswaTeacherAttendance", "Status length: ${status.length}, isEmpty: ${status.isEmpty()}, isBlank: ${status.isBlank()}")
                
                // Validate status is not empty
                if (status.isBlank()) {
                    android.util.Log.e("SiswaTeacherAttendance", "Status is blank!")
                    siswaViewModel.setErrorMessage("Status kehadiran harus dipilih")
                    return@TeacherAttendanceAddDialog
                }
                
                // If no schedule selected, use first available schedule for this date
                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale("id"))
                val parsedDate = try { dateFormat.parse(date) } catch (e: Exception) { java.util.Date() }
                val dayName = dayFormat.format(parsedDate ?: java.util.Date())
                val schedulesForDay = schedules.filter { it.hari.equals(dayName, ignoreCase = true) }
                val actualSchedule = schedule ?: schedulesForDay.firstOrNull()
                
                android.util.Log.d("SiswaTeacherAttendance", "actualSchedule: ${actualSchedule?.mapel}, guruId: ${actualSchedule?.guruId}, jadwalId: ${actualSchedule?.id}, schedulesForDay: ${schedulesForDay.size}")
                
                if (actualSchedule != null) {
                    android.util.Log.d("SiswaTeacherAttendance", "Calling submitTeacherAttendance with status: '$status'")
                    siswaViewModel.submitTeacherAttendance(
                        guruId = actualSchedule.guruId,
                        jadwalId = actualSchedule.id,
                        tanggal = date,
                        statusKehadiran = status,
                        keterangan = if (desc.isBlank()) null else desc,
                        onSuccess = {
                            android.util.Log.d("SiswaTeacherAttendance", "Submit SUCCESS")
                            showAddAttendanceDialog = false
                            successMessage = "✅ Kehadiran guru berhasil disimpan!"
                            showSuccessMessage = true
                            // Reset form
                            selectedSchedule = null
                            selectedStatus = "hadir"
                            keterangan = ""
                        },
                        onError = { error ->
                            android.util.Log.e("SiswaTeacherAttendance", "Submit ERROR: $error")
                            showAddAttendanceDialog = false
                            
                            // Parse error message for better user feedback
                            val userMessage = when {
                                error.contains("sudah ada", ignoreCase = true) || 
                                error.contains("already exists", ignoreCase = true) ||
                                error.contains("duplicate", ignoreCase = true) -> 
                                    "❌ Data kehadiran sudah ada untuk jadwal dan tanggal ini"
                                error.contains("invalid", ignoreCase = true) -> 
                                    "❌ Status kehadiran tidak valid. Silakan coba lagi"
                                error.contains("network", ignoreCase = true) || 
                                error.contains("timeout", ignoreCase = true) -> 
                                    "❌ Koneksi bermasalah. Periksa internet Anda"
                                else -> "❌ Gagal menyimpan: ${error.take(100)}"
                            }
                            
                            siswaViewModel.setErrorMessage(userMessage)
                        }
                    )
                } else {
                    android.util.Log.e("SiswaTeacherAttendance", "No schedule available for $dayName")
                    siswaViewModel.setErrorMessage("Tidak ada jadwal untuk hari $dayName. Silakan pilih tanggal dengan jadwal yang tersedia atau hubungi admin.")
                    showAddAttendanceDialog = false
                }
            },
            allSchedules = schedules,
            initialDate = selectedDate
        )
    }
}

@Composable
fun TeacherAttendanceCardItem(
    attendance: TeacherAttendance,
    isSelected: Boolean = false,
    onItemSelection: (Int) -> Unit = {}
) {
    StandardDataCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Tanggal dan Status - same as kepsek
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Checkbox for selection
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            onItemSelection(attendance.id ?: 0)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = formatDate(attendance.tanggal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ModernColors.TextPrimary
                        )
                        attendance.guruName?.let { guruName ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = guruName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ModernColors.TextSecondary
                            )
                        }
                    }
                }
                StandardStatusBadge(status = attendance.statusKehadiran ?: "belum_ada_data")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = ModernColors.BorderGray)
            Spacer(modifier = Modifier.height(8.dp))

            // Details Section - same as kepsek
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status details
                attendance.statusKehadiran?.let { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = getTeacherStatusColor(status)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getTeacherStatusDisplayName(status),
                            style = MaterialTheme.typography.bodyMedium,
                            color = getTeacherStatusColor(status)
                        )
                    }
                }

                // Kelas (if available)
                attendance.kelasName?.let { kelas ->
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

                // Waktu Datang (if available)
                attendance.waktuDatang?.let { waktu ->
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

                // Durasi Keterlambatan (if available and positive)
                attendance.durasiKeterlambatan?.let { durasi ->
                    if (durasi > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = ModernColors.ErrorRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Terlambat: ${durasi} menit",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ModernColors.ErrorRed
                            )
                        }
                    }
                }

                // Keterangan (if available and not empty)
                attendance.keterangan?.let { ket ->
                    if (ket.isNotBlank()) {
                        StandardInfoCard(
                            message = "Keterangan: $ket",
                            icon = Icons.Default.Info
                        )
                    }
                }
            }
        }
    }
}

fun getTeacherStatusDisplayName(status: String): String {
    return when (status.lowercase()) {
        "hadir" -> "Hadir"
        "tidak_hadir" -> "Tidak Hadir"
        "izin", "izin_" -> "Izin"
        "sakit" -> "Sakit"
        "telat", "terlambat" -> "Terlambat"
        "pending", "disetujui", "ditolak" -> status.replace("_", " ").replaceFirstChar { it.uppercase() }
        else -> status.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}

fun getTeacherStatusColor(status: String): androidx.compose.ui.graphics.Color {
    return when (status.lowercase()) {
        "hadir" -> ModernColors.SuccessGreen
        "tidak_hadir", "ditolak" -> ModernColors.ErrorRed
        "izin", "sakit", "telat", "terlambat", "pending" -> ModernColors.WarningAmber
        else -> ModernColors.TextSecondary
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherAttendanceAddDialog(
    kelasId: Int?,
    onDismiss: () -> Unit,
    onSubmit: (Schedule?, String, String, String) -> Unit,
    allSchedules: List<Schedule>,
    initialDate: String
) {
    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }
    var selectedStatus by remember { mutableStateOf("hadir") }
    var keterangan by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Filter schedules based on selected date's day AND kelas_id
    val schedules = remember(selectedDate, allSchedules, kelasId) {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale("id"))
        val parsedDate = try {
            dateFormat.parse(selectedDate)
        } catch (e: Exception) {
            java.util.Date()
        }
        val dayName = dayFormat.format(parsedDate ?: java.util.Date())
        val filtered = allSchedules.filter { 
            it.hari.equals(dayName, ignoreCase = true) && 
            (kelasId == null || it.kelasId == kelasId)
        }
        
        android.util.Log.d("TeacherAttendanceAddDialog", "Date: $selectedDate, Day: $dayName, kelasId: $kelasId, Schedules: ${filtered.size}")
        
        // Reset selected schedule if it's not in the filtered list
        if (selectedSchedule != null && !filtered.contains(selectedSchedule)) {
            selectedSchedule = null
        }
        
        filtered
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Kehadiran Guru",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Picker
                OutlinedTextField(
                    value = selectedDate,
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

                // Schedule Selection
                OutlinedButton(
                    onClick = { 
                        if (schedules.isNotEmpty()) {
                            showScheduleDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = schedules.isNotEmpty()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            selectedSchedule?.let { "${it.mapel} - ${it.guruName}" } 
                                ?: if (schedules.isEmpty()) "Tidak ada jadwal tersedia" 
                                else "Pilih Jadwal"
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Status Selection
                OutlinedButton(
                    onClick = { showStatusDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status: ${getTeacherStatusDisplayName(selectedStatus)}")
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Keterangan
                OutlinedTextField(
                    value = keterangan,
                    onValueChange = { keterangan = it },
                    label = { Text("Keterangan") },
                    placeholder = { Text("Masukkan keterangan kehadiran") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    android.util.Log.d("TeacherAttendanceAddDialog", "Simpan clicked - schedule: ${selectedSchedule?.mapel}, schedules count: ${schedules.size}")
                    // Always allow submit, let the handler decide
                    onSubmit(selectedSchedule, selectedStatus, selectedDate, keterangan)
                },
                enabled = true
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )

    // Schedule Selection Dialog
    if (showScheduleDialog) {
        ModernFilterDialog(
            title = "Pilih Jadwal",
            options = schedules.map { schedule -> "${schedule.mapel} - ${schedule.guruName}" },
            selectedOption = selectedSchedule?.let { schedule -> "${schedule.mapel} - ${schedule.guruName}" } ?: "",
            onSelect = { selectedOption ->
                val foundSchedule = schedules.find { schedule -> "${schedule.mapel} - ${schedule.guruName}" == selectedOption }
                selectedSchedule = foundSchedule
                showScheduleDialog = false
            },
            onDismiss = { showScheduleDialog = false }
        )
    }

    // Status Selection Dialog
    if (showStatusDialog) {
        val statusOptions = listOf("hadir", "izin", "sakit", "telat", "tidak_hadir")
        ModernFilterDialog(
            title = "Pilih Status Kehadiran",
            options = statusOptions.map { status -> getTeacherStatusDisplayName(status) },
            selectedOption = getTeacherStatusDisplayName(selectedStatus),
            onSelect = { selectedOption ->
                val statusKey = statusOptions.find { status -> getTeacherStatusDisplayName(status) == selectedOption }
                if (statusKey != null) {
                    selectedStatus = statusKey
                }
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val calendar = java.util.Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        try {
            calendar.time = dateFormat.parse(selectedDate) ?: java.util.Date()
        } catch (e: Exception) {
            calendar.time = java.util.Date()
        }

        val datePickerDialog = android.app.DatePickerDialog(
            androidx.compose.ui.platform.LocalContext.current,
            { _, year, month, dayOfMonth ->
                val newDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                selectedDate = newDate
                showDatePicker = false
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
        
        DisposableEffect(Unit) {
            datePickerDialog.show()
            onDispose {
                datePickerDialog.dismiss()
            }
        }
    }
}

