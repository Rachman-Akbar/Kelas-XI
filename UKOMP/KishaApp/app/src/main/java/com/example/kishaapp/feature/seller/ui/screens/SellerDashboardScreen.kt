package com.example.kishaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kishaapp.ui.components.LoadingContent
import com.example.kishaapp.ui.components.ProductCard
import com.example.kishaapp.viewmodel.AuthViewModel
import com.example.kishaapp.viewmodel.SellerViewModel

@Composable
fun SellerDashboardScreen(
    authViewModel: AuthViewModel,
    sellerViewModel: SellerViewModel,
    onAddProduct: () -> Unit,
    onEditProduct: (String) -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val sellerState by sellerViewModel.uiState.collectAsState()
    val sellerId = authState.userProfile?.uid.orEmpty()

    LaunchedEffect(sellerId) {
        if (sellerId.isNotBlank()) {
            sellerViewModel.observeMyProducts(sellerId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                )
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dashboard Seller",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "${sellerState.myProducts.size} produk dikelola",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Button(onClick = onAddProduct, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("  Tambah Produk")
        }

        if (sellerState.isLoading) {
            LoadingContent()
        } else if (sellerState.myProducts.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Belum ada produk. Mulai tambahkan produk pertama Anda.",
                    modifier = Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sellerState.myProducts, key = { it.id }) { item ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ProductCard(product = item) { onEditProduct(item.id) }
                        OutlinedButton(
                            onClick = { sellerViewModel.deleteProduct(item.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Hapus Produk")
                        }
                    }
                }
            }
        }
    }
}
