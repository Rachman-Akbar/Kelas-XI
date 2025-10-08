package com.komputerkit.listdemoapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.komputerkit.listdemoapp.ui.theme.ListDemoAppTheme

// Data classes untuk berbagai contoh list
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val rating: Float
)

data class Message(
    val id: Int,
    val sender: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean
)

/**
 * Berbagai contoh penggunaan LazyColumn
 */
@Composable
fun LazyColumnExamples(modifier: Modifier = Modifier) {
    var selectedExample by remember { mutableIntStateOf(0) }
    
    val exampleTitles = listOf(
        "Simple List (items(count))",
        "Product List (items(list))", 
        "Message List (itemsIndexed)",
        "Mixed Content List"
    )
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Tab-like selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(exampleTitles) { index, title ->
                FilterChip(
                    onClick = { selectedExample = index },
                    label = { Text(title, fontSize = 12.sp) },
                    selected = selectedExample == index
                )
            }
        }
        
        // Content based on selection
        when (selectedExample) {
            0 -> SimpleCountList()
            1 -> ProductList()
            2 -> MessageList()
            3 -> MixedContentList()
        }
    }
}

@Composable
fun SimpleCountList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Menggunakan items(count)
        items(count = 25) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle click */ },
                colors = CardDefaults.cardColors(
                    containerColor = when (index % 3) {
                        0 -> MaterialTheme.colorScheme.primaryContainer
                        1 -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val icon = when (index % 4) {
                        0 -> Icons.Default.Home
                        1 -> Icons.Default.Person // Replacing Business
                        2 -> Icons.Default.LocationOn // Replacing School
                        else -> Icons.Default.ShoppingCart
                    }
                    
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Item #${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Description for item ${index + 1}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductList(modifier: Modifier = Modifier) {
    val products = remember {
        listOf(
            Product(1, "Laptop Gaming", 15000000.0, "Electronics", 4.5f),
            Product(2, "Smartphone", 8000000.0, "Electronics", 4.2f),
            Product(3, "Headphone", 500000.0, "Audio", 4.7f),
            Product(4, "Mechanical Keyboard", 1200000.0, "Accessories", 4.3f),
            Product(5, "Gaming Mouse", 800000.0, "Accessories", 4.4f),
            Product(6, "Monitor 4K", 6000000.0, "Electronics", 4.6f),
            Product(7, "Webcam HD", 1500000.0, "Electronics", 4.1f),
            Product(8, "Power Bank", 300000.0, "Accessories", 4.0f),
            Product(9, "USB Cable", 50000.0, "Accessories", 4.2f),
            Product(10, "Bluetooth Speaker", 2000000.0, "Audio", 4.8f)
        )
    }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Menggunakan items(list)
        items(products) { product ->
            ProductItem(product = product)
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Handle product click */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product icon based on category
            val icon = when (product.category) {
                "Electronics" -> Icons.Default.Phone
                "Audio" -> Icons.Default.Favorite // Replacing MusicNote
                "Accessories" -> Icons.Default.Build // Replacing Devices
                else -> Icons.Default.ShoppingCart
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = product.category,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < product.rating.toInt()) Icons.Default.Star else Icons.Default.Star,
                            contentDescription = "Rating",
                            modifier = Modifier.size(16.dp),
                            tint = if (index < product.rating.toInt()) Color(0xFFFFB000) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }
                    Text(
                        text = " ${product.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Rp ${String.format("%,.0f", product.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier) {
    val messages = remember {
        listOf(
            Message(1, "Ahmad", "Halo, bagaimana kabarnya?", "10:30", false),
            Message(2, "Siti", "Project sudah selesai belum?", "09:15", true),
            Message(3, "Budi", "Meeting jam 2 siang ya", "08:45", true),
            Message(4, "Citra", "File sudah saya kirim", "Yesterday", true),
            Message(5, "Doni", "Terima kasih atas bantuannya", "Yesterday", true),
            Message(6, "Eka", "Besok libur ya?", "2 days ago", false),
            Message(7, "Fitri", "Design mockup sudah ready", "2 days ago", true)
        )
    }
    
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Menggunakan itemsIndexed(list)
        itemsIndexed(messages) { index, message ->
            MessageItem(
                message = message,
                index = index,
                onClick = { /* Handle message click */ }
            )
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (!message.isRead) 
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!message.isRead) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Avatar dengan nomor urut
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${index + 1}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.sender,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (!message.isRead) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!message.isRead) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MixedContentList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu, // Replacing Apps
                        contentDescription = "Dashboard",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mixed Content List",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kombinasi berbagai item types",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Section title
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // Action buttons with items(count)
        items(count = 4) { index ->
            val actions = listOf(
                "Create New Document" to Icons.Default.Add,
                "Import from File" to Icons.Default.KeyboardArrowUp, // Simple arrow up
                "Export Data" to Icons.Default.KeyboardArrowDown, // Simple arrow down
                "Settings" to Icons.Default.Settings
            )
            
            val (title, icon) = actions[index]
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Handle action */ }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight, // Replacing ChevronRight
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Another section
        item {
            Text(
                text = "Recent Items",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        // Recent items with itemsIndexed
        itemsIndexed(listOf("Document 1", "Document 2", "Document 3")) { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "2 hours ago",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Footer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "End of list - Mixed content dengan item, items(count), dan itemsIndexed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LazyColumnExamplesPreview() {
    ListDemoAppTheme {
        LazyColumnExamples()
    }
}

@Preview(showBackground = true)
@Composable
fun ProductItemPreview() {
    ListDemoAppTheme {
        ProductItem(
            product = Product(1, "Gaming Laptop", 15000000.0, "Electronics", 4.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageItemPreview() {
    ListDemoAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MessageItem(
                message = Message(1, "Ahmad", "Halo, bagaimana kabarnya?", "10:30", false),
                index = 0,
                onClick = {}
            )
            MessageItem(
                message = Message(2, "Siti", "Project sudah selesai belum?", "09:15", true),
                index = 1,
                onClick = {}
            )
        }
    }
}