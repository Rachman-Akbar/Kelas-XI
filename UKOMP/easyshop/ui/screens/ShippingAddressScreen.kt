package com.komputerkit.easyshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(
    onNavigateBack: () -> Unit,
    onConfirmAddress: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var recipientName by remember { mutableStateOf("") }
    var fullAddress by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    
    var isNameError by remember { mutableStateOf(false) }
    var isAddressError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Alamat Pengiriman",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Informasi Pengiriman",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lengkapi informasi alamat pengiriman untuk melanjutkan proses checkout.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Recipient Name Field
            OutlinedTextField(
                value = recipientName,
                onValueChange = { 
                    recipientName = it
                    isNameError = it.isBlank()
                },
                label = { Text("Nama Penerima") },
                placeholder = { Text("Masukkan nama lengkap penerima") },
                modifier = Modifier.fillMaxWidth(),
                isError = isNameError,
                supportingText = if (isNameError) {
                    { Text("Nama penerima tidak boleh kosong") }
                } else null,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
            
            // Full Address Field
            OutlinedTextField(
                value = fullAddress,
                onValueChange = { 
                    fullAddress = it
                    isAddressError = it.isBlank()
                },
                label = { Text("Alamat Lengkap") },
                placeholder = { Text("Jl. Nama Jalan No. XX, RT/RW, Kelurahan, Kecamatan, Kota, Kode Pos") },
                modifier = Modifier.fillMaxWidth(),
                isError = isAddressError,
                supportingText = if (isAddressError) {
                    { Text("Alamat lengkap tidak boleh kosong") }
                } else null,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                minLines = 3,
                maxLines = 5
            )
            
            // Phone Number Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { 
                    // Only allow numbers and limit to reasonable length
                    if (it.all { char -> char.isDigit() || char == '+' } && it.length <= 15) {
                        phoneNumber = it
                        isPhoneError = it.length < 10
                    }
                },
                label = { Text("Nomor Telepon") },
                placeholder = { Text("08xxxxxxxxxx atau +62xxxxxxxxx") },
                modifier = Modifier.fillMaxWidth(),
                isError = isPhoneError,
                supportingText = if (isPhoneError) {
                    { Text("Nomor telepon minimal 10 digit") }
                } else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Shipping Options Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Opsi Pengiriman",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Regular Shipping (Default)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = true,
                            onClick = { /* For now, only one option */ }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Pengiriman Reguler",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "3-5 hari kerja • Rp 15.000",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kembali")
                }
                
                Button(
                    onClick = {
                        // Validate all fields
                        val nameValid = recipientName.isNotBlank()
                        val addressValid = fullAddress.isNotBlank()
                        val phoneValid = phoneNumber.length >= 10
                        
                        isNameError = !nameValid
                        isAddressError = !addressValid
                        isPhoneError = !phoneValid
                        
                        if (nameValid && addressValid && phoneValid) {
                            onConfirmAddress(recipientName, fullAddress, phoneNumber)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = recipientName.isNotBlank() && 
                             fullAddress.isNotBlank() && 
                             phoneNumber.length >= 10
                ) {
                    Text("Konfirmasi Alamat")
                }
            }
        }
    }
}