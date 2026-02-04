package com.komputerkit.business.screens.bahan_baku

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
import com.komputerkit.business.utils.Formatter
import com.komputerkit.business.components.common.AppBottomNavigation

data class MaterialSpecificHistoryItem(
    val id: String,
    val materialName: String,
    val quantity: Double,
    val unit: String,
    val type: String, // "Masuk" atau "Keluar"
    val reference: String, // Supplier (Masuk) atau Production ID (Keluar)
    val date: Long,
    val notes: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialSpecificHistoryScreen(
    currentRoute: String = "",
    onNavigate: (String) -> Unit = {},
    userRole: String = "Produksi",
    materialId: String = "1",
    materialName: String = "Tepung Terigu",
    onNavigateBack: () -> Unit = {},
    onAddRestock: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("Masuk") }
    
    val historyItems = remember {
        listOf(
            MaterialSpecificHistoryItem(
                id = "1",
                materialName = "Tepung Terigu",
                quantity = 150.0,
                unit = "kg",
                type = "Masuk",
                reference = "Agro Lestari Abadi",
                date = System.currentTimeMillis() - 86400000 * 4,
                notes = "Restock rutin bulanan"
            ),
            MaterialSpecificHistoryItem(
                id = "2",
                materialName = "Tepung Terigu",
                quantity = 25.0,
                unit = "kg",
                type = "Keluar",
                reference = "Produksi #PRD-012",
                date = System.currentTimeMillis() - 86400000 * 2,
                notes = "Produksi Roti Tawar"
            ),
            MaterialSpecificHistoryItem(
                id = "3",
                materialName = "Tepung Terigu",
                quantity = 100.0,
                unit = "kg",
                type = "Masuk",
                reference = "PT Indofood Sukses",
                date = System.currentTimeMillis() - 86400000 * 7,
                notes = "Penambahan stok"
            ),
            MaterialSpecificHistoryItem(
                id = "4",
                materialName = "Tepung Terigu",
                quantity = 15.0,
                unit = "kg",
                type = "Keluar",
                reference = "Produksi #PRD-008",
                date = System.currentTimeMillis() - 86400000 * 5,
                notes = "Produksi Kue Kering"
            ),
            MaterialSpecificHistoryItem(
                id = "5",
                materialName = "Tepung Terigu",
                quantity = 200.0,
                unit = "kg",
                type = "Masuk",
                reference = "Agro Lestari Abadi",
                date = System.currentTimeMillis() - 86400000 * 14,
                notes = "Stok awal bulan"
            )
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Riwayat Bahan Baku",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section
            item {
                Column {
                    Text(
                        text = materialName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Riwayat keluar masuk bahan baku",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BULAN INI",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = "450 kg",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Total Restock",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFF3B82F6).copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF3B82F6),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SUPPLIER UTAMA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3B82F6).copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                text = "Agro Lestari",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                            Text(
                                text = "60% Kontribusi",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF3B82F6).copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            // Segmented Filter Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedFilter == "Masuk") 
                            Color(0xFF22C55E)
                        else 
                            MaterialTheme.colorScheme.surfaceVariant,
                        shadowElevation = if (selectedFilter == "Masuk") 3.dp else 0.dp,
                        onClick = { selectedFilter = "Masuk" }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (selectedFilter == "Masuk") 
                                    Color.White
                                else 
                                    Color(0xFF22C55E),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Masuk",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedFilter == "Masuk") 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedFilter == "Keluar") 
                            Color(0xFFEF4444)
                        else 
                            MaterialTheme.colorScheme.surfaceVariant,
                        shadowElevation = if (selectedFilter == "Keluar") 3.dp else 0.dp,
                        onClick = { selectedFilter = "Keluar" }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (selectedFilter == "Keluar") 
                                    Color.White
                                else 
                                    Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Keluar",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedFilter == "Keluar") 
                                    MaterialTheme.colorScheme.onPrimary 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Section Header
            item {
                Text(
                    text = "Entri Terbaru",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // History Items (Filtered)
            val filteredItems = historyItems.filter { it.type == selectedFilter }
            items(filteredItems) { item ->
                MaterialSpecificHistoryCard(item = item)
            }
        }
    }
}

@Composable
fun MaterialSpecificHistoryCard(item: MaterialSpecificHistoryItem) {
    val isMasuk = item.type == "Masuk"
    val iconColor = if (isMasuk) Color(0xFF10B981) else Color(0xFFEF4444)
    val bgColor = if (isMasuk) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
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
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = bgColor
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMasuk) Icons.Default.LocalShipping else Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isMasuk) "+" else "-",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = iconColor
                        )
                        Text(
                            text = "${item.quantity} ${item.unit}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = item.reference,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (item.notes.isNotEmpty()) {
                        Text(
                            text = item.notes,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = Formatter.formatDate(item.date),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isMasuk) 
                        Color(0xFF10B981).copy(alpha = 0.2f) 
                    else 
                        Color(0xFFEF4444).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = item.type,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isMasuk) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}
