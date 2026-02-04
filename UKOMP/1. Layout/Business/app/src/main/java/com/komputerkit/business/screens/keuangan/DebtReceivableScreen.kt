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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.components.keuangan.FinanceToggleButton
import com.komputerkit.business.components.keuangan.ToggleOption

// Data classes defined at top level
private enum class DebtStatus {
    PAID,
    OVERDUE,
    APPROACHING,
    ACTIVE
}

private data class DebtReceivableData(
    val id: String,
    val name: String,
    val invoiceNumber: String,
    val amount: Double,
    val dueDate: String,
    val status: String,
    val statusColor: DebtStatus
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtReceivableScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Keuangan",
    onNavigateToHistory: () -> Unit = {},
    onAddDebt: (Boolean) -> Unit = {} // true = Hutang, false = Piutang
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample debt data (Hutang) - matching HTML design
    val debts = remember {
        listOf(
            DebtReceivableData(
                id = "1",
                name = "PT. Bahan Baku Sejahtera",
                invoiceNumber = "INV-2023-0089",
                amount = 12500000.0,
                dueDate = "24 Okt",
                status = "Mendekati",
                statusColor = DebtStatus.APPROACHING
            ),
            DebtReceivableData(
                id = "2",
                name = "Logistik Global Mandiri",
                invoiceNumber = "INV-2023-0072",
                amount = 4200000.0,
                dueDate = "15 Okt",
                status = "Terlambat",
                statusColor = DebtStatus.OVERDUE
            ),
            DebtReceivableData(
                id = "3",
                name = "Sewa Kantor Bulan Okt",
                invoiceNumber = "INV-2023-0065",
                amount = 25000000.0,
                dueDate = "01 Okt",
                status = "Lunas",
                statusColor = DebtStatus.PAID
            ),
            DebtReceivableData(
                id = "4",
                name = "Pajak PPh Pasal 21",
                invoiceNumber = "TAX-2023-10",
                amount = 8750000.0,
                dueDate = "10 Nov",
                status = "Berjalan",
                statusColor = DebtStatus.ACTIVE
            )
        )
    }
    
    // Sample receivable data (Piutang)
    val receivables = remember {
        listOf(
            DebtReceivableData(
                id = "1",
                name = "Cafe Maju Jaya",
                invoiceNumber = "INV-2023-0150",
                amount = 3500000.0,
                dueDate = "28 Jan",
                status = "Berjalan",
                statusColor = DebtStatus.ACTIVE
            ),
            DebtReceivableData(
                id = "2",
                name = "Kantor ABC",
                invoiceNumber = "INV-2023-0148",
                amount = 2000000.0,
                dueDate = "20 Jan",
                status = "Lunas",
                statusColor = DebtStatus.PAID
            )
        )
    }
    
    val totalDebt = 245000.0 // in thousands
    val activeDebts = 12
    val totalReceivable = 412800.0 // in thousands
    val activeReceivables = 8
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Utang & Piutang",
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
                            IconButton(onClick = onNavigateToHistory) {
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
            }
        },
        bottomBar = {
            AppBottomNavigation(
                selectedRoute = currentRoute,
                onNavigate = onNavigate,
                userRole = userRole
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddDebt(selectedTab == 0) },
                containerColor = Color(0xFF197FE6),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah",
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF6F7F8))
        ) {
            // Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Utang Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "TOTAL UTANG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${String.format("%.0f", totalDebt)}k",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$activeDebts Tagihan Aktif",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }
                
                // Total Piutang Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "TOTAL PIUTANG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${String.format("%.0f", totalReceivable)}k",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$activeReceivables Piutang Aktif",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            // Tabs
            FinanceToggleButton(
                selectedIndex = selectedTab,
                onSelectedChange = { selectedTab = it },
                option1 = ToggleOption(
                    text = "Hutang",
                    selectedColor = Color(0xFFEF4444),
                    showDot = true
                ),
                option2 = ToggleOption(
                    text = "Piutang",
                    selectedColor = Color(0xFF10B981),
                    showDot = true
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
                    .padding(bottom = 16.dp)
                    .background(Color(0xFFF6F7F8))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
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
                        focusedBorderColor = Color(0xFF197FE6),
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.2f)
                    ),
                    singleLine = true
                )
            }
            
            // Content List
            val currentList = if (selectedTab == 0) debts else receivables
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF6F7F8)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentList) { item ->
                    DebtReceivableCard(
                        data = item,
                        isDebt = selectedTab == 0,
                        onPay = { /* Handle payment */ }
                    )
                }
                
                // Add bottom spacing
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun DebtReceivableCard(
    data: DebtReceivableData,
    isDebt: Boolean,
    onPay: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (data.statusColor == DebtStatus.PAID)
                Color.White.copy(alpha = 0.7f)
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Name and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                
                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (data.statusColor) {
                        DebtStatus.PAID -> Color(0xFF10B981).copy(alpha = 0.15f)
                        DebtStatus.OVERDUE -> Color(0xFFEF4444).copy(alpha = 0.15f)
                        DebtStatus.APPROACHING -> Color(0xFFF97316).copy(alpha = 0.15f)
                        DebtStatus.ACTIVE -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                    }
                ) {
                    Text(
                        text = data.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (data.statusColor) {
                            DebtStatus.PAID -> Color(0xFF10B981)
                            DebtStatus.OVERDUE -> Color(0xFFEF4444)
                            DebtStatus.APPROACHING -> Color(0xFFF97316)
                            DebtStatus.ACTIVE -> Color(0xFF3B82F6)
                        },
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            
            // Invoice Number and Due Date
            Text(
                text = "${data.invoiceNumber} \u2022 Jatuh tempo ${data.dueDate}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            
            // Amount Label
            Text(
                text = "Jumlah",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            
            // Amount and Arrow/Pay Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isDebt) "-" else "+"} Rp ${String.format("%,d", data.amount.toInt())}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDebt) Color(0xFFEF4444) else Color(0xFF10B981),
                    fontSize = 16.sp
                )
                
                if (data.statusColor != DebtStatus.PAID) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bayar",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF197FE6),
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF197FE6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
