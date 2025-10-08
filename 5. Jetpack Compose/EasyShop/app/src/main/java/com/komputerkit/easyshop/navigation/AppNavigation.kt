package com.komputerkit.easyshop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.komputerkit.easyshop.ui.screens.CheckoutScreen
import com.komputerkit.easyshop.ui.screens.CheckoutSuccessScreen
import com.komputerkit.easyshop.ui.screens.FavoritesScreen
import com.komputerkit.easyshop.ui.screens.MainScreen
import com.komputerkit.easyshop.ui.screens.OrderDetailScreen
import com.komputerkit.easyshop.ui.screens.OrdersScreen
import com.komputerkit.easyshop.ui.screens.ProductDetailScreen
import com.komputerkit.easyshop.ui.screens.ShippingAddressScreen
import com.komputerkit.easyshop.ui.screens.auth.LoginScreen
import com.komputerkit.easyshop.ui.screens.auth.SignUpScreen
import com.komputerkit.easyshop.ui.viewmodel.AuthViewModel
import com.komputerkit.easyshop.ui.viewmodel.CartViewModel
import com.komputerkit.easyshop.ui.viewmodel.FavoriteViewModel
import com.komputerkit.easyshop.ui.viewmodel.HomeViewModel
import com.komputerkit.easyshop.ui.viewmodel.OrderViewModel
import com.komputerkit.easyshop.ui.viewmodel.ProductDetailViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object Cart : Screen("cart")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object ShippingAddress : Screen("shipping_address")
    object Checkout : Screen("checkout/{recipientName}/{recipientAddress}/{recipientPhone}") {
        fun createRoute(recipientName: String, recipientAddress: String, recipientPhone: String) = 
            "checkout/$recipientName/$recipientAddress/$recipientPhone"
    }
    object CheckoutSuccess : Screen("checkout_success/{orderId}/{orderTotal}") {
        fun createRoute(orderId: String, orderTotal: String) = "checkout_success/$orderId/$orderTotal"
    }
    object Orders : Screen("orders")
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object Favorites : Screen("favorites")
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    cartViewModel: CartViewModel,
    productDetailViewModel: ProductDetailViewModel,
    orderViewModel: OrderViewModel,
    favoriteViewModel: FavoriteViewModel,
    navController: NavHostController = rememberNavController()
) {
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsStateWithLifecycle()
    
    // Handle initial navigation based on auth state
    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn) {
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
        startDestination = if (isUserLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.SignUp.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            MainScreen(
                authViewModel = authViewModel,
                homeViewModel = homeViewModel,
                cartViewModel = cartViewModel,
                productDetailViewModel = productDetailViewModel,
                onNavigateToAuth = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.ShippingAddress.route)
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }
        
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                productDetailViewModel = productDetailViewModel,
                cartViewModel = cartViewModel,
                favoriteViewModel = favoriteViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ShippingAddress.route) {
            ShippingAddressScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onConfirmAddress = { name, address, phone ->
                    navController.navigate(Screen.Checkout.createRoute(name, address, phone))
                }
            )
        }
        
        composable(
            Screen.Checkout.route,
            arguments = listOf(
                navArgument("recipientName") { type = NavType.StringType },
                navArgument("recipientAddress") { type = NavType.StringType },
                navArgument("recipientPhone") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recipientName = backStackEntry.arguments?.getString("recipientName") ?: ""
            val recipientAddress = backStackEntry.arguments?.getString("recipientAddress") ?: ""
            val recipientPhone = backStackEntry.arguments?.getString("recipientPhone") ?: ""
            
            CheckoutScreen(
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel,
                recipientName = recipientName,
                recipientAddress = recipientAddress,
                recipientPhone = recipientPhone,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSuccess = { orderId, orderTotal ->
                    navController.navigate(Screen.CheckoutSuccess.createRoute(orderId, orderTotal)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(
            Screen.CheckoutSuccess.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType },
                navArgument("orderTotal") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val orderTotal = backStackEntry.arguments?.getString("orderTotal") ?: ""
            
            CheckoutSuccessScreen(
                orderId = orderId,
                orderTotal = orderTotal,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToOrders = {
                    navController.navigate(Screen.Orders.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }
        
        composable(Screen.Orders.route) {
            OrdersScreen(
                orderViewModel = orderViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToOrderDetail = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: return@composable
            OrderDetailScreen(
                orderId = orderId,
                orderViewModel = orderViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                favoriteViewModel = favoriteViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToProductDetail = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
    }
}