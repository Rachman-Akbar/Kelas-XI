package com.example.kishaapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kishaapp.ui.components.CartItem
import com.example.kishaapp.ui.components.EmptyContent
import com.example.kishaapp.ui.components.LoadingContent
import com.example.kishaapp.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

private val cartCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    transactionViewModel: TransactionViewModel,
    onNavigateToCheckout: () -> Unit
) {
    val uiState by transactionViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        transactionViewModel.loadCart()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang") },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        TextButton(onClick = transactionViewModel::clearCart) {
                            Text("Kosongkan")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isCartLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center
                ) {
                    LoadingContent()
                }
            }
            uiState.cartItems.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(12.dp)
                ) {
                    EmptyContent("Keranjang masih kosong")
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.cartItems, key = { it.id }) { cartItem ->
                            CartItem(
                                item = cartItem,
                                onIncrement = { transactionViewModel.updateCartItem(cartItem.id, cartItem.quantity + 1) },
                                onDecrement = { transactionViewModel.updateCartItem(cartItem.id, cartItem.quantity - 1) },
                                onRemove = { transactionViewModel.removeCartItem(cartItem.id) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = cartCurrencyFormatter.format(uiState.cartSubtotal),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onNavigateToCheckout,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.cartItems.isNotEmpty()
                    ) {
                        Text("Lanjut Checkout")
                    }
                }
            }
        }
    }
}
