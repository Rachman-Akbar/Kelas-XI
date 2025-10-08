package com.komputerkit.easyshop.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.widget.Toast
import com.komputerkit.easyshop.ui.components.CustomTextField
import com.komputerkit.easyshop.ui.components.LoadingButton
import com.komputerkit.easyshop.ui.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    // Handle navigation setelah sukses
    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) {
            authViewModel.resetSuccessState()
            onNavigateToHome()
        }
    }
    
    // Show Toast message
    authState.showToast?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.clearToast()
        }
    }
    
    // Show error message
    authState.error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Auto clear error after 3 seconds
            kotlinx.coroutines.delay(3000)
            authViewModel.clearError()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Daftar Akun",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Buat akun baru untuk mulai berbelanja",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 32.dp)
        )
        
        // Form Fields
        CustomTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nama Lengkap",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        CustomTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        CustomTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Nomor Telepon",
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        CustomTextField(
            value = address,
            onValueChange = { address = it },
            label = "Alamat",
            singleLine = false,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        CustomTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        CustomTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Konfirmasi Password",
            isPassword = true,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Error Message
        authState.error?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // Sign Up Button
        LoadingButton(
            text = "Daftar",
            onClick = {
                authViewModel.signUp(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    password = password,
                    confirmPassword = confirmPassword,
                    onSuccess = onNavigateToHome
                )
            },
            isLoading = authState.isLoading,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Navigate to Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sudah punya akun? ")
            TextButton(onClick = onNavigateToLogin) {
                Text("Masuk")
            }
        }
    }
}