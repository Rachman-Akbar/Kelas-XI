package com.komputerkit.aplikasimonitoringapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EntryScreen() {
    var namaGuru by remember { mutableStateOf("") }
    var pelajaran by remember { mutableStateOf("") }
    var kelas by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Entry Data Jadwal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = namaGuru,
            onValueChange = { namaGuru = it },
            label = { Text("Nama Guru") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = pelajaran,
            onValueChange = { pelajaran = it },
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
            value = jam,
            onValueChange = { jam = it },
            label = { Text("Jam Pelajaran") },
            placeholder = { Text("Contoh: 07:00 - 08:30") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                // Logic untuk menyimpan data
                namaGuru = ""
                pelajaran = ""
                kelas = ""
                jam = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = namaGuru.isNotEmpty() && pelajaran.isNotEmpty() && 
                     kelas.isNotEmpty() && jam.isNotEmpty()
        ) {
            Text(
                text = "Simpan Data",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ListScreen() {
    val absensiList = remember {
        listOf(
            "Ahmad Rizki - XI IPA 1 - Hadir",
            "Siti Aisyah - XI IPA 1 - Hadir", 
            "Budi Santoso - XI IPA 1 - Tidak Hadir",
            "Dewi Kartika - XI IPA 2 - Hadir",
            "Muhammad Yusuf - XI IPA 2 - Hadir",
            "Rina Sari - XI IPA 2 - Hadir",
            "Wahyu Hidayat - XI IPS 1 - Tidak Hadir",
            "Sri Mulyani - XI IPS 1 - Hadir",
            "Indira Puspita - XI IPS 1 - Hadir",
            "Sutrisno - XI IPS 2 - Hadir"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daftar Absensi",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(absensiList) { absensi ->
                AbsensiCard(absensi = absensi)
            }
        }
    }
}

@Composable
fun AbsensiCard(absensi: String) {
    val parts = absensi.split(" - ")
    val nama = parts.getOrNull(0) ?: ""
    val kelas = parts.getOrNull(1) ?: ""
    val status = parts.getOrNull(2) ?: ""
    
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nama,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = kelas,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = status,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (status == "Hadir") 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.error
            )
        }
    }
}