package com.komputerkit.aplikasimonitoringapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.aplikasimonitoringapp.data.JadwalItem

@Composable
fun JadwalPage() {
    val jadwalList = remember {
        listOf(
            JadwalItem("Drs. Ahmad Supriyanto", "Matematika", "XI IPA 1", "07:00 - 08:30"),
            JadwalItem("Siti Nurhaliza, S.Pd", "Bahasa Indonesia", "XI IPA 2", "08:30 - 10:00"),
            JadwalItem("Dr. Budi Santoso", "Fisika", "XI IPA 1", "10:15 - 11:45"),
            JadwalItem("Rina Kartika, M.Pd", "Kimia", "XI IPA 3", "12:30 - 14:00"),
            JadwalItem("H. Muhammad Yusuf", "Pendidikan Agama Islam", "XI IPS 1", "14:00 - 15:30"),
            JadwalItem("Dewi Sartika, S.S", "Bahasa Inggris", "XI IPS 2", "07:00 - 08:30"),
            JadwalItem("Prof. Dr. Sutrisno", "Sejarah", "XI IPS 1", "08:30 - 10:00"),
            JadwalItem("Indira Puspitasari, S.Pd", "Geografi", "XI IPS 3", "10:15 - 11:45"),
            JadwalItem("Drs. Wahyu Hidayat", "Ekonomi", "XI IPS 2", "12:30 - 14:00"),
            JadwalItem("Dr. Sri Mulyani", "Sosiologi", "XI IPS 3", "14:00 - 15:30")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Jadwal Pelajaran",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(jadwalList) { jadwal ->
                JadwalCard(jadwal = jadwal)
            }
        }
    }
}

@Composable
fun JadwalCard(jadwal: JadwalItem) {
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
                    Text(
                        text = jadwal.namaGuru,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = jadwal.pelajaran,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Kelas: ${jadwal.kelas}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Text(
                    text = jadwal.jam,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}