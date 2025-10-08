package com.komputerkit.aplikasimonitoringapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.komputerkit.aplikasimonitoringapp.data.*
import com.komputerkit.aplikasimonitoringapp.ui.theme.AplikasiMonitoringAppTheme

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdminMainScreen()
                }
            }
        }
    }
}

sealed class AdminScreen(val route: String, val title: String, val icon: ImageVector) {
    object EntryUser : AdminScreen("entry_user", "Entri User", Icons.Default.People)
    object EntryJadwal : AdminScreen("entry_jadwal", "Entri Jadwal", Icons.Default.Settings)
    object List : AdminScreen("list", "List", Icons.AutoMirrored.Filled.List)
}

val adminBottomNavItems = listOf(
    AdminScreen.EntryUser,
    AdminScreen.EntryJadwal,
    AdminScreen.List
)

@Composable
fun AdminMainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AdminBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AdminScreen.EntryUser.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AdminScreen.EntryUser.route) {
                AdminEntryUserPage()
            }
            composable(AdminScreen.EntryJadwal.route) {
                AdminEntryJadwalPage()
            }
            composable(AdminScreen.List.route) {
                AdminListPage()
            }
        }
    }
}

@Composable
fun AdminBottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        adminBottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEntryUserPage() {
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.SISWA) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var userEntries by remember { mutableStateOf(listOf<UserEntry>()) }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Informasi Admin
        Text(
            text = "Selamat datang, Admin (Administrator)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Entri User Baru",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Role Spinner
        ExposedDropdownMenuBox(
            expanded = isRoleDropdownExpanded,
            onExpandedChange = { isRoleDropdownExpanded = !isRoleDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedRole.displayName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Pilih Role") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isRoleDropdownExpanded,
                onDismissRequest = { isRoleDropdownExpanded = false }
            ) {
                UserRole.values().forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.displayName) },
                        onClick = {
                            selectedRole = role
                            isRoleDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // Nama TextField
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama Lengkap") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = if (it.isNotEmpty() && !validateEmail(it)) {
                    "Format email tidak valid"
                } else {
                    ""
                }
            },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            isError = emailError.isNotEmpty(),
            supportingText = if (emailError.isNotEmpty()) {
                { Text(emailError, color = MaterialTheme.colorScheme.error) }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Simpan Button
        Button(
            onClick = {
                if (nama.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && 
                    validateEmail(email) && emailError.isEmpty()) {
                    val newUser = UserEntry(nama, email, selectedRole)
                    userEntries = userEntries + newUser
                    // Reset form
                    nama = ""
                    email = ""
                    password = ""
                    selectedRole = UserRole.SISWA
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = nama.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && 
                     validateEmail(email) && emailError.isEmpty()
        ) {
            Text(
                text = "Simpan User",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List User yang sudah ditambahkan
        if (userEntries.isNotEmpty()) {
            Text(
                text = "Daftar User yang Ditambahkan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userEntries) { user ->
                    AdminUserEntryCard(userEntry = user)
                }
            }
        }
    }
}

@Composable
fun AdminUserEntryCard(userEntry: UserEntry) {
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
                text = userEntry.nama,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = userEntry.email,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Role: ${userEntry.role.displayName}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEntryJadwalPage() {
    var selectedHari by remember { mutableStateOf(Hari.SENIN) }
    var selectedKelas by remember { mutableStateOf(Kelas.XI_RPL) }
    var selectedMataPelajaran by remember { mutableStateOf(MataPelajaran.IPA) }
    var selectedGuru by remember { mutableStateOf(NamaGuru.SITI) }
    var jamKe by remember { mutableStateOf("") }
    
    var isHariDropdownExpanded by remember { mutableStateOf(false) }
    var isKelasDropdownExpanded by remember { mutableStateOf(false) }
    var isMataPelajaranDropdownExpanded by remember { mutableStateOf(false) }
    var isGuruDropdownExpanded by remember { mutableStateOf(false) }
    
    var jadwalEntries by remember { mutableStateOf(listOf<JadwalEntry>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Informasi Admin
        Text(
            text = "Selamat datang, Admin (Administrator)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Entri Jadwal Baru",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 1. Spinner Hari
        ExposedDropdownMenuBox(
            expanded = isHariDropdownExpanded,
            onExpandedChange = { isHariDropdownExpanded = !isHariDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedHari.displayName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Pilih Hari") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHariDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isHariDropdownExpanded,
                onDismissRequest = { isHariDropdownExpanded = false }
            ) {
                Hari.values().forEach { hari ->
                    DropdownMenuItem(
                        text = { Text(hari.displayName) },
                        onClick = {
                            selectedHari = hari
                            isHariDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // 2. Spinner Kelas
        ExposedDropdownMenuBox(
            expanded = isKelasDropdownExpanded,
            onExpandedChange = { isKelasDropdownExpanded = !isKelasDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedKelas.displayName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Pilih Kelas") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isKelasDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isKelasDropdownExpanded,
                onDismissRequest = { isKelasDropdownExpanded = false }
            ) {
                Kelas.values().forEach { kelas ->
                    DropdownMenuItem(
                        text = { Text(kelas.displayName) },
                        onClick = {
                            selectedKelas = kelas
                            isKelasDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // 3. Spinner Mata Pelajaran
        ExposedDropdownMenuBox(
            expanded = isMataPelajaranDropdownExpanded,
            onExpandedChange = { isMataPelajaranDropdownExpanded = !isMataPelajaranDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedMataPelajaran.displayName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Pilih Mata Pelajaran") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isMataPelajaranDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isMataPelajaranDropdownExpanded,
                onDismissRequest = { isMataPelajaranDropdownExpanded = false }
            ) {
                MataPelajaran.values().forEach { mataPelajaran ->
                    DropdownMenuItem(
                        text = { Text(mataPelajaran.displayName) },
                        onClick = {
                            selectedMataPelajaran = mataPelajaran
                            isMataPelajaranDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // 4. Spinner Guru
        ExposedDropdownMenuBox(
            expanded = isGuruDropdownExpanded,
            onExpandedChange = { isGuruDropdownExpanded = !isGuruDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedGuru.displayName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Pilih Guru") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGuruDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isGuruDropdownExpanded,
                onDismissRequest = { isGuruDropdownExpanded = false }
            ) {
                NamaGuru.values().forEach { guru ->
                    DropdownMenuItem(
                        text = { Text(guru.displayName) },
                        onClick = {
                            selectedGuru = guru
                            isGuruDropdownExpanded = false
                        }
                    )
                }
            }
        }

        // 5. TextField Jam Ke
        OutlinedTextField(
            value = jamKe,
            onValueChange = { jamKe = it },
            label = { Text("Jam Ke (contoh: 1-3)") },
            placeholder = { Text("1-3") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // 6. Button Simpan
        Button(
            onClick = {
                if (jamKe.isNotEmpty()) {
                    val newJadwal = JadwalEntry(
                        selectedHari, selectedKelas, selectedMataPelajaran, selectedGuru, jamKe
                    )
                    jadwalEntries = jadwalEntries + newJadwal
                    // Reset form
                    jamKe = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = jamKe.isNotEmpty()
        ) {
            Text(
                text = "Simpan Jadwal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7. Scrollable Cards dari data yang sudah disimpan
        if (jadwalEntries.isNotEmpty()) {
            Text(
                text = "Daftar Jadwal yang Ditambahkan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(jadwalEntries) { jadwal ->
                    AdminJadwalEntryCard(jadwalEntry = jadwal)
                }
            }
        }
    }
}

@Composable
fun AdminJadwalEntryCard(jadwalEntry: JadwalEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${jadwalEntry.hari.displayName} - Jam ${jadwalEntry.jamKe}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "${jadwalEntry.mataPelajaran.displayName} - ${jadwalEntry.kelas.displayName}",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Guru: ${jadwalEntry.guru.displayName}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AdminListPage() {
    val logList = remember {
        listOf(
            "2024-10-08 09:30 - User Login: siswa@email.com",
            "2024-10-08 09:25 - Database Backup: Success",
            "2024-10-08 09:20 - User Created: guru_baru@email.com",
            "2024-10-08 09:15 - System Update: Completed",
            "2024-10-08 09:10 - Login Failed: invalid_user@email.com",
            "2024-10-08 09:05 - Schedule Updated: Mathematics XI IPA 1",
            "2024-10-08 09:00 - User Logout: admin@email.com"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Log Aktivitas Sistem",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logList) { log ->
                AdminLogCard(log = log)
            }
        }
    }
}

@Composable
fun AdminLogCard(log: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = log,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}