package com.komputerkit.business.components.keuangan

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
import com.komputerkit.business.models.Finance
import com.komputerkit.business.models.TransactionType

@Composable
fun IncomeCard(
    title: String,
    amount: Double,
    category: String,
    date: String,
    description: String,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Text(
                text = "Rp ${"%,.0f".format(amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(label = "Tanggal", value = date)
        if (description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ExpenseCard(
    title: String,
    amount: Double,
    category: String,
    date: String,
    description: String,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Text(
                text = "Rp ${"%,.0f".format(amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF44336)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(label = "Tanggal", value = date)
        if (description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ProfitCard(
    totalIncome: Double,
    totalExpense: Double,
    netProfit: Double,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        containerColor = if (netProfit >= 0) 
            Color(0xFF4CAF50).copy(alpha = 0.1f) 
        else 
            Color(0xFFF44336).copy(alpha = 0.1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = null,
                tint = if (netProfit >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ringkasan Laba/Rugi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoRow(
            label = "Total Pemasukan",
            value = "Rp ${"%,.0f".format(totalIncome)}",
            valueColor = Color(0xFF4CAF50)
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Total Pengeluaran",
            value = "Rp ${"%,.0f".format(totalExpense)}",
            valueColor = Color(0xFFF44336)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        
        InfoRow(
            label = if (netProfit >= 0) "Laba Bersih" else "Rugi Bersih",
            value = "Rp ${"%,.0f".format(kotlin.math.abs(netProfit))}",
            valueColor = if (netProfit >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}

@Composable
fun FinanceTransactionItem(
    finance: Finance,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (finance.type == TransactionType.INCOME) 
                            Icons.Default.TrendingUp 
                        else 
                            Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (finance.type == TransactionType.INCOME) 
                            Color(0xFF4CAF50) 
                        else 
                            Color(0xFFF44336),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = finance.category,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
                        .format(java.util.Date(finance.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                if (finance.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = finance.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Text(
                text = "Rp ${"%,.0f".format(finance.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (finance.type == TransactionType.INCOME) 
                    Color(0xFF4CAF50) 
                else 
                    Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun HppBepCard(
    productName: String,
    hpp: Double,
    bep: Double,
    margin: Double,
    sellingPrice: Double,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
    ) {
        Text(
            text = productName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoRow(label = "HPP", value = "Rp ${"%,.0f".format(hpp)}")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = "BEP", value = "Rp ${"%,.0f".format(bep)}")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(
            label = "Margin",
            value = "${"%.1f".format(margin)}%",
            valueColor = if (margin > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(label = "Harga Jual", value = "Rp ${"%,.0f".format(sellingPrice)}")
    }
}
