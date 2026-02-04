package com.komputerkit.business.screens.penjualan

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.utils.Formatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreorderHistoryScreen(
    userRole: String = "Penjualan",
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit = {},
    onPreorderClick: (String) -> Unit = {}
) {
    // Reuse dummy data from PreorderList for history view
    val preorders = remember {
        listOf(
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
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Preorder History", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(preorders) { p ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = p.transactionId, fontWeight = FontWeight.SemiBold)
                            Text(text = p.customerName, style = MaterialTheme.typography.bodySmall)
                            Text(text = "Pickup: ${p.pickupDate} ${p.pickupTime}", style = MaterialTheme.typography.bodySmall)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = Formatter.formatCurrency(p.totalAmount), fontWeight = FontWeight.Bold)
                            Text(text = p.status, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(onClick = { onPreorderClick(p.id) }) {
                                Text("Lihat Detail")
                            }
                        }
                    }
                }
            }
        }
    }
}
