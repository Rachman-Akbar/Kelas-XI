package com.example.kishaapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.kishaapp.ui.components.EmptyContent
import com.example.kishaapp.ui.components.LoadingContent
import com.example.kishaapp.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

// ─── Brand colors ─────────────────────────────────────────────────────────────
private val GreenPrimary = Color(0xFF2E7D32)
private val GreenSurface = Color(0xFFE8F5E9)
private val BgColor      = Color(0xFFF1F8F1)
private val CardBg       = Color(0xFFFFFFFF)
private val TextPrimary  = Color(0xFF1B2E1C)
private val TextSecondary = Color(0xFF5A7A5C)

// ─── Static category definitions ─────────────────────────────────────────────
private data class CategoryDef(val icon: ImageVector, val label: String, val key: String)

private val staticCategories = listOf(
    CategoryDef(Icons.Default.Devices,       "Elektronik",    "elektronik"),
    CategoryDef(Icons.Default.Checkroom,     "Fashion",       "fashion"),
    CategoryDef(Icons.Default.Restaurant,    "Makanan",       "makanan"),
    CategoryDef(Icons.Default.Build,         "Jasa",          "jasa"),
    CategoryDef(Icons.Default.DirectionsCar, "Otomotif",      "otomotif"),
    CategoryDef(Icons.Default.Chair,         "Rumah Tangga",  "rumah tangga"),
    CategoryDef(Icons.Default.GridView,      "Lainnya",       "lainnya")
)

// ─── Main Screen ──────────────────────────────────────────────────────────────
@Composable
fun CategoryListScreen(
    marketplaceViewModel: MarketplaceViewModel,
    onProductClick: (String) -> Unit
) {
    val uiState by marketplaceViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            CategoryTopBar()

            Spacer(Modifier.height(16.dp))

            // ── Category icon grid ────────────────────────────────────────
            CategoryIconGrid(
                selected = uiState.selectedCategory,
                onSelect = { marketplaceViewModel.onCategorySelected(it) }
            )

            Spacer(Modifier.height(24.dp))

            // ── Products section ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Produk Pilihan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    "Lihat Semua",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary
                )
            }

            Spacer(Modifier.height(14.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        contentAlignment = Alignment.Center
                    ) { LoadingContent() }
                }
                uiState.filteredProducts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(240.dp),
                        contentAlignment = Alignment.Center
                    ) { EmptyContent("Tidak ada produk di kategori ini") }
                }
                else -> {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        uiState.filteredProducts.forEach { product ->
                            HorizontalProductCard(
                                imageUrl = product.imageUrl,
                                name = product.title,
                                seller = product.sellerName,
                                price = product.price,
                                rating = null,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                        Spacer(Modifier.height(100.dp))
                    }
                }
            }
        }

        // ── FAB filter ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
                .size(52.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(GreenPrimary)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Filter",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@Composable
private fun CategoryTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgColor)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            "Kategori",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GreenPrimary
        )
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ─── Category Icon Grid ───────────────────────────────────────────────────────
@Composable
private fun CategoryIconGrid(selected: String, onSelect: (String) -> Unit) {
    val chunked = staticCategories.chunked(4)

    Column(
        modifier = Modifier.padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        chunked.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                row.forEach { cat ->
                    val isSelected = selected.equals(cat.key, ignoreCase = true)
                    CategoryIconItem(
                        icon = cat.icon,
                        label = cat.label,
                        isSelected = isSelected,
                        onClick = { onSelect(cat.key) }
                    )
                }
                // Pad row if less than 4
                repeat(4 - row.size) {
                    Spacer(Modifier.width(64.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryIconItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) GreenSurface
                    else Color(0xFFF0F0F0)
                )
                .then(
                    if (isSelected) Modifier.border(2.dp, GreenPrimary, CircleShape)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) GreenPrimary else Color(0xFF555555),
                modifier = Modifier.size(26.dp)
            )
        }
        Text(
            label,
            fontSize = 11.sp,
            color = if (isSelected) GreenPrimary else TextPrimary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── Horizontal Product Card ──────────────────────────────────────────────────
@Composable
private fun HorizontalProductCard(
    imageUrl: String?,
    name: String,
    seller: String?,
    price: Double,
    rating: Double?,
    onClick: () -> Unit
) {
    val formattedPrice = remember(price) {
        "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(price.toLong())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿", fontSize = 32.sp)
                    }
                }
            }

            // Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 19.sp
                    )
                    if (!seller.isNullOrBlank()) {
                        Text(
                            seller,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        formattedPrice,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenPrimary
                    )
                    if (rating != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("⭐", fontSize = 10.sp)
                                Text(
                                    rating.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}