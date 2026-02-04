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
import com.komputerkit.business.components.bahan_baku.MaterialCard
import com.komputerkit.business.components.common.AppBottomNavigation
import com.komputerkit.business.models.Material

@Composable
fun MaterialListScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    userRole: String = "Produksi",
    onNavigateToHistory: () -> Unit,
    onNavigateToRestock: () -> Unit = {},
    onNavigateToMaterialHistory: (String) -> Unit = {},
    onMaterialClick: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Sample data
    val sampleMaterials = remember {
        listOf(
            Material(
                id = "1",
                name = "Biji Kopi Arabica",
                category = "Biji Kopi",
                stock = 15.5,
                unit = "kg",
                minStock = 10.0,
                purchasePrice = 120000.0,
                supplier = "CV Kopi Nusantara"
            ),
            Material(
                id = "2",
                name = "Susu Full Cream",
                category = "Susu",
                stock = 8.0,
                unit = "liter",
                minStock = 15.0,
                purchasePrice = 18000.0,
                supplier = "PT Susu Segar"
            ),
            Material(
                id = "3",
                name = "Gula Pasir",
                category = "Pemanis",
                stock = 25.0,
                unit = "kg",
                minStock = 20.0,
                purchasePrice = 15000.0,
                supplier = "Toko Makmur"
            ),
            Material(
                id = "4",
                name = "Cup Paper 12oz",
                category = "Kemasan",
                stock = 0.0,
                unit = "pcs",
                minStock = 500.0,
                purchasePrice = 800.0,
                supplier = "CV Kemasan Jaya"
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
                            text = "Bahan Baku",
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
                                "Cari nama atau kode bahan...",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRestock,
                containerColor = Color(0xFF197FE6),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Material",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
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
        // Material List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleMaterials) { material ->
                MaterialCard(
                    material = material,
                    onClick = { onMaterialClick(material.id) },
                    onRestock = onNavigateToRestock,
                    onViewHistory = { onNavigateToMaterialHistory(material.id) }
                )
            }
        }
    }
}
