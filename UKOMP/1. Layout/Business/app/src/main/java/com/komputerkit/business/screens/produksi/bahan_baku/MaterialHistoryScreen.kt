package com.komputerkit.business.screens.bahan_baku

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
import com.komputerkit.business.components.common.AppTopBar
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.utils.Formatter

data class MaterialHistory(
    val id: String,
    val materialName: String,
    val quantity: Double,
    val unit: String,
    val supplier: String,
    val date: Long,
    val cost: Double,
    val notes: String = ""
)

data class MaterialHistoryItem(
    val id: String,
    val materialName: String,
    val quantity: Double,
    val unit: String,
    val type: String, // "Masuk" (Restock) atau "Keluar" (Digunakan untuk produksi)
    val reference: String, // Supplier (Masuk) atau Production ID (Keluar)
    val date: Long,
    val notes: String = ""
)

@Composable
fun MaterialHistoryScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Produksi",
    onNavigateBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Masuk") }
    
    // Sample material history data
    val historyItems = remember {
        listOf(
            MaterialHistoryItem(
                id = "1",
                materialName = "Biji Kopi Arabica",
                quantity = 50.0,
                unit = "kg",
                type = "Masuk",
                reference = "CV Kopi Nusantara",
                date = System.currentTimeMillis() - 86400000,
                notes = "Restock rutin bulanan"
            ),
            MaterialHistoryItem(
                id = "2",
                materialName = "Biji Kopi Arabica",
                quantity = -5.5,
                unit = "kg",
                type = "Keluar",
                reference = "Produksi #PRD-001",
                date = System.currentTimeMillis() - 129600000,
                notes = "Produksi Kopi Latte"
            ),
            MaterialHistoryItem(
                id = "3",
                materialName = "Susu Full Cream",
                quantity = 30.0,
                unit = "liter",
                type = "Masuk",
                reference = "PT Susu Segar",
                date = System.currentTimeMillis() - 172800000,
                notes = "Stok menipis"
            ),
            MaterialHistoryItem(
                id = "4",
                materialName = "Susu Full Cream",
                quantity = -2.0,
                unit = "liter",
                type = "Keluar",
                reference = "Produksi #PRD-002",
                date = System.currentTimeMillis() - 216000000,
                notes = "Produksi Cappuccino"
            ),
            MaterialHistoryItem(
                id = "5",
                materialName = "Gula Pasir",
                quantity = 25.0,
                unit = "kg",
                type = "Masuk",
                reference = "Toko Makmur",
                date = System.currentTimeMillis() - 259200000,
                notes = ""
            ),
            MaterialHistoryItem(
                id = "6",
                materialName = "Cup Paper 12oz",
                quantity = 1000.0,
                unit = "pcs",
                type = "Masuk",
                reference = "CV Kemasan Jaya",
                date = System.currentTimeMillis() - 345600000,
                notes = "Stok habis"
            )
        )
    }
    
    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Kembali",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Text(
                            text = "Riwayat Bahan Baku",
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
                    
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        placeholder = { 
                            Text(
                                "Cari material...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            ) 
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = Color(0xFF197FE6)
                        ),
                        singleLine = true
                    )
                    
                    // Segmented Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shadowElevation = 3.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp)
                            ) {
                                SegmentedButton(
                                    selected = selectedFilter == "Masuk",
                                    onClick = { selectedFilter = "Masuk" },
                                    text = "Masuk",
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.ArrowDownward,
                                    iconTint = Color(0xFF22C55E)
                                )
                                SegmentedButton(
                                    selected = selectedFilter == "Keluar",
                                    onClick = { selectedFilter = "Keluar" },
                                    text = "Keluar",
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.ArrowUpward,
                                    iconTint = Color(0xFFEF4444)
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
        ) {
            // Date Header
            Text(
                text = "HARI INI - ${Formatter.formatDate(System.currentTimeMillis())}".uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            
            // Filtered List
            val filteredItems = historyItems.filter { item ->
                val matchesSearch = item.materialName.contains(searchQuery, ignoreCase = true) ||
                                  item.reference.contains(searchQuery, ignoreCase = true)
                val matchesFilter = when (selectedFilter) {
                    "Masuk" -> item.type == "Masuk"
                    "Keluar" -> item.type == "Keluar"
                    else -> true
                }
                matchesSearch && matchesFilter
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems) { item ->
                    MaterialHistoryCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun SegmentedButton(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: Color? = null
) {
    val backgroundColor = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent
    val textColor = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint ?: textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun MaterialHistoryCard(
    item: MaterialHistoryItem,
    modifier: Modifier = Modifier
) {
    val isIncoming = item.type == "Masuk"
    val cardColor = if (isIncoming) Color(0xFF10B981) else Color(0xFFEF4444)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Circle
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = cardColor.copy(alpha = 0.1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = cardColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.materialName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Formatter.formatDate(item.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFF197FE6).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "STOK AKHIR",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF197FE6),
                            fontSize = MaterialTheme.typography.labelSmall.fontSize * 0.7f
                        )
                    }
                    Text(
                        text = "${Formatter.formatNumber(item.quantity * 10)} ${item.unit}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF197FE6)
                    )
                }
            }
            
            // Quantity
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (isIncoming) "+${Formatter.formatNumber(item.quantity)}" else "-${Formatter.formatNumber(Math.abs(item.quantity))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = cardColor
                )
                Text(
                    text = item.unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun HistoryCard(
    history: MaterialHistory,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = history.materialName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Formatter.formatDate(history.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Supplier
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = history.supplier,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Jumlah",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${Formatter.formatNumber(history.quantity)} ${history.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Biaya",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = Formatter.formatCurrency(history.cost),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (history.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.StickyNote2,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = history.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
