package com.komputerkit.basicandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.basicandroid.ui.theme.BasicAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BasicAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormComponent(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FormComponent(modifier: Modifier = Modifier) {
    // State untuk menyimpan input dari user
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    
    // State untuk menampilkan dialog
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image di bagian atas
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Text "Nama"
        Text(
            text = "Nama",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // TextField untuk input nama
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Masukkan nama Anda") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Text "Alamat"
        Text(
            text = "Alamat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // TextField untuk input alamat
        OutlinedTextField(
            value = alamat,
            onValueChange = { alamat = it },
            label = { Text("Masukkan alamat Anda") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Button Simpan
        Button(
            onClick = {
                // Aksi ketika button diklik
                // Validasi input terlebih dahulu
                if (nama.isBlank() && alamat.isBlank()) {
                    dialogMessage = "Nama dan Alamat tidak boleh kosong!"
                } else if (nama.isBlank()) {
                    dialogMessage = "Nama tidak boleh kosong!"
                } else if (alamat.isBlank()) {
                    dialogMessage = "Alamat tidak boleh kosong!"
                } else {
                    // Jika semua field terisi, tampilkan data
                    dialogMessage = "Data berhasil disimpan!\n\nNama: $nama\nAlamat: $alamat"
                }
                showDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = "Simpan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
    
    // AlertDialog untuk menampilkan pesan
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { 
                Text(
                    text = if (nama.isNotBlank() && alamat.isNotBlank()) "Sukses!" else "Peringatan!",
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { 
                Text(text = dialogMessage) 
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showDialog = false
                        // Reset form jika data berhasil disimpan
                        if (nama.isNotBlank() && alamat.isNotBlank()) {
                            nama = ""
                            alamat = ""
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FormComponentPreview() {
    BasicAndroidTheme {
        FormComponent()
    }
}