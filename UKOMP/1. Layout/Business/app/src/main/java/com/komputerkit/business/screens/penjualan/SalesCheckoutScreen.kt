package com.komputerkit.business.screens.penjualan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.data.CartManager
import com.komputerkit.business.models.Product
import com.komputerkit.business.models.CartItem
import com.komputerkit.business.utils.Formatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesCheckoutScreen(
    userRole: String = "Penjualan",
    currentRoute: String = "sales_checkout",
    onNavigate: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onProcessSale: () -> Unit = {}
) {
    var customerName by remember { mutableStateOf("Ahmad Wijaya") }
    var customerPhone by remember { mutableStateOf("081234567890") }
    var paymentMethod by remember { mutableStateOf("Tunai") }
    var discount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isPreorder by remember { mutableStateOf(false) }
    var preorderDate by remember { mutableStateOf("") }
    var preorderTime by remember { mutableStateOf("") }
    var downPayment by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showReceiptDialog by remember { mutableStateOf(false) }
    var transactionId by remember { mutableStateOf("") }
    var refreshTrigger by remember { mutableStateOf(0) }

    // If cart is empty, prefill with dummy products for testing checkout/receipt
    if (CartManager.getTotalItems() == 0) {
        CartManager.addToCart(Product(id = "1", name = "Kopi Latte", sellingPrice = 25000.0, stock = 45), 2)
        CartManager.addToCart(Product(id = "4", name = "Nasi Goreng", sellingPrice = 35000.0, stock = 25), 1)
        CartManager.addToCart(Product(id = "6", name = "Kentang Goreng", sellingPrice = 15000.0, stock = 40), 3)
    }

    val cartItems = CartManager.cartItems.entries.map { (product, quantity) ->
        CartItem(
            id = product.id,
            name = product.name,
            price = product.sellingPrice,
            quantity = quantity,
            image = ""
        )
    }

    // Auto-show receipt on component load
    LaunchedEffect(Unit) {
        if (cartItems.isNotEmpty()) {
            transactionId = "TRX-${System.currentTimeMillis().toString().takeLast(8)}"
            showReceiptDialog = true
        }
    }

    // Get cart items from CartManager
    val subtotal = CartManager.getTotalPrice()
    val discountAmount = discount.toDoubleOrNull() ?: 0.0
    val total = subtotal - discountAmount
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        CartManager.clearCart()
                        onNavigateBack()
                    }) {
                        Text(
                            "Clear Cart",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFEFF6FF)
                )
            )
        },
        bottomBar = {
            AppBottomNavigation(
                selectedRoute = currentRoute,
                onNavigate = onNavigate,
                userRole = userRole
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            // Empty Cart State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "Keranjang Kosong",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tambahkan produk dari katalog",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigate("sales_catalog") },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lihat Katalog")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                // Cart Items Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "DETAIL PESANAN (${cartItems.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            
                            Divider()
                            
                            cartItems.forEach { item ->
                                CartItemRow(item = item)
                                if (item != cartItems.last()) {
                                    Divider()
                                }
                            }
                        }
                    }
                }
                
                // Customer & Payment Details
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Customer Name
                            Column {
                                Text(
                                    text = "Nama Pelanggan *",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = customerName,
                                    onValueChange = { customerName = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, null)
                                    },
                                    placeholder = { Text("Nama pelanggan") },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            
                            // Customer Phone
                            Column {
                                Text(
                                    text = "No. Telepon ${if (isPreorder) "*" else "(Opsional)"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = customerPhone,
                                    onValueChange = { customerPhone = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, null)
                                    },
                                    placeholder = { Text("08xxxxxxxxxx") },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            
                            // Preorder Toggle
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isPreorder) Color(0xFF197FE6).copy(alpha = 0.1f) 
                                                    else MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (isPreorder) Color(0xFF197FE6) else Color(0xFFE5E7EB)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = if (isPreorder) Color(0xFF197FE6) else Color.Gray
                                        )
                                        Column {
                                            Text(
                                                text = "Pesanan Preorder",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Untuk pesanan yang diambil nanti",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = isPreorder,
                                        onCheckedChange = { isPreorder = it }
                                    )
                                }
                            }
                            
                            // Preorder Details (Only shown if preorder is enabled)
                            if (isPreorder) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Pickup Date
                                    Column {
                                        Text(
                                            text = "Tanggal Pengambilan *",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = preorderDate,
                                            onValueChange = { preorderDate = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Icon(Icons.Default.CalendarToday, null)
                                            },
                                            placeholder = { Text("DD/MM/YYYY") },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                    
                                    // Pickup Time
                                    Column {
                                        Text(
                                            text = "Jam Pengambilan *",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = preorderTime,
                                            onValueChange = { preorderTime = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Icon(Icons.Default.AccessTime, null)
                                            },
                                            placeholder = { Text("HH:MM") },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                    
                                    // Down Payment
                                    Column {
                                        Text(
                                            text = "DP / Uang Muka (Opsional)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                            value = downPayment,
                                            onValueChange = { downPayment = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            leadingIcon = {
                                                Icon(Icons.Default.Payments, null)
                                            },
                                            placeholder = { Text("0") },
                                            trailingIcon = {
                                                Text(
                                                    "IDR",
                                                    modifier = Modifier.padding(end = 8.dp),
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                }
                            }
                            
                            // Payment Method
                            Column {
                                Text(
                                    text = "Metode Pembayaran",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilterChip(
                                        selected = paymentMethod == "Tunai",
                                        onClick = { paymentMethod = "Tunai" },
                                        label = { Text("Tunai") }
                                    )
                                    FilterChip(
                                        selected = paymentMethod == "Transfer",
                                        onClick = { paymentMethod = "Transfer" },
                                        label = { Text("Transfer") }
                                    )
                                    FilterChip(
                                        selected = paymentMethod == "QRIS",
                                        onClick = { paymentMethod = "QRIS" },
                                        label = { Text("QRIS") }
                                    )
                                }
                            }
                            
                            // Discount
                            Column {
                                Text(
                                    text = "Diskon (Manual)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = discount,
                                    onValueChange = { discount = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(Icons.Default.Sell, null)
                                    },
                                    placeholder = { Text("0") },
                                    trailingIcon = {
                                        Text(
                                            "IDR",
                                            modifier = Modifier.padding(end = 8.dp),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            
                            // Notes
                            Column {
                                Text(
                                    text = "Catatan (Opsional)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = notes,
                                    onValueChange = { notes = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(Icons.Default.Notes, null)
                                    },
                                    placeholder = { Text("Catatan tambahan...") },
                                    maxLines = 3,
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }
                }
                
                // Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPreorder) Color(0xFFFFF4E5) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPreorder) Color(0xFFFF9800) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (isPreorder) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "PESANAN PREORDER",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9800)
                                    )
                                }
                                HorizontalDivider(color = Color(0xFFFF9800).copy(alpha = 0.3f))
                            }
                            
                            SummaryRow("Subtotal", Formatter.formatCurrency(subtotal))
                            if (discountAmount > 0) {
                                SummaryRow("Diskon", "- ${Formatter.formatCurrency(discountAmount)}", Color(0xFFEF4444))
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Bayar",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = Formatter.formatCurrency(total),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isPreorder) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            if (isPreorder && downPayment.isNotBlank()) {
                                val dpAmount = downPayment.toDoubleOrNull() ?: 0.0
                                val remaining = total - dpAmount
                                
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                
                                SummaryRow("DP Dibayar", Formatter.formatCurrency(dpAmount), Color(0xFF10B981))
                                SummaryRow("Sisa Pembayaran", Formatter.formatCurrency(remaining), Color(0xFFFF9800))
                            }
                        }
                    }
                }
            }
            
            // Bottom Action
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            // Auto-checkout - generate transaction ID and show receipt
                            transactionId = "TRX-${System.currentTimeMillis().toString().takeLast(8)}"
                            showReceiptDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPreorder) Color(0xFFFF9800) else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isPreorder) Icons.Default.Schedule else Icons.Default.PointOfSale,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPreorder) "Buat Pesanan Preorder" else "Proses Penjualan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
            }
        }
    }
    
    // Receipt Dialog
    if (showReceiptDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = if (isPreorder) Color(0xFFFF9800) else Color(0xFF197FE6)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isPreorder) "STRUK PREORDER" else "STRUK PEMBAYARAN",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        // Store Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TOKO KOMPUTERKIT",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Jl. Contoh No. 123, Jakarta",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = "Telp: 021-12345678",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.Gray
                        )
                        
                        // Transaction Info
                        Text(
                            text = "No. Transaksi: $transactionId",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Tanggal: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Kasir: Admin",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        
                        if (isPreorder) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF4E5)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            null,
                                            tint = Color(0xFFFF9800),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            "PESANAN PREORDER",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF9800),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    Text(
                                        "Ambil: $preorderDate jam $preorderTime",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        HorizontalDivider(color = Color.Gray)
                    }
                    
                    // Customer Info
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Pelanggan: $customerName",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            if (customerPhone.isNotBlank()) {
                                Text(
                                    text = "Telp: $customerPhone",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.Gray
                        )
                    }
                    
                    // Items
                    items(cartItems) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${item.quantity} × ${Formatter.formatCurrency(item.price)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = Formatter.formatCurrency(item.price * item.quantity),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Summary
                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.Gray
                        )
                        
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    Formatter.formatCurrency(subtotal),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            if (discountAmount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Diskon", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        "- ${Formatter.formatCurrency(discountAmount)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }
                            
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                thickness = 2.dp
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "TOTAL",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    Formatter.formatCurrency(total),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            
                            if (isPreorder && downPayment.isNotBlank()) {
                                val dpAmount = downPayment.toDoubleOrNull() ?: 0.0
                                val remaining = total - dpAmount
                                
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("DP Dibayar", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        Formatter.formatCurrency(dpAmount),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "Sisa Bayar",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        Formatter.formatCurrency(remaining),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFFF9800),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pembayaran ($paymentMethod)", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        Formatter.formatCurrency(total),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        if (notes.isNotBlank()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color.Gray
                            )
                            Text(
                                text = "Catatan: $notes",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color.Gray
                        )
                        
                        Text(
                            text = "Terima kasih atas pembelian Anda!",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        
                        if (isPreorder) {
                            Text(
                                text = "Jangan lupa ambil pesanan sesuai jadwal!",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReceiptDialog = false
                        CartManager.clearCart()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPreorder) Color(0xFFFF9800) else Color(0xFF197FE6)
                    )
                ) {
                    Icon(Icons.Default.Print, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Cetak Struk")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showReceiptDialog = false
                        CartManager.clearCart()
                        onNavigateBack()
                    }
                ) {
                    Text("Selesai")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = Formatter.formatCurrency(item.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                onClick = { /* Delete */ },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { /* Decrease */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { /* Increase */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
