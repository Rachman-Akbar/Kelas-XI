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
import androidx.compose.material.icons.filled.MeetingRoom
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

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KepalaSekolahMainScreen()
                }
            }
        }
    }
}

sealed class KepalaSekolahScreen(val route: String, val title: String, val icon: ImageVector) {
    object JadwalPelajaran : KepalaSekolahScreen("jadwal_pelajaran", "Jadwal Pelajaran", Icons.Default.Schedule)
    object KelasKosong : KepalaSekolahScreen("kelas_kosong", "Kelas Kosong", Icons.Default.MeetingRoom)
    object List : KepalaSekolahScreen("list", "List", Icons.AutoMirrored.Filled.List)
}

val kepalaSekolahBottomNavItems = listOf(
    KepalaSekolahScreen.JadwalPelajaran,
    KepalaSekolahScreen.KelasKosong,
    KepalaSekolahScreen.List
)

@Composable
fun KepalaSekolahMainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { KepalaSekolahBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = KepalaSekolahScreen.JadwalPelajaran.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(KepalaSekolahScreen.JadwalPelajaran.route) {
                KepalaSekolahJadwalPelajaranPage()
            }
            composable(KepalaSekolahScreen.KelasKosong.route) {
                KepalaSekolahKelasKosongPage()
            }
            composable(KepalaSekolahScreen.List.route) {
                KepalaSekolahListPage()
            }
        }
    }
}

@Composable
fun KepalaSekolahBottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        kepalaSekolahBottomNavItems.forEach { screen ->
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
fun KepalaSekolahJadwalPelajaranPage() {
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
            text = "Selamat datang, Prof. Dr. Sutrisno (Kepala Sekolah)",
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
            text = "Overview Jadwal Pelajaran",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Monitoring seluruh jadwal pelajaran sekolah",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Scrollable card list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jadwalList) { jadwal ->
                KepalaSekolahJadwalCard(jadwal = jadwal)
            }
        }
    }
}

@Composable
fun KepalaSekolahJadwalCard(jadwal: JadwalItem) {
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
                    Text(
                        text = "✓ Aktif",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun KepalaSekolahKelasKosongPage() {
    val kelasKosongList = remember {
        listOf(
            "XI IPA 1 - Jam ke-3 (Guru sakit - Drs. Ahmad Supriyanto)",
            "XI IPA 2 - Jam ke-5 (Rapat dinas - Siti Nurhaliza, S.Pd)",
            "XI IPS 1 - Jam ke-2 (Pelatihan - Prof. Dr. Sutrisno)",
            "X IPA 1 - Jam ke-4 (Cuti - Dr. Budi Santoso)",
            "XII IPS 2 - Jam ke-6 (Tugas luar - Dewi Sartika, S.S)"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Monitor Kelas Kosong",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Kelas yang memerlukan guru pengganti hari ini",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (kelasKosongList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✓ Tidak ada kelas kosong hari ini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(kelasKosongList) { kelasKosong ->
                    KelasKosongCard(kelasKosong = kelasKosong)
                }
            }
        }
    }
}

@Composable
fun KelasKosongCard(kelasKosong: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = kelasKosong,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            TextButton(
                onClick = { /* Assign guru pengganti */ }
            ) {
                Text(
                    "Tindak Lanjut",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun KepalaSekolahListPage() {
    val laporanList = remember {
        listOf(
            "Laporan Bulanan - Oktober 2024",
            "Evaluasi Kinerja Guru - Q3 2024",
            "Statistik Kehadiran Siswa - Minggu Ini",
            "Laporan Keuangan Sekolah - September",
            "Audit Internal - Semester Ganjil",
            "Laporan Prestasi Siswa - Olimpiade",
            "Evaluasi Fasilitas Sekolah - 2024",
            "Laporan Kegiatan Ekstrakurikuler",
            "Monitoring Pembelajaran Daring",
            "Laporan Kolaborasi Orang Tua"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dashboard Kepala Sekolah",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Ringkasan laporan dan evaluasi sekolah",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(laporanList) { laporan ->
                KepalaSekolahLaporanCard(laporan = laporan)
            }
        }
    }
}

@Composable
fun KepalaSekolahLaporanCard(laporan: String) {
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
            
            Row {
                TextButton(
                    onClick = { /* Lihat detail */ }
                ) {
                    Text("Lihat", fontSize = 12.sp)
                }
                TextButton(
                    onClick = { /* Download */ }
                ) {
                    Text("Unduh", fontSize = 12.sp)
                }
            }
        }
    }
}