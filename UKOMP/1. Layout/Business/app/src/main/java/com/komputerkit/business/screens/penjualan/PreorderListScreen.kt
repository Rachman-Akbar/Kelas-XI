package com.komputerkit.business.screens.penjualan

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
import com.komputerkit.business.components.common.AppBottomNavigation

data class PreorderItem(
    val id: String,
    val transactionId: String,
    val customerName: String,
    val customerPhone: String,
    val pickupDate: String,
    val pickupTime: String,
    val totalAmount: Double,
    val downPayment: Double,
    val remainingAmount: Double,
    val status: String, // PENDING, READY, COMPLETED, CANCELLED
    val items: List<String>,
    val notes: String,
    val createdDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreorderListScreen(
    userRole: String = "Penjualan",
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit = {},
    onPreorderClick: (String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    
    // Dummy data
    val preorders = remember {
        listOf(
            PreorderItem(
                id = "1",
                transactionId = "PO-20240131001",
                customerName = "Ahmad Wijaya",
                customerPhone = "081234567890",
                pickupDate = "05/02/2026",
                pickupTime = "14:00",
                totalAmount = 250000.0,
                downPayment = 100000.0,
                remainingAmount = 150000.0,
                status = "PENDING",
                items = listOf("Kue Tart Coklat 1kg", "Lilin 10pcs"),
                notes = "Tulisan: Happy Birthday Sarah",
                createdDate = "31/01/2026 10:30"
            ),
            PreorderItem(
                id = "2",
                transactionId = "PO-20240130005",
                customerName = "Siti Nurhaliza",
                customerPhone = "085678901234",
                pickupDate = "03/02/2026",
                pickupTime = "16:00",
                totalAmount = 500000.0,
                downPayment = 200000.0,
                remainingAmount = 300000.0,
                status = "READY",
                items = listOf("Nasi Box 50pcs", "Air Mineral 50pcs"),
                notes = "Untuk acara kantor",
                createdDate = "30/01/2026 14:20"
            ),
            PreorderItem(
                id = "3",
                transactionId = "PO-20240129012",
                customerName = "Budi Santoso",
                customerPhone = "087654321098",
                pickupDate = "02/02/2026",
                pickupTime = "10:00",
                totalAmount = 180000.0,
                downPayment = 180000.0,
                remainingAmount = 0.0,
                status = "COMPLETED",
                items = listOf("Roti Sandwich 20pcs"),
                notes = "",
                createdDate = "29/01/2026 09:15"
            ),
            PreorderItem(
                id = "4",
                transactionId = "PO-20240128008",
                customerName = "Dewi Lestari",
                customerPhone = "082345678901",
                pickupDate = "01/02/2026",
                pickupTime = "18:00",
                totalAmount = 350000.0,
                downPayment = 0.0,
                remainingAmount = 350000.0,
                status = "CANCELLED",
                items = listOf("Pizza Medium 3pcs"),
                notes = "Pelanggan membatalkan",
                createdDate = "28/01/2026 11:45"
            ),
            PreorderItem(
                id = "5",
                transactionId = "PO-20240131002",
                customerName = "Rina Marlina",
                customerPhone = "089876543210",
                pickupDate = "06/02/2026",
                pickupTime = "12:00",
                totalAmount = 420000.0,
                downPayment = 150000.0,
                remainingAmount = 270000.0,
                status = "PENDING",
                items = listOf("Nasi Kotak Premium 30pcs"),
                notes = "Untuk arisan RT",
                createdDate = "31/01/2026 13:20"
            )
        )
    }
    
    val filters = listOf("Semua", "Pending", "Ready", "Completed", "Cancelled")
    
    val filteredPreorders = preorders.filter { preorder ->
        (selectedFilter == "Semua" || preorder.status.equals(selectedFilter, ignoreCase = true)) &&
        (searchQuery.isEmpty() || 
         preorder.customerName.contains(searchQuery, ignoreCase = true) ||
         preorder.transactionId.contains(searchQuery, ignoreCase = true) ||
         preorder.customerPhone.contains(searchQuery, ignoreCase = true))
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFEFF6FF),
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
                            Column {
                                Text(
                                    text = "PREORDER",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "Manajemen Preorder",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Tanggal Icon
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
                            
                            // Kategori Icon
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF197FE6)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(onClick = { /* Filter by category */ }) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = "Kategori",
                                        tint = Color(0xFF197FE6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            
                            // Riwayat Icon
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF197FE6)),
                                modifier = Modifier.size(36.dp)
                            ) {
                                IconButton(onClick = { onNavigate("preorder_history") }) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "Riwayat",
                                        tint = Color(0xFF197FE6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            
                            // (Add button removed)
                        }
                    }
                    
                    HorizontalDivider(color = Color(0xFFE5E7EB))
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari preorder...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
            
            // (Filter chips removed) keep spacing
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Pending",
                        value = preorders.count { it.status == "PENDING" }.toString(),
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Ready",
                        value = preorders.count { it.status == "READY" }.toString(),
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total",
                        value = preorders.size.toString(),
                        color = Color(0xFF197FE6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Preorder List
            if (filteredPreorders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = "Tidak ada preorder",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = "Belum ada preorder yang dibuat",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            } else {
                items(filteredPreorders) { preorder ->
                    PreorderCard(
                        preorder = preorder,
                        onClick = { onPreorderClick(preorder.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

@Composable
fun PreorderCard(
    preorder: PreorderItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preorder.transactionId,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = preorder.customerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (preorder.status) {
                        "PENDING" -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        "READY" -> Color(0xFF10B981).copy(alpha = 0.1f)
                        "COMPLETED" -> Color(0xFF6B7280).copy(alpha = 0.1f)
                        "CANCELLED" -> Color(0xFFEF4444).copy(alpha = 0.1f)
                        else -> Color.Gray.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = preorder.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (preorder.status) {
                            "PENDING" -> Color(0xFFFF9800)
                            "READY" -> Color(0xFF10B981)
                            "COMPLETED" -> Color(0xFF6B7280)
                            "CANCELLED" -> Color(0xFFEF4444)
                            else -> Color.Gray
                        }
                    )
                }
            }
            
            HorizontalDivider(color = Color(0xFFE5E7EB))
            
            // Pickup Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "Tanggal Ambil",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = preorder.pickupDate,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(16.dp)
                    )
                    Column {
                        Text(
                            text = "Jam",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = preorder.pickupTime,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Items
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Items (${preorder.items.size}):",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                preorder.items.take(2).forEach { item ->
                    Text(
                        text = "• $item",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (preorder.items.size > 2) {
                    Text(
                        text = "+ ${preorder.items.size - 2} item lainnya",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF197FE6)
                    )
                }
            }
            
            HorizontalDivider(color = Color(0xFFE5E7EB))
            
            // Payment Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Rp ${String.format("%,.0f", preorder.totalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF197FE6)
                    )
                }
                
                if (preorder.remainingAmount > 0 && preorder.status != "CANCELLED") {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFF9800).copy(alpha = 0.1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Sisa Bayar",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFF9800)
                            )
                            Text(
                                text = "Rp ${String.format("%,.0f", preorder.remainingAmount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }
            }
        }
    }
}
