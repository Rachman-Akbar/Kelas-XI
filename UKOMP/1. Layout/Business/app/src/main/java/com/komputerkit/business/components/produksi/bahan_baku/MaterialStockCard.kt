package com.komputerkit.business.components.bahan_baku

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.komputerkit.business.components.common.AppCard
import com.komputerkit.business.components.common.InfoRow
import com.komputerkit.business.components.common.StatusChip
import com.komputerkit.business.models.Material
import com.komputerkit.business.models.StockStatus

@Composable
fun MaterialStockCard(
    material: Material,
    onDetailClick: () -> Unit,
    onRestockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = material.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            StockStatusBadge(status = material.stockStatus)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(
            label = "Stok Tersedia",
            value = "${material.stock} ${material.unit}"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Harga Beli",
            value = "Rp ${"%,.0f".format(material.purchasePrice)}/${material.unit}"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Supplier",
            value = material.supplier.ifEmpty { "Tidak ada" }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDetailClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Detail")
            }
            
            Button(
                onClick = onRestockClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Restock")
            }
        }
    }
}

@Composable
fun StockStatusBadge(
    status: StockStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        StockStatus.IN_STOCK -> Color(0xFF4CAF50) to "Tersedia"
        StockStatus.LOW_STOCK -> Color(0xFFFF9800) to "Menipis"
        StockStatus.OUT_OF_STOCK -> Color(0xFFF44336) to "Habis"
    }
    
    StatusChip(
        text = text,
        containerColor = color,
        contentColor = Color.White,
        modifier = modifier
    )
}

@Composable
fun MaterialHistoryItem(
    date: String,
    type: String,
    quantity: String,
    notes: String,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (type == "Restock") Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (type == "Restock") Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = type,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Text(
                text = quantity,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (type == "Restock") Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}
