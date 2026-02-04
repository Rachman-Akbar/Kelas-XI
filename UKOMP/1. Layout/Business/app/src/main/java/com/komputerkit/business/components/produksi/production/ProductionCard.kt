package com.komputerkit.business.components.produksi

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
import com.komputerkit.business.components.common.DividerLine
import com.komputerkit.business.models.Production
import com.komputerkit.business.models.ProductionStatus

@Composable
fun ProductionItem(
    production: Production,
    onDetailClick: () -> Unit,
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
                    text = production.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Qty: ${production.quantity} pcs",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            ProductionStatusBadge(status = production.status)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(
            label = "Total Biaya",
            value = "Rp ${"%,.0f".format(production.totalCost)}"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Tanggal Produksi",
            value = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
                .format(java.util.Date(production.productionDate))
        )
        
        if (production.notes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Catatan: ${production.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onDetailClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Visibility,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Lihat Detail")
        }
    }
}

@Composable
fun ProductionStatusBadge(
    status: ProductionStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        ProductionStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        ProductionStatus.IN_PROGRESS -> Color(0xFF2196F3) to "Proses"
        ProductionStatus.COMPLETED -> Color(0xFF4CAF50) to "Selesai"
        ProductionStatus.CANCELLED -> Color(0xFFF44336) to "Batal"
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ProductionSummaryCard(
    productName: String,
    quantity: Int,
    materialsUsed: List<MaterialUsed>,
    totalCost: Double,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Factory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ringkasan Produksi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoRow(label = "Produk", value = productName)
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = "Jumlah Produksi", value = "$quantity pcs")
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Bahan Terpakai:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (materialsUsed.isEmpty()) {
            Text(
                text = "Belum ada bahan yang digunakan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            materialsUsed.forEach { material ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${material.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${material.quantity} ${material.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(
            label = "Total Biaya Produksi",
            value = "Rp ${"%,.0f".format(totalCost)}"
        )
    }
}

data class MaterialUsed(
    val name: String,
    val quantity: Double,
    val unit: String
)
