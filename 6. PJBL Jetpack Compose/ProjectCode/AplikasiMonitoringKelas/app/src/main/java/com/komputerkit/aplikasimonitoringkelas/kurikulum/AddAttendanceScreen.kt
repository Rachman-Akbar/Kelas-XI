package com.komputerkit.aplikasimonitoringkelas.kurikulum

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAttendanceScreen(
    navController: NavController,
    kurikulumViewModel: KurikulumViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "kurikulum_add_attendance"
    
    // Get user info for header
    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    // Attendance creation states
    var attendanceType by remember { mutableStateOf("Guru") } // "Guru" or "Siswa"
    var selectedGuru by remember { mutableStateOf<com.komputerkit.aplikasimonitoringkelas.data.models.Guru?>(null) }
    var selectedSiswa by remember { mutableStateOf<com.komputerkit.aplikasimonitoringkelas.data.models.Siswa?>(null) }
    var selectedKelas by remember { mutableStateOf<com.komputerkit.aplikasimonitoringkelas.data.models.Class?>(null) }
    var selectedJadwal by remember { mutableStateOf<com.komputerkit.aplikasimonitoringkelas.data.models.Schedule?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var tanggal by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    
    // Dialog states
    var showAttendanceTypeDialog by remember { mutableStateOf(false) }
    var showGuruDialog by remember { mutableStateOf(false) }
    var showSiswaDialog by remember { mutableStateOf(false) }
    var showKelasDialog by remember { mutableStateOf(false) }
    var showJadwalDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    
    // Data lists
    val guruList by kurikulumViewModel.guruList.collectAsState()
    val classList by kurikulumViewModel.classList.collectAsState()
    val scheduleList by kurikulumViewModel.scheduleList.collectAsState()
    val isLoading by kurikulumViewModel.isLoading.collectAsState()
    val errorMessage by kurikulumViewModel.errorMessage.collectAsState()
    val infoMessage by kurikulumViewModel.infoMessage.collectAsState()

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            try {
                val authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
                userName = authRepo.getUserName()
                userRole = authRepo.getUserRole()
            } catch (e: Exception) {
                // Ignore error
            }
            // Load reference data
            kurikulumViewModel.loadGurus()
            kurikulumViewModel.loadClasses()
            kurikulumViewModel.loadSchedules()
            isInitialized = true
        }
    }

    // Auto-fill today's date
    LaunchedEffect(Unit) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        tanggal = dateFormat.format(Date())
    }

    // Filter data based on current selections
    val filteredSiswaList = if (selectedKelas != null) {
        // We would need to load students by class, but for now using a placeholder
        emptyList<com.komputerkit.aplikasimonitoringkelas.data.models.Siswa>() // Actual implementation would load from repository
    } else {
        emptyList<com.komputerkit.aplikasimonitoringkelas.data.models.Siswa>()
    }

    val filteredScheduleList = scheduleList.filter { schedule ->
        when (attendanceType) {
            "Guru" -> selectedGuru?.id == schedule.guru_id
            "Siswa" -> selectedKelas?.id == schedule.kelas_id
            else -> true
        }
    }

    val statusOptions = when (attendanceType) {
        "Guru" -> listOf("hadir", "izin", "sakit", "telat", "tidak_hadir")
        "Siswa" -> listOf("hadir", "izin", "sakit", "telat", "tidak_hadir")
        else -> emptyList()
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
            title = "Tambah Kehadiran",
            icon = Icons.Default.EventAvailable
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(ModernColors.BackgroundWhite)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    kurikulumViewModel.clearInfo()
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

            // Attendance Type Selection
            StandardSectionHeader("Jenis Kehadiran")

            OutlinedButton(
                onClick = { showAttendanceTypeDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Jenis: ${attendanceType}")
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            // Date Selection
            StandardSectionHeader("Tanggal")
            OutlinedTextField(
                value = tanggal,
                onValueChange = { tanggal = it },
                label = { Text("Tanggal") },
                placeholder = { Text("yyyy-MM-dd") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                }
            )

            // Conditional fields based on attendance type
            if (attendanceType == "Guru") {
                StandardSectionHeader("Data Guru")

                // Guru Selection
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
                            text = selectedGuru?.nama ?: "Pilih Guru",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Schedule Selection based on selected guru
                if (selectedGuru != null) {
                    OutlinedButton(
                        onClick = { showJadwalDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedJadwal != null) {
                                    "Jadwal: ${selectedJadwal?.hari}, Jam ${selectedJadwal?.jam_ke} - ${selectedJadwal?.mata_pelajaran?.nama}"
                                } else {
                                    "Pilih Jadwal"
                                }
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                }
            } else { // Siswa
                StandardSectionHeader("Data Siswa")

                // Kelas Selection
                OutlinedButton(
                    onClick = { showKelasDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedKelas?.nama ?: "Pilih Kelas",
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Siswa Selection (based on selected class)
                if (selectedKelas != null) {
                    OutlinedButton(
                        onClick = { showSiswaDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedSiswa?.nama ?: "Pilih Siswa",
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    // Schedule Selection based on selected class
                    OutlinedButton(
                        onClick = { showJadwalDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedJadwal != null) {
                                    "Jadwal: ${selectedJadwal?.hari}, Jam ${selectedJadwal?.jam_ke} - ${selectedJadwal?.mata_pelajaran?.nama}"
                                } else {
                                    "Pilih Jadwal"
                                }
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                }
            }

            // Status Selection
            StandardSectionHeader("Status Kehadiran")
            OutlinedButton(
                onClick = { showStatusDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedStatus ?: "Pilih Status"
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            // Keterangan
            StandardSectionHeader("Keterangan")
            OutlinedTextField(
                value = keterangan,
                onValueChange = { keterangan = it },
                label = { Text("Keterangan") },
                placeholder = { Text("Masukkan keterangan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    when (attendanceType) {
                        "Guru" -> {
                            if (selectedGuru != null && selectedJadwal != null && selectedStatus != null) {
                                kurikulumViewModel.createTeacherAttendance(
                                    selectedGuru!!.id,
                                    selectedJadwal!!.id,
                                    tanggal,
                                    selectedStatus!!,
                                    keterangan.takeIf { it.isNotBlank() }
                                )
                            } else {
                                kurikulumViewModel.updateInfo("Silakan lengkapi semua field wajib")
                            }
                        }
                        "Siswa" -> {
                            if (selectedSiswa != null && selectedJadwal != null && selectedStatus != null) {
                                kurikulumViewModel.createStudentAttendance(
                                    selectedSiswa!!.id,
                                    selectedJadwal!!.id,
                                    tanggal,
                                    selectedStatus!!,
                                    keterangan.takeIf { it.isNotBlank() }
                                )
                            } else {
                                kurikulumViewModel.updateInfo("Silakan lengkapi semua field wajib")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = when (attendanceType) {
                    "Guru" -> selectedGuru != null && selectedJadwal != null && selectedStatus != null
                    "Siswa" -> selectedSiswa != null && selectedJadwal != null && selectedStatus != null
                    else -> false
                }
            ) {
                Text("Simpan Kehadiran")
            }
        }

        KurikulumFooter(
            navController = navController,
            currentRoute = currentRoute
        )
    }

    // Dialogs
    // Attendance Type Selection Dialog
    if (showAttendanceTypeDialog) {
        AlertDialog(
            onDismissRequest = { showAttendanceTypeDialog = false },
            title = { Text("Pilih Jenis Kehadiran") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Guru", "Siswa")) { type ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (attendanceType == type)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            onClick = {
                                attendanceType = type
                                showAttendanceTypeDialog = false
                                
                                // Reset dependent selections when type changes
                                selectedGuru = null
                                selectedSiswa = null
                                selectedKelas = null
                                selectedJadwal = null
                                selectedStatus = null
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = type,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (attendanceType == type) {
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
                TextButton(onClick = { showAttendanceTypeDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Guru Selection Dialog
    if (showGuruDialog) {
        AlertDialog(
            onDismissRequest = { showGuruDialog = false },
            title = { Text("Pilih Guru") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(guruList) { guru ->
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
                                        text = "NIP: ${guru.nip ?: "-"}",
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

    // Siswa Selection Dialog
    if (showSiswaDialog) {
        AlertDialog(
            onDismissRequest = { showSiswaDialog = false },
            title = { Text("Pilih Siswa") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSiswaList) { siswa ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedSiswa?.id == siswa.id)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            onClick = {
                                selectedSiswa = siswa
                                showSiswaDialog = false
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
                                        text = siswa.nama,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "NIS: ${siswa.nis ?: "-"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (selectedSiswa?.id == siswa.id) {
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
                TextButton(onClick = { showSiswaDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Kelas Selection Dialog
    if (showKelasDialog) {
        AlertDialog(
            onDismissRequest = { showKelasDialog = false },
            title = { Text("Pilih Kelas") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(classList) { kelas ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedKelas?.id == kelas.id)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            onClick = {
                                selectedKelas = kelas
                                // Load students for this class
                                kurikulumViewModel.loadStudentsByClass(kelas.id)
                                showKelasDialog = false
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
                                        text = kelas.nama,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Tingkat: ${kelas.tingkat} - ${kelas.jurusan}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (selectedKelas?.id == kelas.id) {
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
                TextButton(onClick = { showKelasDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Jadwal Selection Dialog
    if (showJadwalDialog) {
        AlertDialog(
            onDismissRequest = { showJadwalDialog = false },
            title = { Text("Pilih Jadwal") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredScheduleList) { schedule ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedJadwal?.id == schedule.id)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            onClick = {
                                selectedJadwal = schedule
                                showJadwalDialog = false
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
                                        text = "${schedule.hari}, Jam ${schedule.jam_ke}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${schedule.mata_pelajaran?.nama} - ${schedule.guru?.nama}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (selectedJadwal?.id == schedule.id) {
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
                TextButton(onClick = { showJadwalDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Status Selection Dialog
    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("Pilih Status Kehadiran") },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(statusOptions) { status ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedStatus == status)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surface,
                            onClick = {
                                selectedStatus = status
                                showStatusDialog = false
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = status.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (selectedStatus == status) {
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
                TextButton(onClick = { showStatusDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}