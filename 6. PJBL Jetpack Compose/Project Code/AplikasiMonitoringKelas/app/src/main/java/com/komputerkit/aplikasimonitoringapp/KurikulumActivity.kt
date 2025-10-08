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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.komputerkit.aplikasimonitoringapp.data.JadwalItem
import com.komputerkit.aplikasimonitoringapp.data.Hari
import com.komputerkit.aplikasimonitoringapp.data.Kelas
import com.komputerkit.aplikasimonitoringapp.ui.theme.AplikasiMonitoringAppTheme

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KurikulumMainScreen()
                }
            }
        }
    }
}

sealed class KurikulumScreen(val route: String, val title: String, val icon: ImageVector) {
    object JadwalPelajaran : KurikulumScreen("jadwal_pelajaran", "Jadwal Pelajaran", Icons.Default.Schedule)
    object GantiGuru : KurikulumScreen("ganti_guru", "Ganti Guru", Icons.Default.PersonAdd)
    object List : KurikulumScreen("list", "List", Icons.AutoMirrored.Filled.List)
}

val kurikulumBottomNavItems = listOf(
    KurikulumScreen.JadwalPelajaran,
    KurikulumScreen.GantiGuru,
    KurikulumScreen.List
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumMainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kurikulum") },
                actions = {
                    com.komputerkit.aplikasimonitoringapp.ui.components.LogoutButton(
                        modifier = Modifier.padding(end = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = { KurikulumBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = KurikulumScreen.JadwalPelajaran.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(KurikulumScreen.JadwalPelajaran.route) {
                KurikulumJadwalPelajaranPage()
            }
            composable(KurikulumScreen.GantiGuru.route) {
                KurikulumGantiGuruPage()
            }
            composable(KurikulumScreen.List.route) {
                KurikulumListPage()
            }
        }
    }
}

@Composable
fun KurikulumBottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        kurikulumBottomNavItems.forEach { screen ->
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
fun KurikulumJadwalPelajaranPage() {
    var selectedHari by remember { mutableStateOf(Hari.SENIN) }
    var selectedKelas by remember { mutableStateOf(Kelas.XI_RPL) }
    var isHariDropdownExpanded by remember { mutableStateOf(false) }
    var isKelasDropdownExpanded by remember { mutableStateOf(false) }
    
    // Data jadwal untuk 10 card
    val jadwalList = remember(selectedHari, selectedKelas) {
        listOf(
            JadwalItem("Drs. Ahmad Supriyanto", "Matematika", selectedKelas.displayName, "07:00 - 08:30", "1-3", selectedHari.displayName),
            JadwalItem("Siti Nurhaliza, S.Pd", "Bahasa Indonesia", selectedKelas.displayName, "08:30 - 10:00", "4-6", selectedHari.displayName),
            JadwalItem("Dr. Budi Santoso", "Fisika", selectedKelas.displayName, "10:15 - 11:45", "7-9", selectedHari.displayName),
            JadwalItem("Rina Kartika, M.Pd", "Kimia", selectedKelas.displayName, "12:30 - 14:00", "10-12", selectedHari.displayName),
            JadwalItem("H. Muhammad Yusuf", "Pendidikan Agama Islam", selectedKelas.displayName, "14:00 - 15:30", "13-15", selectedHari.displayName),
            JadwalItem("Dewi Sartika, S.S", "Bahasa Inggris", selectedKelas.displayName, "07:00 - 08:30", "1-3", selectedHari.displayName),
            JadwalItem("Prof. Dr. Sutrisno", "Sejarah", selectedKelas.displayName, "08:30 - 10:00", "4-6", selectedHari.displayName),
            JadwalItem("Indira Puspitasari, S.Pd", "Geografia", selectedKelas.displayName, "10:15 - 11:45", "7-9", selectedHari.displayName),
            JadwalItem("Drs. Wahyu Hidayat", "Ekonomi", selectedKelas.displayName, "12:30 - 14:00", "10-12", selectedHari.displayName),
            JadwalItem("Dr. Sri Mulyani", "Sosiologi", selectedKelas.displayName, "14:00 - 15:30", "13-15", selectedHari.displayName)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Informasi user yang login
        Text(
            text = "Selamat datang, Dr. Budi Santoso (Kurikulum)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spinner Hari
        ExposedDropdownMenuBox(
            expanded = isHariDropdownExpanded,
            onExpandedChange = { isHariDropdownExpanded = !isHariDropdownExpanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
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

        // Spinner Kelas
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

        Text(
            text = "Manajemen Jadwal Pelajaran",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Scrollable card list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jadwalList) { jadwal ->
                KurikulumJadwalCard(jadwal = jadwal)
            }
        }
    }
}

@Composable
fun KurikulumJadwalCard(jadwal: JadwalItem) {
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Jam (1-3, 4-6, etc.)
                    Text(
                        text = "Jam ${jadwal.jamKe}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Mata Pelajaran
                    Text(
                        text = jadwal.pelajaran,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    // Guru dan Nama Guru
                    Text(
                        text = "Guru: ${jadwal.namaGuru}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    TextButton(
                        onClick = { /* Edit jadwal */ },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text("Edit", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun KurikulumGantiGuruPage() {
    var namaGuruLama by remember { mutableStateOf("") }
    var namaGuruBaru by remember { mutableStateOf("") }
    var mataPelajaran by remember { mutableStateOf("") }
    var kelas by remember { mutableStateOf("") }
    var alasan by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ganti Guru",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = namaGuruLama,
            onValueChange = { namaGuruLama = it },
            label = { Text("Nama Guru Lama") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = namaGuruBaru,
            onValueChange = { namaGuruBaru = it },
            label = { Text("Nama Guru Pengganti") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = mataPelajaran,
            onValueChange = { mataPelajaran = it },
            label = { Text("Mata Pelajaran") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = kelas,
            onValueChange = { kelas = it },
            label = { Text("Kelas") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = alasan,
            onValueChange = { alasan = it },
            label = { Text("Alasan Penggantian") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 24.dp),
            maxLines = 4
        )

        Button(
            onClick = {
                // Logic untuk menyimpan perubahan guru
                namaGuruLama = ""
                namaGuruBaru = ""
                mataPelajaran = ""
                kelas = ""
                alasan = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = namaGuruLama.isNotEmpty() && namaGuruBaru.isNotEmpty() && 
                     mataPelajaran.isNotEmpty() && kelas.isNotEmpty()
        ) {
            Text(
                text = "Simpan Perubahan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun KurikulumListPage() {
    val laporanList = remember {
        listOf(
            "Laporan Mingguan - Minggu ke-1 Oktober",
            "Laporan Pergantian Guru - Matematika XI IPA 1",
            "Laporan Evaluasi Kurikulum - Semester Ganjil",
            "Laporan Absensi Guru - September 2024",
            "Laporan Pencapaian Target - Q3 2024",
            "Laporan Remedial - Mata Pelajaran Fisika",
            "Laporan Ekstrakurikuler - Oktober 2024"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daftar Laporan Kurikulum",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(laporanList) { laporan ->
                KurikulumLaporanCard(laporan = laporan)
            }
        }
    }
}

@Composable
fun KurikulumLaporanCard(laporan: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = laporan,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            
            TextButton(
                onClick = { /* Lihat detail laporan */ }
            ) {
                Text("Lihat", fontSize = 12.sp)
            }
        }
    }
}