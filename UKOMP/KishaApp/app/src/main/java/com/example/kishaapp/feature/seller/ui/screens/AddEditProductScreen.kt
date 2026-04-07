package com.example.kishaapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.kishaapp.viewmodel.AuthViewModel
import com.example.kishaapp.viewmodel.MarketplaceViewModel
import com.example.kishaapp.viewmodel.SellerViewModel

@Composable
fun AddEditProductScreen(
    productId: String?,
    authViewModel: AuthViewModel,
    marketplaceViewModel: MarketplaceViewModel,
    sellerViewModel: SellerViewModel,
    onSaved: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    val sellerState by sellerViewModel.uiState.collectAsState()

    val product = productId?.let { marketplaceViewModel.getProductById(it) }

    var title by remember(productId) { mutableStateOf(product?.title.orEmpty()) }
    var description by remember(productId) { mutableStateOf(product?.description.orEmpty()) }
    var priceText by remember(productId) { mutableStateOf(product?.price?.toString().orEmpty()) }
    var category by remember(productId) { mutableStateOf(product?.category.orEmpty()) }
    var location by remember(productId) { mutableStateOf(product?.location.orEmpty()) }
    var type by remember(productId) { mutableStateOf(product?.type ?: "product") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val listState = rememberLazyListState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    LaunchedEffect(sellerState.successMessage) {
        if (!sellerState.successMessage.isNullOrBlank()) {
            onSaved()
            sellerViewModel.clearMessages()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                )
            )
            .padding(16.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (productId == null) "Tambah Produk" else "Edit Produk",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Lengkapi data produk agar tampil menarik di marketplace",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        item {
            OutlinedTextField(title, { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        item {
            OutlinedTextField(description, { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        }
        item {
            OutlinedTextField(
                priceText,
                { priceText = it },
                label = { Text("Harga") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        item {
            OutlinedTextField(category, { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        item {
            OutlinedTextField(location, { location = it }, label = { Text("Lokasi") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        item {
            OutlinedTextField(type, { type = it }, label = { Text("Tipe: product/service") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }

        item {
            Button(onClick = { imagePicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("  Pilih Gambar")
            }
        }
        if (imageUri != null) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Gambar berhasil dipilih",
                        modifier = Modifier.padding(10.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val seller = authState.userProfile ?: return@Button
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    sellerViewModel.addOrUpdateProduct(
                        existingId = productId,
                        title = title,
                        description = description,
                        price = price,
                        category = category,
                        location = location,
                        type = type,
                        sellerId = seller.uid,
                        sellerName = seller.name,
                        imageUri = imageUri,
                        oldImageUrl = product?.imageUrl.orEmpty()
                    )
                },
                enabled = !sellerState.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Produk")
            }
        }

        item {
            if (sellerState.isSaving) {
                CircularProgressIndicator()
            }
        }

        item {
            if (!sellerState.errorMessage.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = sellerState.errorMessage ?: "Terjadi kesalahan",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
