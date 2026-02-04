package com.komputerkit.business.screens.penjualan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.business.components.common.AppBottomNavigation

data class POSProduct(
    val id: String,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: String,
    val image: String = ""
)

data class POSCartItem(
    val product: POSProduct,
    var quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesPOSScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Penjualan",
    onNavigateBack: () -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val cartItems = remember { 
        mutableStateListOf<POSCartItem>().apply {
            // Pre-populate cart with dummy data
            add(POSCartItem(POSProduct("1", "Kopi Latte", 25000.0, 45, "Minuman"), 2))
            add(POSCartItem(POSProduct("4", "Nasi Goreng", 35000.0, 25, "Makanan"), 1))
            add(POSCartItem(POSProduct("6", "Kentang Goreng", 15000.0, 40, "Snack"), 3))
        }
    }
    
    val categories = remember {
        listOf("Semua", "Minuman", "Makanan", "Snack", "Lainnya")
    }
    
    val products = remember {
        listOf(
            POSProduct("1", "Kopi Latte", 25000.0, 45, "Minuman"),
            POSProduct("2", "Cappuccino", 28000.0, 32, "Minuman"),
            POSProduct("3", "Americano", 20000.0, 50, "Minuman"),
            POSProduct("4", "Nasi Goreng", 35000.0, 25, "Makanan"),
            POSProduct("5", "Mie Goreng", 30000.0, 30, "Makanan"),
            POSProduct("6", "Kentang Goreng", 15000.0, 40, "Snack"),
            POSProduct("7", "Roti Bakar", 18000.0, 35, "Snack"),
            POSProduct("8", "Es Teh", 8000.0, 60, "Minuman"),
            POSProduct("9", "Es Jeruk", 10000.0, 55, "Minuman"),
            POSProduct("10", "Burger", 40000.0, 20, "Makanan")
        )
    }
    
    val filteredProducts = products.filter { product ->
        (selectedCategory == "Semua" || product.category == selectedCategory) &&
        (searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true))
    }
    
    val totalItems = cartItems.sumOf { it.quantity }
    val totalPrice = cartItems.sumOf { (it.product.price * it.quantity) }
    
    fun addToCart(product: POSProduct) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            if (existingItem.quantity < product.stock) {
                existingItem.quantity++
            }
        } else {
            cartItems.add(POSCartItem(product, 1))
        }
    }
    
    fun removeFromCart(productId: String) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            if (item.quantity > 1) {
                item.quantity--
            } else {
                cartItems.remove(item)
            }
        }
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFF6FF),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "POINT OF SALE",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Kasir",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // History Icon
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF197FE6)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(onClick = { onNavigate("sales_history") }) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "Riwayat",
                                        tint = Color(0xFF197FE6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            
                            // Cart Badge
                            BadgedBox(
                                badge = {
                                    if (totalItems > 0) {
                                        Badge(
                                            containerColor = Color(0xFFEF4444)
                                        ) {
                                            Text(
                                                text = totalItems.toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF197FE6).copy(alpha = 0.1f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    IconButton(onClick = { /* Show cart summary */ }) {
                                        Icon(
                                            imageVector = Icons.Default.ShoppingCart,
                                            contentDescription = "Cart",
                                            tint = Color(0xFF197FE6)
                                        )
                                    }
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
                            .height(52.dp)
                            .padding(horizontal = 16.dp),
                        placeholder = {
                            Text(
                                "Cari produk...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = Color(0xFF197FE6)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        },
        bottomBar = {
            Column {
                // Cart Summary
                if (cartItems.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "$totalItems items",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "Rp ${String.format("%,d", totalPrice.toLong())}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                                
                                Button(
                                    onClick = onCheckout,
                                    modifier = Modifier.height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF10B981)
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Checkout",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Bottom Navigation
                AppBottomNavigation(
                    selectedRoute = currentRoute,
                    onNavigate = onNavigate,
                    userRole = userRole
                )
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredProducts) { product ->
                val cartItem = cartItems.find { it.product.id == product.id }
                val quantityInCart = cartItem?.quantity ?: 0
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { addToCart(product) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (quantityInCart > 0) 2.dp else 1.dp,
                        color = if (quantityInCart > 0) Color(0xFF10B981) else Color(0xFFE2E8F0)
                    ),
                    shadowElevation = if (quantityInCart > 0) 4.dp else 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        // Product Image Placeholder
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Icon(
                                imageVector = when (product.category) {
                                    "Minuman" -> Icons.Default.LocalCafe
                                    "Makanan" -> Icons.Default.Restaurant
                                    "Snack" -> Icons.Default.Fastfood
                                    else -> Icons.Default.ShoppingBag
                                },
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Product Info
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = "Stok: ${product.stock}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rp ${String.format("%,d", product.price.toLong())}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF197FE6)
                            )
                            
                            if (quantityInCart > 0) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF10B981),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Text(
                                            text = quantityInCart.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (quantityInCart > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { removeFromCart(product.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFFEE2E2),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Remove",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                                
                                IconButton(
                                    onClick = { addToCart(product) },
                                    modifier = Modifier.size(32.dp),
                                    enabled = quantityInCart < product.stock
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFD1FAE5),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
