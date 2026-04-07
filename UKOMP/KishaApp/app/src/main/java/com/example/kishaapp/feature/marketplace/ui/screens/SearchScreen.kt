package com.example.kishaapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.kishaapp.ui.components.EmptyContent
import com.example.kishaapp.ui.components.LoadingContent
import com.example.kishaapp.viewmodel.MarketplaceViewModel
import java.text.NumberFormat
import java.util.Locale

// ─── Brand colors ─────────────────────────────────────────────────────────────
private val GreenPrimary   = Color(0xFF2E7D32)
private val GreenSurface   = Color(0xFFE8F5E9)
private val BgColor        = Color(0xFFF1F8F1)
private val CardBg         = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1B2E1C)
private val TextSecondary  = Color(0xFF5A7A5C)

// Static mock data for recent & trending (replace with ViewModel state if needed)
private val recentSearches = listOf("Sayur Organik", "Kopi Arabika", "Pupuk Kompos", "Madu Hutan")
private val trendingItems  = listOf("Beras Merah", "Buah Naga", "Bibit Pohon", "Alat Tani")

// ─── Main Screen ──────────────────────────────────────────────────────────────
@Composable
fun SearchScreen(
    marketplaceViewModel: MarketplaceViewModel,
    onProductClick: (String) -> Unit
) {
    val uiState by marketplaceViewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    var isSearchFocused by remember { mutableStateOf(false) }

    val showResults = uiState.query.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        SearchTopBar(
            query = uiState.query,
            onQueryChange = marketplaceViewModel::onQueryChange,
            onFocusChange = { isSearchFocused = it },
            onSearch = { focusManager.clearFocus() }
        )

        // ── Body ──────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (!showResults) {
                // ── Idle state: recent + trending ─────────────────────────
                Spacer(Modifier.height(4.dp))

                // Recent searches
                SectionHeader(title = "Pencarian Terakhir", actionText = "Hapus", onAction = {})
                Spacer(Modifier.height(10.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentSearches) { item ->
                        RecentChip(
                            label = item,
                            onClick = { marketplaceViewModel.onQueryChange(item) }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Trending
                SectionHeader(title = "Sedang Tren")
                Spacer(Modifier.height(10.dp))

                val chunked = trendingItems.chunked(2)
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    chunked.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            row.forEach { item ->
                                TrendingItem(
                                    label = item,
                                    modifier = Modifier.weight(1f),
                                    onClick = { marketplaceViewModel.onQueryChange(item) }
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

            } else {
                // ── Results ────────────────────────────────────────────────
                Spacer(Modifier.height(4.dp))

                SectionHeader(
                    title = "Hasil Untuk Anda",
                    subtitle = if (!uiState.isLoading)
                        "${uiState.filteredProducts.size} produk ditemukan" else null
                )

                Spacer(Modifier.height(12.dp))

                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) { LoadingContent() }
                    }
                    uiState.filteredProducts.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentAlignment = Alignment.Center
                        ) { EmptyContent("Produk tidak ditemukan") }
                    }
                    else -> {
                        val chunked = uiState.filteredProducts.chunked(2)
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            chunked.forEach { rowItems ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    rowItems.forEach { product ->
                                        SearchProductCard(
                                            modifier = Modifier.weight(1f),
                                            imageUrl = product.imageUrl,
                                            category = product.category,
                                            name = product.title,
                                            price = product.price,
                                            rating = null,
                                            location = product.location,
                                            onClick = { onProductClick(product.id) }
                                        )
                                    }
                                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                                }
                            }
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

// ─── Search Top Bar ────────────────────────────────────────────────────────────
@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search field
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { onFocusChange(it.isFocused) },
            placeholder = {
                Text(
                    "Cari produk atau jasa",
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                cursorColor = GreenPrimary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
        )

        // Filter button
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = TextSecondary)
            }
        }
        if (actionText != null && onAction != null) {
            Text(
                actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GreenPrimary,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

// ─── Recent Chip ──────────────────────────────────────────────────────────────
@Composable
private fun RecentChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─── Trending Item ────────────────────────────────────────────────────────────
@Composable
private fun TrendingItem(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(GreenSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

// ─── Search Product Card ──────────────────────────────────────────────────────
@Composable
private fun SearchProductCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    category: String?,
    name: String,
    price: Double,
    rating: Double?,
    location: String?,
    onClick: () -> Unit
) {
    val formattedPrice = remember(price) {
        "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(price.toLong())
    }

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image + wishlist button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(GreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿", fontSize = 36.sp)
                    }
                }

                // Wishlist button
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                if (!category.isNullOrBlank()) {
                    Text(
                        category,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
                Text(
                    formattedPrice,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenPrimary
                )
                if (rating != null || !location.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        if (rating != null) {
                            Text("⭐", fontSize = 10.sp)
                            Text(
                                rating.toString(),
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if (!location.isNullOrBlank()) {
                            Text(" • ", fontSize = 10.sp, color = TextSecondary.copy(alpha = 0.4f))
                            Text(
                                location,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}