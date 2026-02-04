package com.komputerkit.business.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun AppBottomNavigation(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Produksi"
) {
    // Define all possible bottom nav items
    val allItems = listOf(
        BottomNavItem(
            route = "dashboard",
            icon = Icons.Default.Home,
            label = "Beranda"
        ),
        BottomNavItem(
            route = "bahan_baku",
            icon = Icons.Default.Inventory2,
            label = "Bahan Baku"
        ),
        BottomNavItem(
            route = "product_list",
            icon = Icons.Default.Inventory,
            label = "Produk"
        ),
        BottomNavItem(
            route = "production",
            icon = Icons.Default.Factory,
            label = "Produksi"
        ),
        BottomNavItem(
            route = "sales_dashboard",
            icon = Icons.Default.Home,
            label = "Home"
        ),
        BottomNavItem(
            route = "sales_pos",
            icon = Icons.Default.PointOfSale,
            label = "Kasir"
        ),
        BottomNavItem(
            route = "preorder_list",
            icon = Icons.Default.Schedule,
            label = "Preorder"
        ),
        BottomNavItem(
            route = "customer_list",
            icon = Icons.Default.People,
            label = "Pelanggan"
        ),
        BottomNavItem(
            route = "sales_catalog",
            icon = Icons.Default.ShoppingCart,
            label = "Penjualan"
        ),
        BottomNavItem(
            route = "sales_history",
            icon = Icons.Default.History,
            label = "Riwayat"
        ),
        BottomNavItem(
            route = "finance_summary",
            icon = Icons.Default.Assessment,
            label = "Laporan"
        ),
        BottomNavItem(
            route = "finance_dashboard",
            icon = Icons.Default.Home,
            label = "Home"
        ),
        BottomNavItem(
            route = "hpp_bep_analysis",
            icon = Icons.Default.Analytics,
            label = "HPP-BEP"
        ),
        BottomNavItem(
            route = "debt_receivable",
            icon = Icons.Default.AccountBalance,
            label = "Utang"
        ),
        BottomNavItem(
            route = "transaction_history",
            icon = Icons.Default.Receipt,
            label = "Transaksi"
        )
    )
    
    // Filter items based on user role with explicit ordering
    val items = when (userRole) {
        "Keuangan" -> listOf(
            "finance_dashboard",   // 1. Home - Dashboard keuangan utama
            "hpp_bep_analysis",    // 2. HPP-BEP - Analisis HPP-BEP
            "debt_receivable",     // 3. Hutang Piutang - Kelola utang & piutang
            "transaction_history"  // 4. Transaksi - Riwayat transaksi lengkap
        ).mapNotNull { route -> allItems.find { it.route == route } }
        "Penjualan" -> listOf(
            "sales_dashboard",  // 1. Home - Dashboard penjualan utama
            "sales_pos",         // 2. Kasir - Point of Sale
            "preorder_list",     // 3. Preorder - Manajemen preorder
            "customer_list"      // 4. Pelanggan - Customer management
        ).mapNotNull { route -> allItems.find { it.route == route } }
        "Produksi" -> listOf(
            "dashboard",       // 1. Home - Dashboard utama produksi
            "bahan_baku",      // 2. Bahan Baku - List & restock bahan baku
            "product_list",    // 3. Produk - List produk & restock
            "production"       // 4. Produksi - Jadwal & riwayat produksi
        ).mapNotNull { route -> allItems.find { it.route == route } }
        else -> allItems.filter { it.route == "dashboard" } // Default: hanya beranda
    }
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = selectedRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selectedRoute == item.route) 
                            FontWeight.Bold 
                        else 
                            FontWeight.Normal
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
