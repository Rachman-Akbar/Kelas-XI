package com.example.kishaapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kishaapp.ui.components.EmptyContent
import com.example.kishaapp.ui.components.LoadingContent
import com.example.kishaapp.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val orderDetailCurrency = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
private val orderDetailDate = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: String,
    transactionViewModel: TransactionViewModel
) {
    val uiState by transactionViewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        transactionViewModel.loadOrderDetail(orderId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detail Transaksi") }) }
    ) { paddingValues ->
        when {
            uiState.isOrdersLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingContent()
                }
            }
            uiState.selectedOrder == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(12.dp)
                ) {
                    EmptyContent("Transaksi tidak ditemukan")
                }
            }
            else -> {
                val order = uiState.selectedOrder!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "${order.transactionCode}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        order.createdAt?.toDate()?.let { createdAt ->
                            Text(
                                text = orderDetailDate.format(Date(createdAt.time)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Status")
                            Text(order.status.uppercase(), fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Text(
                            text = "Item Pembelian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(order.items, key = { it.id }) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(item.productTitle, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "${item.quantity} x ${orderDetailCurrency.format(item.price)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal", style = MaterialTheme.typography.bodySmall)
                                Text(orderDetailCurrency.format(item.subtotal), fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Pembayaran", style = MaterialTheme.typography.titleMedium)
                            Text(
                                orderDetailCurrency.format(order.totalPrice),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}
