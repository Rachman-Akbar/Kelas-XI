package com.komputerkit.business.screens.keuangan

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
import com.komputerkit.business.components.common.AppBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtReceivableHistoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Keuangan",
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    // Sample complete history data matching HTML design
    val debtHistory = remember {
        listOf(
            DebtReceivableHistoryData(
                id = "1",
                name = "PT. Bahan Baku Sejahtera",
                transactionDate = "12 Okt 2023",
                paymentDate = "24 Okt 2023",
                amount = 12500000.0,
                status = "LUNAS"
            ),
            DebtReceivableHistoryData(
                id = "2",
                name = "Logistik Global Mandiri",
                transactionDate = "05 Okt 2023",
                paymentDate = "15 Okt 2023",
                amount = 4200000.0,
                status = "LUNAS"
            ),
            DebtReceivableHistoryData(
                id = "3",
                name = "Sewa Kantor Pusat",
                transactionDate = "01 Sep 2023",
                paymentDate = "01 Okt 2023",
                amount = 25000000.0,
                status = "LUNAS"
            ),
            DebtReceivableHistoryData(
                id = "4",
                name = "Vendor ATK & Office",
                transactionDate = "20 Agu 2023",
                paymentDate = "30 Agu 2023",
                amount = 1150000.0,
                status = "LUNAS"
            )
        )
    }
    
    val receivableHistory = remember {
        listOf(
            DebtReceivableHistoryData(
                id = "1",
                name = "Cafe Maju Jaya",
                transactionDate = "14 Jan 2026",
                paymentDate = "28 Jan 2026",
                amount = 3500000.0,
                status = "LUNAS"
            ),
            DebtReceivableHistoryData(
                id = "2",
                name = "Kantor ABC",
                transactionDate = "06 Jan 2026",
                paymentDate = "18 Jan 2026",
                amount = 2000000.0,
                status = "LUNAS"
            )
        )
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back",
                                tint = Color(0xFF1F2937),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Riwayat Utang Piutang",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
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
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF6F7F8))
        ) {
            // Toggle Buttons
            com.komputerkit.business.components.keuangan.FinanceToggleButton(
                selectedIndex = selectedTab,
                onSelectedChange = { selectedTab = it },
                option1 = com.komputerkit.business.components.keuangan.ToggleOption(
                    text = "Utang",
                    selectedColor = Color(0xFFEF4444),
                    showDot = true
                ),
                option2 = com.komputerkit.business.components.keuangan.ToggleOption(
                    text = "Piutang",
                    selectedColor = Color(0xFF10B981),
                    showDot = true
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { 
                        Text(
                            "Cari vendor atau tagihan...",
                            fontSize = 14.sp,
                            color = Color.Gray.copy(alpha = 0.6f)
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                        focusedBorderColor = Color(0xFF197FE6)
                    ),
                    singleLine = true
                )
            }
            
            // Transaction List with top spacing
            val currentList = if (selectedTab == 0) debtHistory else receivableHistory
            
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentList) { item ->
                    DebtReceivableHistoryCard(data = item)
                }
                
                // Add bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun DebtReceivableHistoryCard(data: DebtReceivableHistoryData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = data.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    // Transaction Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF197FE6)
                        )
                        Text(
                            text = "Transaksi: ${data.transactionDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                    
                    // Payment Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color(0xFF10B981)
                        )
                        Text(
                            text = "Pembayaran: ${data.paymentDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = data.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            // Divider
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Amount and Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "TOTAL AMOUNT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Rp ${String.format("%,d", data.amount.toInt())}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private data class DebtReceivableHistoryData(
    val id: String,
    val name: String,
    val transactionDate: String,
    val paymentDate: String,
    val amount: Double,
    val status: String
)
