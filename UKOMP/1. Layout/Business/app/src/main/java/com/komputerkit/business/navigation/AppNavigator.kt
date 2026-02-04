package com.komputerkit.business.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.komputerkit.business.screens.auth.LoginScreen
import com.komputerkit.business.screens.auth.SplashScreen
import com.komputerkit.business.screens.bahan_baku.MaterialListScreen
import com.komputerkit.business.screens.bahan_baku.MaterialHistoryScreen
import com.komputerkit.business.screens.bahan_baku.MaterialDetailScreen
import com.komputerkit.business.screens.bahan_baku.RestockMaterialScreen
import com.komputerkit.business.screens.bahan_baku.MaterialSpecificHistoryScreen
import com.komputerkit.business.screens.produksi.ProductionDashboardScreen
import com.komputerkit.business.screens.keuangan.FinanceDashboardScreen
import com.komputerkit.business.screens.keuangan.TransactionFormScreen
import com.komputerkit.business.screens.keuangan.TransactionHistoryScreen
import com.komputerkit.business.screens.keuangan.DebtReceivableScreen
import com.komputerkit.business.screens.keuangan.DebtReceivableHistoryScreen
import com.komputerkit.business.screens.keuangan.DebtFormScreen
import com.komputerkit.business.screens.keuangan.HppBepAnalysisScreen
import com.komputerkit.business.screens.penjualan.SalesDashboardScreen
import com.komputerkit.business.screens.penjualan.SalesPOSScreen
import com.komputerkit.business.screens.penjualan.CustomerListScreen
import com.komputerkit.business.screens.penjualan.CustomerFormScreen
import com.komputerkit.business.screens.penjualan.CustomerDetailScreen
import com.komputerkit.business.screens.penjualan.SalesCheckoutScreen
import com.komputerkit.business.screens.penjualan.SalesHistoryScreen
import com.komputerkit.business.screens.penjualan.SalesDetailScreen
import com.komputerkit.business.screens.penjualan.PreorderListScreen
import com.komputerkit.business.screens.penjualan.PreorderDetailScreen
import com.komputerkit.business.screens.penjualan.PreorderHistoryScreen
import com.komputerkit.business.screens.produk.ProductDetailScreen
import com.komputerkit.business.screens.produk.ProductListScreen
import com.komputerkit.business.screens.produk.ProductCompositionScreen
import com.komputerkit.business.screens.produk.RestockProductScreen
import com.komputerkit.business.screens.produksi.ProductionListScreen
import com.komputerkit.business.screens.produksi.ProductionHistoryScreen
import com.komputerkit.business.screens.produksi.ProductionDetailScreen
import com.komputerkit.business.screens.produksi.ProductionFormScreen
import com.komputerkit.business.screens.produksi.ProductionEditScreen
import com.komputerkit.business.screens.profile.ProfileScreen

@Composable
fun AppNavigator(
    navController: NavHostController = rememberNavController()
) {
    // State untuk menyimpan role user yang login
    val userRole = remember { mutableStateOf("Penjualan") } // Default ke Penjualan
    
    NavHost(
        navController = navController,
        startDestination = Screen.SalesDashboard.route // Langsung ke Dashboard Penjualan
    ) {
        // Auth Flow
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { selectedRole ->
                    userRole.value = selectedRole
                    // Route ke dashboard sesuai role
                    val destination = when (selectedRole) {
                        "Keuangan" -> Screen.FinanceDashboard.route
                        "Penjualan" -> Screen.SalesDashboard.route
                        "Produksi" -> Screen.Dashboard.route
                        else -> Screen.Dashboard.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main Flow
        composable(Screen.Dashboard.route) {
            ProductionDashboardScreen(
                currentRoute = Screen.Dashboard.route,
                onNavigate = { route -> navController.navigate(route) },
                userRole = userRole.value,
                onNavigateToProducts = {
                    navController.navigate(Screen.ProductList.route)
                },
                onNavigateToMaterials = {
                    navController.navigate(Screen.BahanBaku.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        
        // Product Flow
        composable(Screen.ProductList.route) {
            ProductListScreen(
                userRole = userRole.value,
                currentRoute = Screen.ProductList.route,
                onNavigate = { route -> navController.navigate(route) },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToAllHistory = {
                    navController.navigate(Screen.AllProductHistory.route)
                }
            )
        }
        
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() },
                onViewComposition = {
                    navController.navigate(Screen.ProductComposition.createRoute(productId))
                },
                onRestockProduct = { pId ->
                    navController.navigate(Screen.RestockProduct.createRoute(pId))
                }
            )
        }
        
        composable(Screen.ProductComposition.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductCompositionScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RestockProduct.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            RestockProductScreen(
                productId = productId,
                currentRoute = Screen.RestockProduct.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AllProductHistory.route) {
            com.komputerkit.business.screens.produk.AllProductHistoryScreen(
                userRole = userRole.value,
                currentRoute = Screen.AllProductHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // Material Flow
        composable(Screen.BahanBaku.route) {
            MaterialListScreen(
                userRole = userRole.value,
                currentRoute = Screen.BahanBaku.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToHistory = {
                    navController.navigate(Screen.MaterialHistory.route)
                },
                onNavigateToRestock = {
                    navController.navigate(Screen.RestockMaterial.route)
                },
                onNavigateToMaterialHistory = { materialId ->
                    navController.navigate(Screen.MaterialSpecificHistory.createRoute(materialId))
                },
                onMaterialClick = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                }
            )
        }
        
        composable(Screen.MaterialDetail.route) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getString("materialId") ?: ""
            MaterialDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onRestockClick = {
                    navController.navigate(Screen.RestockMaterial.route)
                }
            )
        }
        
        composable(Screen.MaterialHistory.route) {
            MaterialHistoryScreen(
                userRole = userRole.value,
                currentRoute = Screen.MaterialHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.RestockMaterial.route) {
            RestockMaterialScreen(
                currentRoute = Screen.RestockMaterial.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.MaterialSpecificHistory.route) { backStackEntry ->
            val materialId = backStackEntry.arguments?.getString("materialId") ?: "1"
            MaterialSpecificHistoryScreen(
                currentRoute = Screen.MaterialSpecificHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                userRole = userRole.value,
                materialId = materialId,
                onNavigateBack = { navController.popBackStack() },
                onAddRestock = {
                    navController.navigate(Screen.RestockMaterial.route)
                }
            )
        }
        
        // Production Flow
        composable(Screen.Production.route) {
            ProductionListScreen(
                userRole = userRole.value,
                currentRoute = Screen.Production.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToHistory = {
                    navController.navigate(Screen.ProductionHistory.route)
                },
                onAddProduction = {
                    navController.navigate(Screen.ProductionForm.route)
                },
                onProductionClick = { productionId ->
                    navController.navigate(Screen.ProductionDetail.createRoute(productionId))
                },
                onEditProduction = { productionId ->
                    navController.navigate(Screen.ProductionEdit.createRoute(productionId))
                },
                onDeleteProduction = { productionId ->
                    // TODO: Implement delete confirmation dialog
                }
            )
        }
        
        composable(Screen.ProductionHistory.route) {
            ProductionHistoryScreen(
                userRole = userRole.value,
                currentRoute = Screen.ProductionHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ProductionForm.route) {
            ProductionFormScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ProductionDetail.route) { backStackEntry ->
            val productionId = backStackEntry.arguments?.getString("productionId") ?: ""
            ProductionDetailScreen(
                productionId = productionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ProductionEdit.route) { backStackEntry ->
            val productionId = backStackEntry.arguments?.getString("productionId") ?: ""
            ProductionEditScreen(
                productionId = productionId,
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
        
        // Sales Flow
        composable(Screen.SalesDashboard.route) {
            SalesDashboardScreen(
                currentRoute = Screen.SalesDashboard.route,
                onNavigate = { route -> navController.navigate(route) },
                userRole = userRole.value,
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToPOS = {
                    navController.navigate(Screen.SalesPOS.route)
                },
                onNavigateToCustomers = {
                    navController.navigate(Screen.CustomerList.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.SalesHistory.route)
                },
                onSaleClick = { saleId ->
                    navController.navigate(Screen.SalesDetail.createRoute(saleId))
                }
            )
        }
        
        composable(Screen.SalesPOS.route) {
            SalesPOSScreen(
                currentRoute = Screen.SalesPOS.route,
                onNavigate = { route -> navController.navigate(route) },
                userRole = userRole.value,
                onNavigateBack = { navController.popBackStack() },
                onCheckout = {
                    navController.navigate(Screen.SalesCheckout.route)
                }
            )
        }
        
        composable(Screen.CustomerList.route) {
            CustomerListScreen(
                currentRoute = Screen.CustomerList.route,
                onNavigate = { route -> navController.navigate(route) },
                userRole = userRole.value,
                onNavigateBack = { navController.popBackStack() },
                onCustomerClick = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onAddCustomer = {
                    navController.navigate(Screen.CustomerForm.route)
                }
            )
        }
        
        composable(Screen.CustomerForm.route) {
            CustomerFormScreen(
                customerId = null,
                onNavigateBack = { navController.popBackStack() },
                onSave = {
                    // TODO: Save customer to database
                }
            )
        }
        
        composable(Screen.CustomerDetail.route) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerDetailScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() },
                onEdit = {
                    // Navigate to edit form
                    navController.navigate(Screen.CustomerForm.route)
                },
                onDelete = {
                    // TODO: Delete customer from database
                }
            )
        }
        
        composable(Screen.SalesCheckout.route) {
            SalesCheckoutScreen(
                userRole = userRole.value,
                currentRoute = Screen.SalesCheckout.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() },
                onProcessSale = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.SalesHistory.route) {
            SalesHistoryScreen(
                userRole = userRole.value,
                currentRoute = Screen.SalesHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() },
                onAddSale = {
                    navController.navigate(Screen.SalesCheckout.route)
                }
            )
        }
        
        composable(Screen.SalesDetail.route) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            SalesDetailScreen(
                transactionId = transactionId,
                onNavigateBack = { navController.popBackStack() },
                onPrintReceipt = {
                    // TODO: Print receipt functionality
                },
                onRefund = {
                    // TODO: Handle refund
                }
            )
        }
        
        composable(Screen.PreorderList.route) {
            PreorderListScreen(
                userRole = userRole.value,
                currentRoute = Screen.PreorderList.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() },
                onPreorderClick = { preorderId ->
                    navController.navigate(Screen.PreorderDetail.createRoute(preorderId))
                }
            )
        }

        composable(Screen.PreorderHistory.route) {
            PreorderHistoryScreen(
                userRole = userRole.value,
                currentRoute = Screen.PreorderHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() },
                onPreorderClick = { preorderId ->
                    navController.navigate(Screen.PreorderDetail.createRoute(preorderId))
                }
            )
        }
        
        composable(Screen.PreorderDetail.route) { backStackEntry ->
            val preorderId = backStackEntry.arguments?.getString("preorderId") ?: ""
            PreorderDetailScreen(
                preorderId = preorderId,
                onNavigateBack = { navController.popBackStack() },
                onEdit = {
                    // TODO: Navigate to edit preorder
                },
                onDelete = {
                    // TODO: Handle delete
                },
                onUpdateStatus = { newStatus ->
                    // TODO: Update status
                },
                onPrintReceipt = {
                    // TODO: Print receipt
                }
            )
        }
        
        // Finance Flow
        composable(Screen.FinanceDashboard.route) {
            FinanceDashboardScreen(
                userRole = userRole.value,
                currentRoute = Screen.FinanceDashboard.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onAddTransaction = {
                    navController.navigate(Screen.TransactionForm.route)
                },
                onTransactionClick = { transactionId ->
                    // Navigate to transaction detail
                },
                onNavigateToDebtManagement = {
                    navController.navigate(Screen.DebtReceivable.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.TransactionHistory.route)
                }
            )
        }
        
        composable(Screen.TransactionForm.route) {
            TransactionFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.TransactionHistory.route) {
            TransactionHistoryScreen(
                userRole = userRole.value,
                currentRoute = Screen.TransactionHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateBack = { navController.popBackStack() },
                onAddTransaction = {
                    navController.navigate(Screen.TransactionForm.route)
                }
            )
        }
        
        composable(Screen.DebtReceivable.route) {
            DebtReceivableScreen(
                userRole = userRole.value,
                currentRoute = Screen.DebtReceivable.route,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToHistory = {
                    navController.navigate(Screen.DebtReceivableHistory.route)
                },
                onAddDebt = { isDebt ->
                    navController.navigate(Screen.DebtForm.route + "/$isDebt")
                }
            )
        }
        
        composable(Screen.HppBepAnalysis.route) {
            HppBepAnalysisScreen(
                currentRoute = Screen.HppBepAnalysis.route,
                onNavigate = { route -> navController.navigate(route) },
                userRole = userRole.value
            )
        }
        
        composable(Screen.DebtReceivableHistory.route) {
            DebtReceivableHistoryScreen(
                currentRoute = Screen.DebtReceivableHistory.route,
                onNavigate = { route -> navController.navigate(route) },
                userRole = userRole.value,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.DebtForm.route + "/{isDebt}") { backStackEntry ->
            val isDebt = backStackEntry.arguments?.getString("isDebt")?.toBoolean() ?: true
            DebtFormScreen(
                onNavigateBack = { navController.popBackStack() },
                initialIsDebt = isDebt,
                onSave = {
                    // TODO: Save to database
                }
            )
        }
        
        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

