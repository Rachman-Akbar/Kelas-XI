package com.komputerkit.easyshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.komputerkit.easyshop.data.model.Product
import com.komputerkit.easyshop.ui.components.LoadingButton
import com.komputerkit.easyshop.ui.viewmodel.CartViewModel
import com.komputerkit.easyshop.ui.viewmodel.FavoriteViewModel
import com.komputerkit.easyshop.ui.viewmodel.ProductDetailViewModel
import java.text.NumberFormat
import java.util.*
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: String,
    productDetailViewModel: ProductDetailViewModel,
    cartViewModel: CartViewModel,
    favoriteViewModel: FavoriteViewModel,
    onNavigateBack: () -> Unit
) {
    val productState by productDetailViewModel.productState.collectAsStateWithLifecycle()
    val cartState by cartViewModel.cartState.collectAsStateWithLifecycle()
    val favoriteState by favoriteViewModel.favoriteState.collectAsStateWithLifecycle()
    val favoriteToggleStates by favoriteViewModel.favoriteToggleStates.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    var quantity by remember { mutableIntStateOf(1) }
    
    // Load product when screen opens
    LaunchedEffect(productId) {
        productDetailViewModel.loadProduct(productId)
        favoriteViewModel.checkFavoriteStatus(productId)
    }
    
    // Show cart messages
    cartState.successMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            cartViewModel.clearSuccessMessage()
        }
    }
    
    cartState.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            cartViewModel.clearError()
        }
    }
    
    // Show favorite messages
    favoriteState.successMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            favoriteViewModel.clearMessages()
        }
    }
    
    favoriteState.error?.let { error ->
        LaunchedEffect(error) {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            favoriteViewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Produk") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val favoriteToggleState = favoriteToggleStates[productId]
                    val isFavorite = favoriteToggleState?.isFavorite ?: false
                    val isLoading = favoriteToggleState?.isLoading ?: false
                    
                    IconButton(
                        onClick = { favoriteViewModel.toggleFavorite(productId) },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (productState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (productState.error != null) {
                val errorMessage = productState.error ?: "Unknown error"
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { productDetailViewModel.loadProduct(productId) }
                    ) {
                        Text("Coba Lagi")
                    }
                }
            } else {
                productState.product?.let { product ->
                    ProductDetailContent(
                        product = product,
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        onAddToCart = { 
                            cartViewModel.addToCart(product, quantity)
                        },
                        isAddingToCart = cartState.isLoading
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: Product,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToCart: () -> Unit,
    isAddingToCart: Boolean
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Product Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = product.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
            contentScale = ContentScale.Crop
        )
        
        // Product Info
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Product Name
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating and Reviews
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${product.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(${product.reviewCount} ulasan)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Price
            Text(
                text = currencyFormatter.format(product.price),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stock Info
            if (product.stock > 0) {
                Text(
                    text = "Stok: ${product.stock} tersedia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Stok habis",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Quantity Selector
            if (product.stock > 0) {
                Text(
                    text = "Jumlah:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { 
                            if (quantity > 1) onQuantityChange(quantity - 1) 
                        },
                        enabled = quantity > 1
                    ) {
                        Text("-")
                    }
                    
                    Text(
                        text = quantity.toString(),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    OutlinedButton(
                        onClick = { 
                            if (quantity < product.stock) onQuantityChange(quantity + 1) 
                        },
                        enabled = quantity < product.stock
                    ) {
                        Text("+")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Description
            Text(
                text = "Deskripsi Produk",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = product.description.ifEmpty { "Tidak ada deskripsi tersedia" },
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Add to Cart Button
            LoadingButton(
                text = "Tambah ke Keranjang",
                onClick = onAddToCart,
                isLoading = isAddingToCart,
                enabled = product.stock > 0 && !isAddingToCart,
                modifier = Modifier.fillMaxWidth()
            )
            
            if (product.stock == 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Produk ini sedang tidak tersedia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}