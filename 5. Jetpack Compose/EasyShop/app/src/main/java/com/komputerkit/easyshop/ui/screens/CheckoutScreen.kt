package com.komputerkit.easyshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.komputerkit.easyshop.data.model.CartItem
import com.komputerkit.easyshop.data.model.OrderItem
import com.komputerkit.easyshop.data.model.ShippingAddress
import com.komputerkit.easyshop.ui.viewmodel.CartViewModel
import com.komputerkit.easyshop.ui.viewmodel.OrderViewModel
import java.text.NumberFormat
import java.util.*
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSuccess: (String, String) -> Unit,
    recipientName: String = "",
    recipientAddress: String = "",
    recipientPhone: String = ""
) {
    val cartState by cartViewModel.cartState.collectAsStateWithLifecycle()
    val orderState by orderViewModel.ordersState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var selectedPaymentMethod by remember { mutableStateOf("COD") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    // Calculate totals
    val subtotal = cartState.cartItems.sumOf { it.totalPrice }
    val tax = subtotal * 0.11 // 11% PPN
    val shippingCost = 15000.0
    val total = subtotal + tax + shippingCost
    
    // Handle order creation success
    orderState.successMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            // Clear cart after successful order
            cartViewModel.clearCart()
            orderViewModel.clearSuccessMessage()
            // Navigate to success screen with order details
            onNavigateToSuccess("ORD-${System.currentTimeMillis()}", currencyFormatter.format(total))
        }
    }
    
    // Handle order creation error
    orderState.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }
    
    // Confirm Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Pesanan") },
            text = {
                Column {
                    Text("Apakah Anda yakin ingin melanjutkan pembayaran?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Total: ${currencyFormatter.format(total)}",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        processCheckout(
                            cartState.cartItems,
                            recipientName,
                            recipientAddress,
                            recipientPhone,
                            selectedPaymentMethod,
                            subtotal,
                            tax,
                            shippingCost,
                            total,
                            orderViewModel
                        )
                    },
                    enabled = !orderState.isLoading
                ) {
                    if (orderState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Ya, Bayar Sekarang")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Checkout",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Shipping Address Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alamat Pengiriman",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = recipientName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = recipientAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recipientPhone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Order Items Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = "Items",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ringkasan Pesanan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    cartState.cartItems.forEach { cartItem ->
                        CheckoutItemRow(cartItem = cartItem)
                        if (cartItem != cartState.cartItems.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Payment Method Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = "Payment",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Metode Pembayaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    PaymentMethodOption(
                        title = "Cash on Delivery (COD)",
                        description = "Bayar saat barang diterima",
                        isSelected = selectedPaymentMethod == "COD",
                        onSelect = { selectedPaymentMethod = "COD" }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    PaymentMethodOption(
                        title = "Transfer Bank",
                        description = "Transfer ke rekening toko",
                        isSelected = selectedPaymentMethod == "TRANSFER",
                        onSelect = { selectedPaymentMethod = "TRANSFER" }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Order Summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ringkasan Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OrderSummaryRow("Subtotal", currencyFormatter.format(subtotal))
                    OrderSummaryRow("PPN (11%)", currencyFormatter.format(tax))
                    OrderSummaryRow("Ongkos Kirim", currencyFormatter.format(shippingCost))
                    
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = currencyFormatter.format(total),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Checkout Button
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                enabled = !orderState.isLoading && cartState.cartItems.isNotEmpty(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (orderState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Buat Pesanan - ${currencyFormatter.format(total)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CheckoutItemRow(cartItem: CartItem) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cartItem.productImageUrl)
                .crossfade(true)
                .build(),
            contentDescription = cartItem.productName,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = cartItem.productName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${cartItem.quantity}x ${currencyFormatter.format(cartItem.productPrice)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = currencyFormatter.format(cartItem.totalPrice),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PaymentMethodOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder()
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

private fun processCheckout(
    cartItems: List<CartItem>,
    recipientName: String,
    recipientAddress: String,
    recipientPhone: String,
    paymentMethod: String,
    subtotal: Double,
    tax: Double,
    shippingCost: Double,
    total: Double,
    orderViewModel: OrderViewModel
) {
    val orderItems = cartItems.map { cartItem ->
        OrderItem(
            productId = cartItem.productId,
            productName = cartItem.productName,
            productPrice = cartItem.productPrice,
            productImageUrl = cartItem.productImageUrl,
            quantity = cartItem.quantity
        )
    }
    
    val shippingAddress = ShippingAddress(
        recipientName = recipientName,
        fullAddress = recipientAddress,
        phoneNumber = recipientPhone
    )
    
    orderViewModel.createOrder(
        items = orderItems,
        shippingAddress = shippingAddress,
        subtotal = subtotal,
        tax = tax,
        shippingCost = shippingCost,
        total = total
    )
}