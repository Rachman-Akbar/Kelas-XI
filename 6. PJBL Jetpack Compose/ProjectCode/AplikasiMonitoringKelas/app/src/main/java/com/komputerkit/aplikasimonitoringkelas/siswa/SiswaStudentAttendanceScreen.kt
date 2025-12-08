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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*

private fun getCurrentDate(): String {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return dateFormat.format(java.util.Date())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaStudentAttendanceScreen(
    navController: NavController,
    siswaViewModel: SiswaViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "siswa_student_attendance"
    val attendances by siswaViewModel.studentAttendances.collectAsState()
    val userKelasId by siswaViewModel.userKelasId.collectAsState()
    val userKelasName by siswaViewModel.userKelasName.collectAsState()
    val statusFilter by siswaViewModel.statusFilter.collectAsState()
    val isLoading by siswaViewModel.isLoading.collectAsState()
    val errorMessage by siswaViewModel.errorMessage.collectAsState()

    var showStatusFilter by remember { mutableStateOf(false) }
    var isInitialized by remember { mutableStateOf(false) }
    
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
                val authRepo =
                    com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
                userName = authRepo.getUserName()
                userRole = authRepo.getUserRole()
                isInitialized = true
            } catch (e: Exception) {
                android.util.Log.e("SiswaStudentAttendanceScreen", "Error loading user info", e)
            }
        }
    }

    // Load data when userKelasId becomes available
    LaunchedEffect(userKelasId) {
        val localKelasId = userKelasId
        if (localKelasId != null) {
            android.util.Log.d(
                "SiswaStudentAttendanceScreen",
                "Loading student attendance for kelas_id: $localKelasId"
            )
            siswaViewModel.loadStudentAttendances(localKelasId, forceRefresh = true)
            // Also load schedules for this class to allow selection in attendance entry
            android.util.Log.d(
                "SiswaStudentAttendanceScreen",
                "Loading schedules for kelas_id: $localKelasId"
            )
            siswaViewModel.loadSchedules(localKelasId, forceRefresh = true)
            // Load students list for this class
            siswaViewModel.loadSiswaList(localKelasId)
        } else {
            android.util.Log.e(
                "SiswaStudentAttendanceScreen",
                "userKelasId is NULL - waiting for it to be loaded from DataStore"
            )
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

    val schedules by siswaViewModel.schedules.collectAsState()

    // Filter attendances by kelas_id (via schedules) AND status
    val filteredAttendances = remember(attendances, statusFilter, schedules, userKelasId) {
        var filtered = attendances

        val localKelasId = userKelasId
        if (localKelasId != null) {
            filtered = filtered.filter { att ->
                schedules.find { it.id == att.jadwalId }?.kelasId == localKelasId
            }
            android.util.Log.d("SiswaStudentAttendanceScreen", "Student attendances after kelas filter: ${filtered.size}")
        }

        if (statusFilter.isNullOrEmpty() || statusFilter == "Semua") {
            filtered
        } else {
            filtered.filter { it.status.equals(statusFilter, ignoreCase = true) }
        }
    }

    var showAddAttendanceDialog by remember { mutableStateOf(false) }
    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }
    var selectedStatus by remember { mutableStateOf("hadir") }
    var selectedDate by remember { mutableStateOf(getCurrentDate()) }
    var keterangan by remember { mutableStateOf("") }
    var showScheduleSelectionDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    val siswaList by siswaViewModel.siswaList.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ModernFAB(
                icon = Icons.Default.Add,
                onClick = {
                    android.util.Log.d("SiswaStudentAttendance", "FAB clicked - opening dialog")
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
            AppHeader(
                title = if (userKelasName != null) "Kehadiran Siswa - $userKelasName" else if (userKelasId != null) "Kehadiran Siswa - Kelas ID: $userKelasId" else "Kehadiran Siswa",
                userName = userName.ifEmpty { userRole },
                userRole = userRole,
                navController = navController,
                onLogout = onLogout
            )

            StandardPageTitle(
                title = if (userKelasName != null) "Kehadiran Siswa - $userKelasName" else if (userKelasId != null) "Kehadiran Siswa - Kelas ID: $userKelasId" else "Kehadiran Siswa",
                icon = Icons.Default.EventAvailable
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

                // Attendance List
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
                                text = "Memuat kehadiran...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ModernColors.TextSecondary
                            )
                        }
                    }
                } else if (filteredAttendances.isEmpty()) {
                    ModernEmptyState(
                        icon = Icons.Default.EventBusy,
                        title = "Belum Ada Data",
                        message = "Belum ada data kehadiran siswa",
                        onResetFilters = null
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredAttendances) { attendance ->
                            StudentAttendanceCardItem(attendance = attendance)
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

    // Add Student Attendance Dialog
    if (showAddAttendanceDialog) {
        android.util.Log.d("SiswaStudentAttendance", "Dialog opened - schedules: ${schedules.size}, students: ${siswaList.size}")
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale("id"))
        val parsedDate = try {
            dateFormat.parse(selectedDate)
        } catch (e: Exception) {
            java.util.Date()
        }
        val dayName = dayFormat.format(parsedDate ?: java.util.Date())
        val todaySchedules = schedules.filter { it.hari.equals(dayName, ignoreCase = true) }

        android.util.Log.d("SiswaStudentAttendance", "Selected date: $selectedDate, Day name: $dayName, Total schedules: ${schedules.size}, Filtered schedules: ${todaySchedules.size}")
        todaySchedules.forEach { schedule ->
            android.util.Log.d("SiswaStudentAttendance", "Schedule: ${schedule.mapel} - ${schedule.hari}")
        }

        // Filter siswa by current class
        val classStudents = remember(siswaList, userKelasId) {
            val localKelasId = userKelasId
            if (localKelasId != null) {
                siswaList.filter { it.kelas_id == localKelasId }
            } else {
                siswaList
            }
        }

        StudentAttendanceAddDialog(
            kelasId = userKelasId,
            onDismiss = {
                showAddAttendanceDialog = false
                // Reset values
                selectedSchedule = null
                selectedStatus = "hadir"
                keterangan = ""
            },
            onSubmit = { siswaId, schedule, status, date, desc ->
                android.util.Log.d("SiswaStudentAttendance", "=== onSubmit CALLED ===")
                android.util.Log.d("SiswaStudentAttendance", "siswaId: $siswaId, schedule: ${schedule?.mapel}, status: '$status', date: $date")
                android.util.Log.d("SiswaStudentAttendance", "Status length: ${status.length}, isEmpty: ${status.isEmpty()}, isBlank: ${status.isBlank()}")
                android.util.Log.d("SiswaStudentAttendance", "userKelasId from state: $userKelasId")
                
                // Validate status is not empty
                if (status.isBlank()) {
                    android.util.Log.e("SiswaStudentAttendance", "Status is blank!")
                    siswaViewModel.setErrorMessage("Status kehadiran harus dipilih")
                    return@StudentAttendanceAddDialog
                }
                
                // Use existing userKelasId from state instead of making API call
                if (userKelasId != null) {
                    // If no schedule selected, use first available schedule from all schedules for this date
                    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    val dayFormat = java.text.SimpleDateFormat("EEEE", java.util.Locale("id"))
                    val parsedDate = try { dateFormat.parse(date) } catch (e: Exception) { java.util.Date() }
                    val dayName = dayFormat.format(parsedDate ?: java.util.Date())
                    val schedulesForDay = schedules.filter { it.hari.equals(dayName, ignoreCase = true) }
                    val jadwalId = schedule?.id ?: schedulesForDay.firstOrNull()?.id ?: 0
                    
                    android.util.Log.d("SiswaStudentAttendance", "jadwalId: $jadwalId, dayName: $dayName, schedulesForDay: ${schedulesForDay.size}")
                    
                    if (jadwalId > 0) {
                        android.util.Log.d("SiswaStudentAttendance", "Calling submitStudentAttendance with status: '$status'")
                        siswaViewModel.submitStudentAttendance(
                            siswaId = siswaId,
                            jadwalId = jadwalId,
                            tanggal = date,
                            statusKehadiran = status,
                            keterangan = if (desc.isBlank()) null else desc,
                            onSuccess = {
                                android.util.Log.d("SiswaStudentAttendance", "Submit SUCCESS")
                                showAddAttendanceDialog = false
                                successMessage = "✅ Kehadiran siswa berhasil disimpan!"
                                showSuccessMessage = true
                                // Reset form
                                selectedSchedule = null
                                selectedStatus = "hadir"
                                keterangan = ""
                            },
                            onError = { error ->
                                android.util.Log.e("SiswaStudentAttendance", "Submit ERROR: $error")
                                showAddAttendanceDialog = false
                                
                                // Parse error message for better user feedback
                                val userMessage = when {
                                    error.contains("sudah ada", ignoreCase = true) || 
                                    error.contains("already exists", ignoreCase = true) ||
                                    error.contains("duplicate", ignoreCase = true) -> 
                                        "❌ Data kehadiran sudah ada untuk siswa dan tanggal ini"
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
                        android.util.Log.e("SiswaStudentAttendance", "No jadwal available for $dayName")
                        siswaViewModel.setErrorMessage("Tidak ada jadwal untuk hari $dayName. Silakan pilih tanggal dengan jadwal yang tersedia atau hubungi admin.")
                        showAddAttendanceDialog = false
                    }
                } else {
                    android.util.Log.e("SiswaStudentAttendance", "userKelasId is null")
                    siswaViewModel.setErrorMessage("Kelas ID tidak tersedia. Silakan login ulang.")
                    showAddAttendanceDialog = false
                }
            },
            allSchedules = schedules,
            studentList = classStudents,
            initialDate = selectedDate
        )
    }
}

@Composable
fun StudentAttendanceCardItem(attendance: StudentAttendance) {
    StandardDataCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Tanggal dan Status - sama persis dengan kepsek
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatDate(attendance.tanggal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ModernColors.TextPrimary
                    )
                    // Nama Siswa sebagai subtitle
                    attendance.siswaName?.let { siswaName ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = siswaName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }
                StandardStatusBadge(status = attendance.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = ModernColors.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Details Section - sama persis dengan kepsek
            Column(modifier = Modifier.fillMaxWidth()) {
                // Kelas
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
                    Spacer(modifier = Modifier.height(8.dp))
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
                            text = mapel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }

                // Guru
                attendance.guruName?.let { guru ->
                    Spacer(modifier = Modifier.height(8.dp))
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
                            color = ModernColors.TextSecondary
                        )
                    }
                }

                // Keterangan - sama persis dengan kepsek
                if (!attendance.keterangan.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    StandardInfoCard(
                        message = "Keterangan: ${attendance.keterangan}",
                        icon = Icons.Default.Info
                    )
                }
            }
        }
    }
}

fun getStudentStatusDisplayName(status: String): String {
    return when (status.lowercase()) {
        "hadir" -> "Hadir"
        "tidak_hadir" -> "Tidak Hadir"
        "izin" -> "Izin"
        "sakit" -> "Sakit"
        "telat" -> "Terlambat"
        else -> status.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}

fun getStudentStatusColor(status: String): androidx.compose.ui.graphics.Color {
    return when (status.lowercase()) {
        "hadir" -> ModernColors.SuccessGreen
        "tidak_hadir" -> ModernColors.ErrorRed
        "izin", "sakit", "telat", "terlambat" -> ModernColors.WarningAmber
        else -> ModernColors.TextSecondary
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceAddDialog(
    kelasId: Int?,
    onDismiss: () -> Unit,
    onSubmit: (Int, Schedule?, String, String, String) -> Unit,
    allSchedules: List<Schedule>,
    studentList: List<com.komputerkit.aplikasimonitoringkelas.data.models.Siswa>,
    initialDate: String
) {
    var selectedStudent by remember { mutableStateOf<com.komputerkit.aplikasimonitoringkelas.data.models.Siswa?>(null) }
    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }
    var selectedStatus by remember { mutableStateOf("hadir") }
    var keterangan by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var showStudentDialog by remember { mutableStateOf(false) }
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
        
        android.util.Log.d("StudentAttendanceAddDialog", "Date: $selectedDate, Day: $dayName, kelasId: $kelasId, Schedules: ${filtered.size}")
        
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
                text = "Tambah Kehadiran Siswa",
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

                // Student Selection
                OutlinedButton(
                    onClick = { 
                        if (studentList.isNotEmpty()) {
                            showStudentDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = studentList.isNotEmpty()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            selectedStudent?.let { "Siswa: ${it.nama}" } 
                                ?: if (studentList.isEmpty()) "Tidak ada data siswa" 
                                else "Pilih Siswa",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                }

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
                            selectedSchedule?.let { "${it.mapel} - ${it.kelasName}" } 
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
                        Text("Status: ${getStudentStatusDisplayName(selectedStatus)}")
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
                    android.util.Log.d("StudentAttendanceAddDialog", "Simpan clicked - student: ${selectedStudent?.nama}, schedule: ${selectedSchedule?.mapel}, enabled: ${selectedStudent != null}")
                    selectedStudent?.let { student ->
                        android.util.Log.d("StudentAttendanceAddDialog", "Submitting with student ID: ${student.id}")
                        onSubmit(student.id, selectedSchedule, selectedStatus, selectedDate, keterangan)
                    } ?: run {
                        android.util.Log.e("StudentAttendanceAddDialog", "Cannot submit - student is null")
                    }
                },
                enabled = selectedStudent != null
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
            options = schedules.map { schedule -> "${schedule.mapel} - ${schedule.kelasName}" },
            selectedOption = selectedSchedule?.let { schedule -> "${schedule.mapel} - ${schedule.kelasName}" } ?: "",
            onSelect = { selectedOption ->
                val foundSchedule = schedules.find { schedule -> "${schedule.mapel} - ${schedule.kelasName}" == selectedOption }
                selectedSchedule = foundSchedule
                showScheduleDialog = false
            },
            onDismiss = { showScheduleDialog = false }
        )
    }

    // Student Selection Dialog
    if (showStudentDialog) {
        ModernFilterDialog(
            title = "Pilih Siswa",
            options = studentList.map { student -> student.nama },
            selectedOption = selectedStudent?.nama ?: "",
            onSelect = { selectedOption ->
                val foundStudent = studentList.find { student -> student.nama == selectedOption }
                selectedStudent = foundStudent
                showStudentDialog = false
            },
            onDismiss = { showStudentDialog = false }
        )
    }

    // Schedule Selection Dialog
    if (showStatusDialog) {
        val statusOptions = listOf("hadir", "izin", "sakit", "telat", "tidak_hadir")
        ModernFilterDialog(
            title = "Pilih Status Kehadiran",
            options = statusOptions.map { status -> getStudentStatusDisplayName(status) },
            selectedOption = getStudentStatusDisplayName(selectedStatus),
            onSelect = { selectedOption ->
                val statusKey = statusOptions.find { status -> getStudentStatusDisplayName(status) == selectedOption }
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