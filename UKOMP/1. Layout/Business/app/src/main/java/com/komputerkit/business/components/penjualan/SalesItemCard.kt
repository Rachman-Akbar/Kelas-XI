package com.komputerkit.business.components.penjualan

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
import com.komputerkit.business.models.Sale
import com.komputerkit.business.models.SaleStatus
import com.komputerkit.business.models.PaymentMethod

@Composable
fun SalesItem(
    sale: Sale,
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
                    text = "Invoice #${sale.invoiceNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sale.customerName.ifEmpty { "Pelanggan Umum" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            SaleStatusBadge(status = sale.status)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(
            label = "Total",
            value = "Rp ${"%,.0f".format(sale.total)}"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Item",
            value = "${sale.items.size} produk"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Pembayaran",
            value = getPaymentMethodText(sale.paymentMethod)
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Tanggal",
            value = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
                .format(java.util.Date(sale.date))
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        OutlinedButton(
            onClick = onDetailClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Lihat Detail")
        }
    }
}

@Composable
fun SaleStatusBadge(
    status: SaleStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        SaleStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        SaleStatus.COMPLETED -> Color(0xFF4CAF50) to "Selesai"
        SaleStatus.CANCELLED -> Color(0xFFF44336) to "Batal"
        SaleStatus.REFUNDED -> Color(0xFF9E9E9E) to "Refund"
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
fun SalesSummaryCard(
    items: List<SaleItemData>,
    subtotal: Double,
    discount: Double,
    tax: Double,
    total: Double,
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
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ringkasan Penjualan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Item Produk:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (items.isEmpty()) {
            Text(
                text = "Belum ada produk",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${item.quantity} × Rp ${"%,.0f".format(item.price)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = "Rp ${"%,.0f".format(item.subtotal)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(label = "Subtotal", value = "Rp ${"%,.0f".format(subtotal)}")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = "Diskon", value = "- Rp ${"%,.0f".format(discount)}")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = "Pajak", value = "Rp ${"%,.0f".format(tax)}")
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(
            label = "Total",
            value = "Rp ${"%,.0f".format(total)}"
        )
    }
}

data class SaleItemData(
    val productName: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)

private fun getPaymentMethodText(method: PaymentMethod): String {
    return when (method) {
        PaymentMethod.CASH -> "Tunai"
        PaymentMethod.TRANSFER -> "Transfer"
        PaymentMethod.DEBIT -> "Debit"
        PaymentMethod.CREDIT -> "Kredit"
        PaymentMethod.E_WALLET -> "E-Wallet"
    }
}
