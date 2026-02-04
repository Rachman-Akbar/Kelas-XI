package com.komputerkit.business.components.produk

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
import com.komputerkit.business.models.Product

@Composable
fun ProductInfoCard(
    product: Product,
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
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SKU: ${product.sku}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (product.isActive) 
                    Color(0xFF4CAF50).copy(alpha = 0.15f) 
                else 
                    Color(0xFF9E9E9E).copy(alpha = 0.15f)
            ) {
                Text(
                    text = if (product.isActive) "Aktif" else "Nonaktif",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (product.isActive) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = product.description.ifEmpty { "Tidak ada deskripsi" },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(label = "Kategori", value = product.category)
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = "Stok Tersedia", value = "${product.stock} ${product.unit}")
    }
}

@Composable
fun ProductCompositionCard(
    title: String = "Komposisi Bahan",
    materials: List<MaterialComposition>,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        
        if (materials.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada komposisi bahan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            materials.forEach { material ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = material.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${material.quantity} ${material.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = "Rp ${"%,.0f".format(material.cost)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

data class MaterialComposition(
    val name: String,
    val quantity: Double,
    val unit: String,
    val cost: Double
)

@Composable
fun ProductCostSummaryCard(
    hpp: Double,
    bep: Double,
    margin: Double,
    sellingPrice: Double,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ringkasan Biaya",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoRow(
            label = "HPP (Harga Pokok Produksi)",
            value = "Rp ${"%,.0f".format(hpp)}"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "BEP (Break Even Point)",
            value = "Rp ${"%,.0f".format(bep)}"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Margin Keuntungan",
            value = "${"%.1f".format(margin)}%",
            valueColor = if (margin > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(
            label = "Harga Jual",
            value = "Rp ${"%,.0f".format(sellingPrice)}"
        )
    }
}
