package com.komputerkit.aplikasimonitoringkelas.siswa

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaTeacherPermissionListScreen(
    navController: NavController,
    siswaViewModel: SiswaViewModel,
    onLogout: () -> Unit
) {
    val currentRoute = "siswa_permission_teacher"
    val teacherPermissions by siswaViewModel.teacherPermissions.collectAsState()
    val jenisOptions by siswaViewModel.jenisIzinOptions.collectAsState()
    val schedules by siswaViewModel.schedules.collectAsState()
    val userKelasId by siswaViewModel.userKelasId.collectAsState()
    val userKelasName by siswaViewModel.userKelasName.collectAsState()
    val isLoading by siswaViewModel.isLoading.collectAsState()
    val errorMessage by siswaViewModel.errorMessage.collectAsState()

    var isInitialized by remember { mutableStateOf(false) }
    var showJenisFilter by remember { mutableStateOf(false) }
    var selectedJenis by remember { mutableStateOf("Semua") }

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
                android.util.Log.e("SiswaTeacherPermissionListScreen", "Error loading user info", e)
            }
        }
    }

    // Load data when userKelasId becomes available
    LaunchedEffect(userKelasId) {
        val localKelasId = userKelasId
        if (localKelasId != null) {
            android.util.Log.d("SiswaTeacherPermissionListScreen", "Loading teacher permissions for kelas_id: $localKelasId")
            siswaViewModel.loadTeacherPermissions(localKelasId)
            siswaViewModel.loadEnumOptions()
        } else {
            android.util.Log.e("SiswaTeacherPermissionListScreen", "userKelasId is NULL - waiting for it to be loaded from DataStore")
        }
    }

    // Build jenis izin options from loaded permissions
    // jenisOptions now comes from ViewModel (backend enums)

    // Filter teacherPermissions by kelas_id (via schedules) AND selected jenis izin (if not "Semua")
    val permissionAttendances = remember(teacherPermissions, selectedJenis, schedules, userKelasId) {
        var filtered = teacherPermissions

        val localKelasId = userKelasId
        if (localKelasId != null) {
            val teacherIdsInClass = schedules.filter { it.kelasId == localKelasId }.map { it.guruId }.distinct()
            filtered = filtered.filter { perm -> teacherIdsInClass.contains(perm.guruId) }
            android.util.Log.d("SiswaTeacherPermissionListScreen", "Permissions after kelas filter: ${filtered.size}")
        }

        if (selectedJenis == "Semua") filtered
        else filtered.filter { it.jenisIzin.equals(selectedJenis, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ModernColors.BackgroundWhite)
    ) {
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
            title = if (userKelasName != null) "Izin Guru - $userKelasName" else "Izin Guru",
            icon = Icons.Default.EventNote
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Filter header
            StandardSectionHeader(title = "Guru yang Izin (Disetujui)")

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StandardFilterButton(
                    label = "Jenis Izin",
                    selectedValue = selectedJenis,
                    icon = Icons.Default.List,
                    onClick = { showJenisFilter = true },
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

            // Permission List
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
                            text = "Memuat izin guru...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }
            } else if (permissionAttendances.isEmpty()) {
                ModernEmptyState(
                    icon = Icons.Default.Done,
                    title = "Tidak Ada Izin",
                    message = "Saat ini tidak ada guru yang izin (disetujui)",
                    onResetFilters = null
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(permissionAttendances) { permission ->
                        TeacherPermissionCardItemModel(permission = permission)
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

    // Show Jenis Izin filter dialog
    MaybeShowJenisDialog(
        show = showJenisFilter,
        options = jenisOptions,
        selected = selectedJenis,
        onSelect = { pilihan -> selectedJenis = pilihan; showJenisFilter = false },
        onDismiss = { showJenisFilter = false }
    )
}

// Dialog for Jenis Izin filter
@Composable
fun JenisIzinFilterDialog(
    show: Boolean,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return
    ModernFilterDialog(
        title = "Pilih Jenis Izin",
        options = options,
        selectedOption = selected,
        onSelect = { pilihan -> onSelect(pilihan) },
        onDismiss = onDismiss
    )
}

// Show dialog when requested
@Composable
private fun MaybeShowJenisDialog(show: Boolean, options: List<String>, selected: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    if (show) {
        ModernFilterDialog(
            title = "Pilih Jenis Izin",
            options = options,
            selectedOption = selected,
            onSelect = { pilihan -> onSelect(pilihan) },
            onDismiss = onDismiss
        )
    }
}


@Composable
fun TeacherPermissionCardItemModel(permission: TeacherPermission) {
    StandardDataCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Tanggal Mulai dan Status - same as kepsek
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatDate(permission.tanggalMulai),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ModernColors.TextPrimary
                    )
                    permission.guruName?.let { guruName ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = guruName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextSecondary
                        )
                    }
                }
                StandardStatusBadge(status = permission.statusApproval)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = ModernColors.BorderGray)
            Spacer(modifier = Modifier.height(8.dp))

            // Details Section - same as kepsek
            Column(modifier = Modifier.fillMaxWidth()) {
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
                            text = "${formatDate(permission.tanggalMulai)} s/d ${formatDate(permission.tanggalSelesai)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ModernColors.TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

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

                // Informasi Approval (if status is approved/rejected)
                if (permission.statusApproval.lowercase() in listOf("disetujui", "ditolak", "dijadwalkan")) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = ModernColors.BorderGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Informasi Approval",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ModernColors.TextSecondary
                        )

                        // Approved/Rejected By
                        permission.approverName?.let { approver ->
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
                                        androidx.compose.ui.graphics.Color(0xFFF44336)
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
                                        text = approver,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ModernColors.TextPrimary
                                    )
                                }
                            }
                        }

                        // Approval Date
                        permission.tanggalApproval?.let { approvalDate ->
                            Spacer(modifier = Modifier.height(4.dp))
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
                                Text(
                                    text = "Tanggal: ${formatDate(approvalDate)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ModernColors.TextSecondary
                                )
                            }
                        }

                        // Catatan Approval
                        permission.catatanApproval?.let { catatan ->
                            if (catatan.isNotBlank()) {
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
                                                text = catatan,
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