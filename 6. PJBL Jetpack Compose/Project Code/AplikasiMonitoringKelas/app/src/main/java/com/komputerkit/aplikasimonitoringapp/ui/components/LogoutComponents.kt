package com.komputerkit.aplikasimonitoringapp.ui.components

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.komputerkit.aplikasimonitoringapp.MainActivity
import com.komputerkit.aplikasimonitoringapp.ui.viewmodel.LogoutViewModel

/**
 * Komponen Logout Button yang dapat digunakan di semua screen
 */
@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    viewModel: LogoutViewModel = viewModel()
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var isLoggingOut by remember { mutableStateOf(false) }
    
    // Logout Button
    Button(
        onClick = { showDialog = true },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Logout",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Logout")
    }
    
    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { if (!isLoggingOut) showDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (isLoggingOut) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Sedang logout...")
                    }
                } else {
                    Text("Apakah Anda yakin ingin keluar dari aplikasi?")
                }
            },
            confirmButton = {
                if (!isLoggingOut) {
                    TextButton(
                        onClick = {
                            isLoggingOut = true
                            viewModel.logout(context) {
                                // Navigate to login
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        }
                    ) {
                        Text("Ya, Logout")
                    }
                }
            },
            dismissButton = {
                if (!isLoggingOut) {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal")
                    }
                }
            }
        )
    }
}

/**
 * Komponen Logout Menu Item untuk Dropdown/Menu
 */
@Composable
fun LogoutMenuItem(
    onLogoutClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        },
        onClick = onLogoutClick
    )
}
