package com.komputerkit.business.screens.penjualan

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.sp

data class SaleItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesDetailScreen(
    transactionId: String,
    onNavigateBack: () -> Unit,
    onPrintReceipt: () -> Unit = {},
    onRefund: () -> Unit = {}
) {
    var showRefundDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    
    // Dummy transaction data
    val transactionNumber = "#TX-${transactionId.take(6).uppercase()}"
    val date = "14 Jan 2026"
    val time = "10:30 WIB"
    val customerName = "Ahmad Wijaya"
    val customerPhone = "081234567890"
    val paymentMethod = "Cash"
    val status = "Completed" // Completed, Pending, Cancelled
    val cashier = "Siti Nurhaliza"
    
    val items = remember {
        listOf(
            SaleItem("1", "Kopi Latte", 2, 25000.0, 50000.0),
            SaleItem("2", "Cappuccino", 1, 28000.0, 28000.0),
            SaleItem("3", "Croissant", 2, 15000.0, 30000.0),
            SaleItem("4", "Sandwich", 1, 35000.0, 35000.0)
        )
    }
    
    val subtotal = items.sumOf { it.subtotal }
    val discount = 5000.0
    val total = subtotal - discount
    val amountPaid = 150000.0
    val change = amountPaid - total
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Back Button
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = Color(0xFF197FE6).copy(alpha = 0.1f)
                            ) {
                                IconButton(onClick = onNavigateBack) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFF197FE6),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            Column {
                                Text(
                                    text = "DETAIL TRANSAKSI",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = transactionNumber,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // More Menu
                        Box {
                            Surface(
                                modifier = Modifier.size(36.dp),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF197FE6))
                            ) {
                                IconButton(onClick = { showMoreMenu = true }) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = Color(0xFF197FE6),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Print Struk") },
                                    onClick = {
                                        showMoreMenu = false
                                        onPrintReceipt()
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Print, null)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Share") },
                                    onClick = {
                                        showMoreMenu = false
                                        // TODO: Share functionality
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Share, null)
                                    }
                                )
                                if (status == "Completed") {
                                    HorizontalDivider()
                                    DropdownMenuItem(
                                        text = { Text("Refund", color = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            showMoreMenu = false
                                            showRefundDialog = true
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.MoneyOff, null, tint = MaterialTheme.colorScheme.error)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    
                    HorizontalDivider(color = Color(0xFFE5E7EB))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Badge
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (status) {
                            "Completed" -> Color(0xFFD1FAE5)
                            "Pending" -> Color(0xFFFEF3C7)
                            "Cancelled" -> Color(0xFFFEE2E2)
                            else -> Color(0xFFF3F4F6)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (status) {
                                    "Completed" -> Icons.Default.CheckCircle
                                    "Pending" -> Icons.Default.Schedule
                                    "Cancelled" -> Icons.Default.Cancel
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = when (status) {
                                    "Completed" -> Color(0xFF059669)
                                    "Pending" -> Color(0xFFD97706)
                                    "Cancelled" -> Color(0xFFDC2626)
                                    else -> Color.Gray
                                },
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = status,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when (status) {
                                        "Completed" -> Color(0xFF059669)
                                        "Pending" -> Color(0xFFD97706)
                                        "Cancelled" -> Color(0xFFDC2626)
                                        else -> Color.Gray
                                    }
                                )
                                Text(
                                    text = "$date • $time",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            
            // Transaction Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Informasi Transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        
                        InfoRow("Nomor Transaksi", transactionNumber)
                        InfoRow("Tanggal", "$date • $time")
                        InfoRow("Kasir", cashier)
                        InfoRow("Metode Pembayaran", paymentMethod)
                    }
                }
            }
            
            // Customer Information
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Informasi Pelanggan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        
                        InfoRow("Nama", customerName)
                        InfoRow("Telepon", customerPhone)
                    }
                }
            }
            
            // Items Header
            item {
                Text(
                    text = "Detail Item (${items.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Items List
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${item.quantity}× @ Rp ${String.format("%,.0f", item.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        
                        Text(
                            text = "Rp ${String.format("%,.0f", item.subtotal)}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Payment Summary
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF197FE6).copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Ringkasan Pembayaran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        
                        SummaryRow("Subtotal", subtotal)
                        if (discount > 0) {
                            SummaryRow("Diskon", -discount, color = Color(0xFFEF4444))
                        }
                        
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rp ${String.format("%,.0f", total)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF197FE6)
                            )
                        }
                        
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        
                        SummaryRow("Dibayar", amountPaid)
                        SummaryRow("Kembalian", change, color = Color(0xFF10B981))
                    }
                }
            }
            
            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrintReceipt,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF197FE6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF197FE6)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Print Struk",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFF197FE6)
                        )
                    }
                    
                    Button(
                        onClick = { /* Share */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF197FE6)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Share",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
    
    // Refund Confirmation Dialog
    if (showRefundDialog) {
        AlertDialog(
            onDismissRequest = { showRefundDialog = false },
            title = { Text("Refund Transaksi?") },
            text = { 
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Apakah Anda yakin ingin melakukan refund untuk transaksi ini?")
                    Text(
                        text = "Total yang akan dikembalikan: Rp ${String.format("%,.0f", total)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRefundDialog = false
                        onRefund()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Ya, Refund")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRefundDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    amount: Double,
    color: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (color != Color.Unspecified) color else Color.Gray
        )
        Text(
            text = "${if (amount < 0) "-" else ""}Rp ${String.format("%,.0f", kotlin.math.abs(amount))}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (color != Color.Unspecified) color else Color.Unspecified
        )
    }
}
