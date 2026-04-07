package com.example.kishaapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kishaapp.ui.screens.AddEditProductScreen
import com.example.kishaapp.ui.screens.CartScreen
import com.example.kishaapp.ui.screens.CategoryListScreen
import com.example.kishaapp.ui.screens.CheckoutScreen
import com.example.kishaapp.ui.screens.HomeScreen
import com.example.kishaapp.ui.screens.OrderDetailScreen
import com.example.kishaapp.ui.screens.OrdersScreen
import com.example.kishaapp.ui.screens.ProductDetailScreen
import com.example.kishaapp.ui.screens.ProfileScreen
import com.example.kishaapp.ui.screens.SearchScreen
import com.example.kishaapp.ui.screens.SellerDashboardScreen
import com.example.kishaapp.ui.screens.SplashScreen
import com.example.kishaapp.ui.screens.auth.ForgotPasswordMvvmScreen
import com.example.kishaapp.ui.screens.auth.LoginMvvmScreen
import com.example.kishaapp.ui.screens.auth.EmailVerificationPendingScreen
import com.example.kishaapp.ui.screens.auth.RegisterMvvmScreen
import com.example.kishaapp.viewmodel.AuthViewModel
import com.example.kishaapp.viewmodel.MarketplaceViewModel
import com.example.kishaapp.viewmodel.SellerViewModel
import com.example.kishaapp.viewmodel.TransactionViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    authViewModel: AuthViewModel,
    marketplaceViewModel: MarketplaceViewModel,
    sellerViewModel: SellerViewModel,
    transactionViewModel: TransactionViewModel,
    onMessage: (String) -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val transactionState by transactionViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    transactionViewModel.loadCart()
                }
            }

            HomeScreen(
                marketplaceViewModel = marketplaceViewModel,
                cartItemCount = transactionState.cartItems.sumOf { it.quantity },
                onOpenCart = {
                    if (authState.isLoggedIn) {
                        navController.navigate(Routes.CART)
                    } else {
                        onMessage("Silakan login untuk membuka keranjang")
                        navController.navigate(Routes.LOGIN)
                    }
                },
                onOpenCategories = {
                    navController.navigate(Routes.CATEGORIES)
                },
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                marketplaceViewModel = marketplaceViewModel,
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onNavigateToSellerDashboard = { navController.navigate(Routes.SELLER_DASHBOARD) },
                onNavigateToCart = {
                    if (authState.isLoggedIn) navController.navigate(Routes.CART) else navController.navigate(Routes.LOGIN)
                },
                onNavigateToOrders = {
                    if (authState.isLoggedIn) navController.navigate(Routes.ORDERS) else navController.navigate(Routes.LOGIN)
                },
                onNavigateToCategories = {
                    navController.navigate(Routes.CATEGORIES)
                }
            )
        }

        composable(Routes.CATEGORIES) {
            CategoryListScreen(
                marketplaceViewModel = marketplaceViewModel,
                onProductClick = { productId ->
                    navController.navigate(Routes.productDetail(productId))
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginMvvmScreen(
                authViewModel = authViewModel,
                onSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgot = { navController.navigate(Routes.FORGOT_PASSWORD) },
                onNavigateToEmailVerification = {
                    navController.navigate(Routes.EMAIL_VERIFICATION) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterMvvmScreen(
                authViewModel = authViewModel,
                onNavigateToEmailVerification = {
                    navController.navigate(Routes.EMAIL_VERIFICATION) {
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.EMAIL_VERIFICATION) {
            EmailVerificationPendingScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onEnterApp = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordMvvmScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStack ->
            val productId = backStack.arguments?.getString("productId").orEmpty()
            ProductDetailScreen(
                productId = productId,
                marketplaceViewModel = marketplaceViewModel,
                onContactClick = { sellerName ->
                    onMessage("Hubungi seller: $sellerName")
                },
                onAddToCart = { product ->
                    if (!authState.isLoggedIn) {
                        onMessage("Silakan login untuk menambahkan ke keranjang")
                        navController.navigate(Routes.LOGIN)
                    } else {
                        transactionViewModel.addToCart(product) {
                            onMessage("Produk ditambahkan ke keranjang")
                        }
                    }
                }
            )
        }

        composable(Routes.CART) {
            if (!authState.isLoggedIn) {
                LaunchedEffect(Unit) {
                    onMessage("Silakan login untuk membuka keranjang")
                    navController.navigate(Routes.LOGIN)
                }
            } else {
                CartScreen(
                    transactionViewModel = transactionViewModel,
                    onNavigateToCheckout = { navController.navigate(Routes.CHECKOUT) }
                )
            }
        }

        composable(Routes.CHECKOUT) {
            if (!authState.isLoggedIn) {
                LaunchedEffect(Unit) {
                    onMessage("Silakan login untuk checkout")
                    navController.navigate(Routes.LOGIN)
                }
            } else {
                CheckoutScreen(
                    transactionViewModel = transactionViewModel,
                    onCheckoutSuccess = { orderId ->
                        navController.navigate(Routes.orderDetail(orderId)) {
                            popUpTo(Routes.CART) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Routes.ORDERS) {
            if (!authState.isLoggedIn) {
                LaunchedEffect(Unit) {
                    onMessage("Silakan login untuk melihat riwayat pembelian")
                    navController.navigate(Routes.LOGIN)
                }
            } else {
                OrdersScreen(
                    transactionViewModel = transactionViewModel,
                    onOrderClick = { orderId -> navController.navigate(Routes.orderDetail(orderId)) }
                )
            }
        }

        composable(
            route = Routes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId").orEmpty()
            if (!authState.isLoggedIn) {
                LaunchedEffect(Unit) {
                    onMessage("Silakan login untuk melihat detail transaksi")
                    navController.navigate(Routes.LOGIN)
                }
            } else {
                OrderDetailScreen(
                    orderId = orderId,
                    transactionViewModel = transactionViewModel
                )
            }
        }

        composable(Routes.SELLER_DASHBOARD) {
            SellerDashboardScreen(
                authViewModel = authViewModel,
                sellerViewModel = sellerViewModel,
                onAddProduct = { navController.navigate(Routes.addEditProduct()) },
                onEditProduct = { id -> navController.navigate(Routes.addEditProduct(id)) }
            )
        }

        composable(
            route = Routes.ADD_EDIT_PRODUCT,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStack ->
            val productId = backStack.arguments?.getString("productId").orEmpty().ifBlank { null }
            AddEditProductScreen(
                productId = productId,
                authViewModel = authViewModel,
                marketplaceViewModel = marketplaceViewModel,
                sellerViewModel = sellerViewModel,
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
