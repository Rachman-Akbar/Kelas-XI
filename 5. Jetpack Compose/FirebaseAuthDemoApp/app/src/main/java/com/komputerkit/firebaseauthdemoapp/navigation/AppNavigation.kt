package com.komputerkit.firebaseauthdemoapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.komputerkit.firebaseauthdemoapp.AuthViewModel
import com.komputerkit.firebaseauthdemoapp.screens.HomeScreen
import com.komputerkit.firebaseauthdemoapp.screens.LoginScreen
import com.komputerkit.firebaseauthdemoapp.screens.RegisterScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = if (authState.isAuthenticated) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                isLoading = authState.isLoading,
                errorMessage = authState.error
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegister = { email, password ->
                    authViewModel.register(email, password)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                isLoading = authState.isLoading,
                errorMessage = authState.error
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                user = authState.user,
                onLogout = {
                    authViewModel.logout()
                }
            )
        }
    }
}