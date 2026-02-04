package com.komputerkit.business.screens.produksi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.models.Production
import com.komputerkit.business.models.ProductionStatus
import com.komputerkit.business.utils.Formatter
import java.util.Locale

@Composable
fun ProductionListScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Produksi",
    onNavigateToHistory: () -> Unit,
    onAddProduction: () -> Unit,
    onProductionClick: (String) -> Unit = {},
    onEditProduction: (String) -> Unit = {},
    onDeleteProduction: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample production data
    val sampleProductions = remember {
        listOf(
            Production(
                id = "1",
                productName = "Kopi Latte",
                quantity = 50,
                status = ProductionStatus.COMPLETED,
                totalCost = 600000.0,
                pic = "John Doe"
            ),
            Production(
                id = "2",
                productName = "Cappuccino",
                quantity = 30,
                status = ProductionStatus.IN_PROGRESS,
                totalCost = 420000.0,
                pic = "Jane Smith"
            ),
            Production(
                id = "3",
                productName = "Croissant",
                quantity = 20,
                status = ProductionStatus.PENDING,
                totalCost = 160000.0,
                pic = "Alice Brown"
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
                        Text(
                            text = "Jadwal Produksi",
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
                    
                    // Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(52.dp),
                        placeholder = {
                            Text(
                                "Cari jadwal produksi...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedBorderColor = Color(0xFF197FE6)
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
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
                onClick = onAddProduction,
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                containerColor = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF197FE6),
                        Color(0xFF1E88E5)
                    )
                ).let { Color(0xFF197FE6) },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 12.dp,
                    pressedElevation = 6.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Production",
                    modifier = Modifier.size(30.dp),
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val filteredProductions = sampleProductions.filter {
                it.productName.contains(searchQuery, ignoreCase = true)
            }
            
            items(filteredProductions) { production ->
                ProductionCard(
                    production = production,
                    onClick = { onProductionClick(production.id) },
                    onEdit = { onEditProduction(production.id) },
                    onDelete = { onDeleteProduction(production.id) },
                    onDetail = { onProductionClick(production.id) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun ProductionCard(
    production: Production,
    onClick: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onDetail: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    
    val statusColor = when (production.status) {
        ProductionStatus.COMPLETED -> Color(0xFF9CA3AF)
        ProductionStatus.IN_PROGRESS -> Color(0xFF3B82F6)
        ProductionStatus.PENDING -> Color(0xFFF59E0B)
        ProductionStatus.CANCELLED -> Color(0xFFEF4444)
    }
    
    val statusText = when (production.status) {
        ProductionStatus.COMPLETED -> "Completed"
        ProductionStatus.IN_PROGRESS -> "In Progress"
        ProductionStatus.PENDING -> "Pending"
        ProductionStatus.CANCELLED -> "Cancelled"
    }
    
    val actionButtonText = when (production.status) {
        ProductionStatus.PENDING -> "Mulai Produksi"
        ProductionStatus.IN_PROGRESS -> "Selesaikan"
        ProductionStatus.COMPLETED -> "Lihat Laporan"
        ProductionStatus.CANCELLED -> "Lihat Detail"
    }
    
    val actionButtonColor = when (production.status) {
        ProductionStatus.PENDING -> Color(0xFF197FE6)
        ProductionStatus.IN_PROGRESS -> Color(0xFF22C55E)
        ProductionStatus.COMPLETED -> Color.Transparent
        ProductionStatus.CANCELLED -> Color.Transparent
    }
    
    val isCompleted = production.status == ProductionStatus.COMPLETED
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCompleted) 1.dp else 4.dp
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = production.productName,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = if (isCompleted) 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                
                Box {
                    IconButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            modifier = Modifier.size(20.dp),
                            tint = if (isCompleted)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.width(160.dp),
                        shape = RoundedCornerShape(12.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        shadowElevation = 8.dp
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Lihat Detail",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDetail()
                            },
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = Color(0xFF3B82F6)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                        
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Edit",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = Color(0xFF10B981)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                        
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Hapus",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFEF4444)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEF4444).copy(alpha = 0.1f),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = Color(0xFFEF4444)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Info Grid (2 columns)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isCompleted) Modifier.alpha(0.6f) else Modifier),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Target Qty Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "TARGET QTY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "${production.quantity} Units",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
                
                // Time Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "WAKTU",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = Formatter.formatTime(production.productionDate),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bottom Row: PIC + Status + Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // PIC Avatar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .then(
                                if (isCompleted) {
                                    Modifier.background(Color(0xFF9CA3AF).copy(alpha = 0.1f))
                                } else {
                                    Modifier.background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF197FE6).copy(alpha = 0.2f),
                                                Color(0xFF197FE6).copy(alpha = 0.05f)
                                            )
                                        )
                                    )
                                }
                            )
                            .then(
                                if (isCompleted) {
                                    Modifier.border(
                                        1.dp,
                                        Color(0xFF9CA3AF).copy(alpha = 0.2f),
                                        CircleShape
                                    )
                                } else {
                                    Modifier.border(
                                        1.dp,
                                        Color(0xFF197FE6).copy(alpha = 0.2f),
                                        CircleShape
                                    )
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = production.pic?.take(2)?.uppercase(Locale.getDefault()) ?: "NA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (isCompleted) 
                                Color(0xFF9CA3AF).copy(alpha = 0.7f)
                            else 
                                Color(0xFF197FE6)
                        )
                    }
                    
                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = statusColor.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            statusColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = if (isCompleted) 
                                Color(0xFF6B7280)
                            else 
                                statusColor,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
                
                // Action Button
                Button(
                    onClick = { /* TODO: Handle action */ },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = actionButtonColor,
                        contentColor = if (production.status == ProductionStatus.COMPLETED)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            Color.White
                    ),
                    border = if (production.status == ProductionStatus.COMPLETED)
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                    else
                        null,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (production.status == ProductionStatus.COMPLETED) 0.dp else 4.dp,
                        pressedElevation = if (production.status == ProductionStatus.COMPLETED) 0.dp else 2.dp
                    )
                ) {
                    Text(
                        text = actionButtonText,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
