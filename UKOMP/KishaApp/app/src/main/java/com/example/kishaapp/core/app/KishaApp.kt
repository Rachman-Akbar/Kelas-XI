package com.example.kishaapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kishaapp.navigation.AppNavGraph
import com.example.kishaapp.navigation.Routes
import com.example.kishaapp.ui.components.MarketplaceBottomBar
import com.example.kishaapp.ui.theme.KishaAppTheme
import com.example.kishaapp.viewmodel.AuthViewModel
import com.example.kishaapp.viewmodel.MarketplaceViewModel
import com.example.kishaapp.viewmodel.SellerViewModel
import com.example.kishaapp.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@Composable
fun KishaApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val marketplaceViewModel: MarketplaceViewModel = viewModel()
    val sellerViewModel: SellerViewModel = viewModel()
    val transactionViewModel: TransactionViewModel = viewModel()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backStackState = navController.currentBackStackEntryAsState()
    val currentRoute = backStackState.value?.destination?.route

    val bottomRoutes = setOf(Routes.HOME, Routes.SEARCH, Routes.PROFILE)

    val authState by authViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.refreshProfile()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentRoute in bottomRoutes) {
                MarketplaceBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (currentRoute == Routes.HOME) {
                FloatingActionButton(
                    onClick = {
                        val role = authState.userProfile?.role
                        if (role == "seller" || role == "admin") {
                            navController.navigate(Routes.addEditProduct())
                        } else {
                            navController.navigate(Routes.LOGIN)
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
                }
            }
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            innerPadding = innerPadding,
            authViewModel = authViewModel,
            marketplaceViewModel = marketplaceViewModel,
            sellerViewModel = sellerViewModel,
            transactionViewModel = transactionViewModel,
            onMessage = { message ->
                scope.launch { snackbarHostState.showSnackbar(message) }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KishaAppPreview() {
    KishaAppTheme {
        KishaApp()
    }
}
