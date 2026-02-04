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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.components.keuangan.FinanceToggleButton
import com.komputerkit.business.components.keuangan.ToggleOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    userRole: String = "Produksi",
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onAddTransaction: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) } // 0=Pemasukan, 1=Pengeluaran
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample transaction data matching HTML design
    val incomeTransactions = remember {
        listOf(
            TransactionData(
                title = "Penjualan Produk A",
                date = "24 Okt 2023",
                time = "Cash",
                category = "Penjualan",
                categoryColor = Color(0xFF10B981),
                amount = 1500000.0,
                icon = Icons.Default.Payments,
                iconColor = Color(0xFF10B981),
                month = "Oktober 2023"
            ),
            TransactionData(
                title = "Pelunasan Invoice #882",
                date = "23 Okt 2023",
                time = "Transfer Bank",
                category = "Piutang",
                categoryColor = Color(0xFF3B82F6),
                amount = 12500000.0,
                icon = Icons.Default.AccountBalanceWallet,
                iconColor = Color(0xFF3B82F6),
                month = "Oktober 2023"
            ),
            TransactionData(
                title = "Pelunasan Piutang Vendor",
                date = "22 Okt 2023",
                time = "Transfer Bank",
                category = "E-Commerce",
                categoryColor = Color(0xFF10B981),
                amount = 4200000.0,
                icon = Icons.Default.AccountBalanceWallet,
                iconColor = Color(0xFF3B82F6),
                month = "Oktober 2023"
            ),
            TransactionData(
                title = "Penjualan Marketplace",
                date = "21 Okt 2023",
                time = "16:45",
                category = "E-Commerce",
                categoryColor = Color(0xFF10B981),
                amount = 850000.0,
                icon = Icons.Default.Storefront,
                iconColor = Color(0xFF10B981),
                month = "September 2023"
            ),
            TransactionData(
                title = "Pendapatan Jasa Konsultasi",
                date = "28 Sep 2023",
                time = "Transfer Bank",
                category = "Jasa",
                categoryColor = Color(0xFF10B981),
                amount = 8750000.0,
                icon = Icons.Default.Storefront,
                iconColor = Color(0xFF10B981),
                month = "September 2023"
            ),
            TransactionData(
                title = "Refund Deposit Sewa",
                date = "15 Sep 2023",
                time = "Cash",
                category = "Lain-lain",
                categoryColor = Color(0xFF8B5CF6),
                amount = 2000000.0,
                icon = Icons.Default.Receipt,
                iconColor = Color(0xFF8B5CF6),
                month = "September 2023"
            )
        )
    }
    
    val expenseTransactions = remember {
        listOf(
            TransactionData(
                title = "Bahan Baku Utama",
                date = "22 Okt 2023",
                time = "09:30",
                category = "HPP",
                categoryColor = Color(0xFFEF4444),
                amount = 4200000.0,
                icon = Icons.Default.ShoppingCart,
                iconColor = Color(0xFFEF4444),
                month = "Oktober 2023"
            ),
            TransactionData(
                title = "Listrik & Air",
                date = "21 Okt 2023",
                time = "16:45",
                category = "Operasional",
                categoryColor = Color(0xFFEF4444),
                amount = 850000.0,
                icon = Icons.Default.ElectricBolt,
                iconColor = Color(0xFF8B5CF6),
                month = "Oktober 2023"
            )
        )
    }
    
    val totalIncome = 842500.0
    val totalExpense = 312800.0
    
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
                        text = "Transaksi",
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
                onClick = onAddTransaction,
                containerColor = Color(0xFF197FE6),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Transaksi",
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
                // Total Income Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TOTAL PEMASUKAN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${String.format("%.1f", totalIncome)}k",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bulan ini",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 10.sp
                        )
                    }
                }
                
                // Total Expense Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TOTAL PENGELUARAN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${String.format("%.1f", totalExpense)}k",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bulan ini",
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
                    text = "Pemasukan",
                    selectedColor = Color(0xFF10B981),
                    showDot = false
                ),
                option2 = ToggleOption(
                    text = "Pengeluaran",
                    selectedColor = Color(0xFFEF4444),
                    showDot = false
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Search Bar (matching dashboard 9 design)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
                    .padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { 
                        Text(
                            "Cari transaksi...",
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
            
            // Transaction List grouped by month
            val currentTransactions = if (selectedTab == 0) incomeTransactions else expenseTransactions
            val groupedByMonth = currentTransactions.groupBy { it.month }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                groupedByMonth.forEach { (month, transactions) ->
                    item {
                        Text(
                            text = month.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 12.dp, top = if (month == groupedByMonth.keys.first()) 0.dp else 24.dp)
                        )
                    }
                    
                    items(transactions) { transaction ->
                        TransactionCard(
                            transaction = transaction,
                            isIncome = selectedTab == 0
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
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
private fun TransactionCard(
    transaction: TransactionData,
    isIncome: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Title and Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                
                // Category Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = transaction.categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = transaction.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = transaction.categoryColor,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
            
            // Date and Time
            Text(
                text = "${transaction.date} \u2022 ${transaction.time}",
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
            
            // Amount and Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isIncome) "+" else "-"} Rp ${String.format("%,d", transaction.amount.toInt())}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444),
                    fontSize = 16.sp
                )
                
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

private data class TransactionData(
    val title: String,
    val date: String,
    val time: String,
    val category: String,
    val categoryColor: Color,
    val amount: Double,
    val icon: ImageVector,
    val iconColor: Color,
    val month: String
)
