package com.komputerkit.aplikasimonitoringapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.aplikasimonitoringapp.data.UserRole
import com.komputerkit.aplikasimonitoringapp.ui.theme.AplikasiMonitoringAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen { role ->
                        navigateToRoleActivity(role)
                    }
                }
            }
        }
    }

    private fun navigateToRoleActivity(role: UserRole) {
        val intent = when (role) {
            UserRole.SISWA -> Intent(this, SiswaActivity::class.java)
            UserRole.KURIKULUM -> Intent(this, KurikulumActivity::class.java)
            UserRole.KEPALA_SEKOLAH -> Intent(this, KepalaSekolahActivity::class.java)
            UserRole.ADMIN -> Intent(this, AdminActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (UserRole) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.SISWA) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    
    val context = LocalContext.current

    fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Sekolah
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground), // Ganti dengan logo sekolah
            contentDescription = "Logo Sekolah",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Login Aplikasi Monitoring",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Role Spinner
        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
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
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                UserRole.values().forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.displayName) },
                        onClick = {
                            selectedRole = role
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

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

        // Login Button
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && validateEmail(email)) {
                    onLoginSuccess(selectedRole)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = email.isNotEmpty() && password.isNotEmpty() && 
                     validateEmail(email) && emailError.isEmpty()
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Role yang dipilih: ${selectedRole.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}