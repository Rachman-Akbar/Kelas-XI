package com.komputerkit.aplikasimonitoringkelas.common

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthRepository

@Composable
fun ScreenWithHeader(
    title: String,
    navController: NavController,
    currentRoute: String,
    onLogout: () -> Unit,
    showHomeButton: Boolean = false,
    onHomeClick: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current
    var userName by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        try {
            val authRepo = AuthRepository(context)
            userName = authRepo.getUserName()
            userRole = authRepo.getUserRole()
        } catch (e: Exception) {
            println("ScreenWrapper - Failed to get user: ${e.message}")
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(
            title = title,
            userName = userName.ifEmpty { userRole },
            userRole = userRole,
            navController = navController,
            onLogout = onLogout,
            showHomeButton = showHomeButton,
            onHomeClick = onHomeClick
        )
        
        Box(modifier = Modifier.weight(1f)) {
            Column(content = content)
        }
        
        bottomBar()
    }
}
