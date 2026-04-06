package com.komputerkit.easyshop.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.komputerkit.easyshop.ui.viewmodel.AuthViewModel
import com.komputerkit.easyshop.ui.viewmodel.CartViewModel
import com.komputerkit.easyshop.ui.viewmodel.HomeViewModel
import com.komputerkit.easyshop.ui.viewmodel.ProductDetailViewModel

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavScreen("bottom_home", "Home", Icons.Default.Home)
    object Cart : BottomNavScreen("bottom_cart", "Cart", Icons.Default.ShoppingCart)
    object Profile : BottomNavScreen("bottom_profile", "Profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    cartViewModel: CartViewModel,
    productDetailViewModel: ProductDetailViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {}
) {
    val navController = rememberNavController()
    val cartState by cartViewModel.cartState.collectAsStateWithLifecycle()
    
    val bottomNavItems = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Cart,
        BottomNavScreen.Profile
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            BadgedBox(
                                badge = {
                                    if (screen == BottomNavScreen.Cart && cartState.cartItemCount > 0) {
                                        Badge {
                                            Text("${cartState.cartItemCount}")
                                        }
                                    }
                                }
                            ) {
                                Icon(screen.icon, contentDescription = screen.title)
                            }
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavScreen.Home.route,
            Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) {
                HomeScreen(
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel,
                    onNavigateToAuth = onNavigateToAuth,
                    onProductClick = onNavigateToProductDetail
                )
            }
            composable(BottomNavScreen.Cart.route) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onNavigateToCheckout = onNavigateToCheckout,
                    onNavigateToHome = {
                        navController.navigate(BottomNavScreen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToAuth = onNavigateToAuth,
                    onNavigateToOrders = onNavigateToOrders,
                    onNavigateToFavorites = onNavigateToFavorites
                )
            }
        }
    }
}