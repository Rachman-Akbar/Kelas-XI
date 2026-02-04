package com.komputerkit.business.screens.produk

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.komputerkit.business.components.produk.ProductCard
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.models.Product
import com.komputerkit.business.navigation.Screen

@Composable
fun ProductListScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Produksi",
    onProductClick: (String) -> Unit,
    onNavigateToAllHistory: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample data
    val sampleProducts = remember {
        listOf(
            Product(
                id = "1",
                name = "Kopi Latte",
                sku = "SKU-001",
                category = "Minuman",
                stock = 45,
                sellingPrice = 25000.0,
                costOfGoodsPrice = 12000.0,
                unit = "cup"
            ),
            Product(
                id = "2",
                name = "Cappuccino",
                sku = "SKU-002",
                category = "Minuman",
                stock = 32,
                sellingPrice = 28000.0,
                costOfGoodsPrice = 14000.0,
                unit = "cup"
            ),
            Product(
                id = "3",
                name = "Croissant",
                sku = "SKU-003",
                category = "Roti",
                stock = 0,
                sellingPrice = 18000.0,
                costOfGoodsPrice = 8000.0,
                unit = "pcs"
            ),
            Product(
                id = "4",
                name = "Americano",
                sku = "SKU-004",
                category = "Minuman",
                stock = 67,
                sellingPrice = 22000.0,
                costOfGoodsPrice = 10000.0,
                unit = "cup"
            )
        )
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Produk",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF197FE6)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(onClick = { /* Filter by date */ }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Tanggal",
                                        tint = Color(0xFF197FE6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(onClick = { /* Filter by category */ }) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = "Kategori",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF8B5CF6)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(onClick = onNavigateToAllHistory) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "Riwayat",
                                        tint = Color(0xFF8B5CF6),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        placeholder = { 
                            Text(
                                "Cari nama produk atau SKU...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = Color(0xFF197FE6)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        bottomBar = {
            AppBottomNavigation(
                selectedRoute = currentRoute,
                onNavigate = onNavigate,
                userRole = userRole
            )
        }
    ) { paddingValues ->
        // Product List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleProducts) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) },
                    onRestock = { onNavigate(Screen.RestockProduct.createRoute(product.id)) },
                    onViewHistory = onNavigateToAllHistory
                )
            }
        }
    }
}
