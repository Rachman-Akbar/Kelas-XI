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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaSubstituteTeacherScreen(
    navController: NavController,
    siswaViewModel: SiswaViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "siswa_substitute_teacher"
    val substitutes by siswaViewModel.substitutes.collectAsState()
    val schedules by siswaViewModel.schedules.collectAsState()
    val userKelasId by siswaViewModel.userKelasId.collectAsState()
    val userKelasName by siswaViewModel.userKelasName.collectAsState()
    val isLoading by siswaViewModel.isLoading.collectAsState()
    val errorMessage by siswaViewModel.errorMessage.collectAsState()
    val infoMessage by siswaViewModel.infoMessage.collectAsState()

    var isInitialized by remember { mutableStateOf(false) }
    var showUpdateStatusDialog by remember { mutableStateOf(false) }
    var selectedSubstitute by remember { mutableStateOf<SubstituteTeacher?>(null) }
    var newStatus by remember { mutableStateOf("") }
    var updateStatus by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }
    var showStatusFilterDialog by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf("Semua") }

    // Get user info
    val context = androidx.compose.ui.platform.LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

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

    // Show info message as snackbar
    LaunchedEffect(infoMessage) {
        infoMessage?.let { message ->
            if (message.isNotEmpty()) {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                // Clear info after showing
                siswaViewModel.setInfoMessage(null)
            }
        }
    }

    LaunchedEffect(isInitialized) {
        if (!isInitialized) {
            try {
                val authRepo = com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository(context)
                userName = authRepo.getUserName()
                userRole = authRepo.getUserRole()
                isInitialized = true
            } catch (e: Exception) {
                android.util.Log.e("SiswaSubstituteTeacherScreen", "Error loading user info", e)
            }
        }
    }

    // Load data when userKelasId becomes available
    LaunchedEffect(userKelasId) {
        val localKelasId = userKelasId
        if (localKelasId != null) {
            android.util.Log.d("SiswaSubstituteTeacherScreen", "Loading approved substitute teachers for kelas_id: $localKelasId")
            siswaViewModel.applyApprovedSubstituteTeacherFilter(localKelasId)
        } else {
            android.util.Log.e("SiswaSubstituteTeacherScreen", "userKelasId is NULL - waiting for it to be loaded from DataStore")
        }
    }

    // Build status options from loaded substitutes for filtering
    val filterStatusOptions = remember(substitutes) {
        val list = substitutes.mapNotNull { it.statusPenggantian?.lowercase() }.distinct().toMutableList()
        // Ensure 'dijadwalkan' is always available as a filter option
        if (!list.contains("dijadwalkan")) {
            list.add("dijadwalkan")
        }
        list.sort()
        list.add(0, "Semua")
        list
    }

    val filteredSubstitutes = remember(substitutes, selectedStatus, schedules, userKelasId) {
        var filtered = substitutes

        // First filter by kelas_id via jadwal lookup (if jadwalId present) - capture userKelasId in local variable
        val localKelasId = userKelasId
        if (localKelasId != null) {
            filtered = filtered.filter { sub ->
                sub.jadwalId == null || schedules.find { it.id == sub.jadwalId }?.kelasId == localKelasId
            }
            android.util.Log.d("SiswaSubstituteTeacherScreen", "Substitutes after kelas filter: ${filtered.size}")
        }

        // Then filter by selected status
        if (selectedStatus == "Semua") filtered
        else filtered.filter { it.statusPenggantian?.equals(selectedStatus, ignoreCase = true) == true }
    }

    // Handle status update
    val handleUpdateStatus: (Int) -> Unit = { substituteId ->
        if (updateStatus.isNotEmpty()) {
            isUpdating = true
            siswaViewModel.updateSubstituteStatus(
                substituteId = substituteId,
                status = updateStatus,
                catatanApproval = null,
                onSuccess = {
                    isUpdating = false
                    showUpdateStatusDialog = false
                    updateStatus = ""
                },
                onError = { error ->
                    isUpdating = false
                    // Show error could be handled here
                }
            )
        }
    }

    // Use enum options for statusPengganti from ViewModel for update dialog
    val statusUpdateOptions by siswaViewModel.statusPenggantiOptions.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                AppHeader(
                    title = "Dashboard Siswa",
                    userName = userName.ifEmpty { userRole },
                    userRole = userRole,
                    navController = navController,
                    onLogout = onLogout,
                    showHomeButton = true,
                    onHomeClick = { navController.navigate("siswa_home") }
                )
                StandardPageTitle(
                    title = if (userKelasName != null) "Guru Pengganti - $userKelasName" else "Guru Pengganti",
                    icon = Icons.Default.PersonAdd
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ModernColors.BackgroundWhite)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
            // Filter header
            StandardSectionHeader(title = "Guru Pengganti (Disetujui)")

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StandardFilterButton(
                    label = "Status",
                    selectedValue = selectedStatus,
                    icon = Icons.Default.List,
                    onClick = { showStatusFilterDialog = true },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Error message
            errorMessage?.let { error ->
                StandardInfoCard(
                    message = error,
                    icon = Icons.Default.Error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Substitute Teacher List
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
                            text = "Memuat guru pengganti...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }
            } else if (filteredSubstitutes.isEmpty()) {
                ModernEmptyState(
                    icon = Icons.Default.Person,
                    title = "Tidak Ada Guru Pengganti",
                    message = "Saat ini tidak ada guru pengganti (disetujui)",
                    onResetFilters = null
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredSubstitutes) { substitute ->
                        SubstituteTeacherCardItem(
                            substitute = substitute,
                            onUpdateStatus = { selectedSub ->
                                selectedSubstitute = selectedSub
                                showUpdateStatusDialog = true
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

    // Show status filter dialog
    if (showStatusFilterDialog) {
        ModernFilterDialog(
            title = "Pilih Status",
            options = filterStatusOptions,
            selectedOption = selectedStatus,
            onSelect = { pilihan -> selectedStatus = pilihan; showStatusFilterDialog = false },
            onDismiss = { showStatusFilterDialog = false }
        )
    }

    // Update Status Dialog
    if (showUpdateStatusDialog && selectedSubstitute != null) {
        AlertDialog(
            onDismissRequest = {
                showUpdateStatusDialog = false
                selectedSubstitute = null
                updateStatus = ""
            },
            title = { Text("Ubah Status") },
            text = {
                Column {
                    Text("Guru Asli: ${selectedSubstitute?.namaGuruAsli}")
                    Text("Guru Pengganti: ${selectedSubstitute?.namaGuruPengganti}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Pilih status baru:")
                    Spacer(modifier = Modifier.height(8.dp))

                    val statusOptions = listOf("selesai", "tidak_hadir")
                    statusOptions.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { updateStatus = status },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = updateStatus == status,
                                onClick = { updateStatus = status }
                            )
                            Text(
                                text = status.replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row {
                    TextButton(
                        onClick = {
                            showUpdateStatusDialog = false
                            selectedSubstitute = null
                            updateStatus = ""
                        }
                    ) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = {
                            if (selectedSubstitute != null) {
                                handleUpdateStatus(selectedSubstitute!!.id)
                            }
                        },
                        enabled = updateStatus.isNotEmpty() && !isUpdating
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Simpan")
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun SubstituteTeacherCardItem(
    substitute: SubstituteTeacher,
    onUpdateStatus: (SubstituteTeacher) -> Unit
) {
    StandardDataCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Tanggal dan Status - same as kepsek
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(substitute.tanggal),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ModernColors.TextPrimary
                )
                StandardStatusBadge(status = substitute.statusPenggantian ?: "pending")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = ModernColors.BorderGray)
            Spacer(modifier = Modifier.height(8.dp))

            // Details Section - same as kepsek
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Kelas
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
                        text = substitute.kelas ?: "Kelas -",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ModernColors.TextSecondary
                    )
                }

                // Mata Pelajaran
                substitute.mataPelajaran?.let { mapel ->
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
                            text = substitute.namaGuruAsli ?: "Guru -",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextPrimary
                        )
                    }
                }

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
                            text = substitute.namaGuruPengganti ?: "Guru -",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextPrimary
                        )
                    }
                }

                // Keterangan dari Guru (saat entry) - same as kepsek
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

                // Catatan Approval (jika ada) - same as kepsek
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
                                imageVector = Icons.Default.Comment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = ModernColors.InfoBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Catatan Approve",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ModernColors.InfoBlue
                                )
                                Text(
                                    text = substitute.catatanApproval ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ModernColors.TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Update status button for "dijadwalkan" status
            if (substitute.statusPenggantian.equals("dijadwalkan", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = { onUpdateStatus(substitute) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ModernColors.PrimaryBlue,
                            contentColor = ModernColors.BackgroundWhite
                        )
                    ) {
                        Text("Perbarui Status")
                    }
                }
            }
        }
    }
}
