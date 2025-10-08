package com.komputerkit.easyshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.komputerkit.easyshop.ui.components.BannerView
import com.komputerkit.easyshop.ui.components.CategoryGrid
import com.komputerkit.easyshop.ui.components.HeaderView
import com.komputerkit.easyshop.ui.components.TopProductsSection
import com.komputerkit.easyshop.ui.viewmodel.AuthViewModel
import com.komputerkit.easyshop.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    onNavigateToAuth: () -> Unit,
    onProductClick: ((String) -> Unit)? = null
) {
    val homeState by homeViewModel.homeState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "EasyShop",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Header Section
            HeaderView(
                userName = homeState.userName,
                isLoading = homeState.isUserNameLoading,
                error = homeState.userNameError,
                modifier = Modifier.padding(horizontal = 16.dp),
                onRetry = { homeViewModel.refreshData() }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Banner Section
            BannerView(
                banners = homeState.banners,
                isLoading = homeState.isBannersLoading,
                error = homeState.bannersError,
                modifier = Modifier.padding(horizontal = 16.dp),
                onRetry = { homeViewModel.refreshData() },
                onBannerClick = { banner ->
                    // TODO: Handle banner click
                }
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Categories Section
            CategoryGrid(
                categories = homeState.categories,
                isLoading = homeState.isCategoriesLoading,
                error = homeState.categoriesError,
                onRetry = { homeViewModel.refreshData() },
                onCategoryClick = { category ->
                    // TODO: Navigate to category products
                }
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Top Products Section
            TopProductsSection(
                products = homeState.topProducts,
                isLoading = homeState.isTopProductsLoading,
                error = homeState.topProductsError,
                onRetry = { homeViewModel.refreshData() },
                onProductClick = { product ->
                    onProductClick?.invoke(product.id)
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}