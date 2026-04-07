package com.example.kishaapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
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
private val GreenLight     = Color(0xFF81C784)
private val GreenSurface   = Color(0xFFE8F5E9)
private val BgColor        = Color(0xFFF1F8F1)
private val CardBg         = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1B2E1C)
private val TextSecondary  = Color(0xFF5A7A5C)

// ─── Main Screen ──────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    marketplaceViewModel: MarketplaceViewModel,
    cartItemCount: Int = 0,
    onOpenCart: () -> Unit = {},
    onOpenCategories: () -> Unit = {},
    onProductClick: (String) -> Unit
) {
    val uiState by marketplaceViewModel.uiState.collectAsState()
    val categories = listOf("All") + uiState.categories.map { it.name }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────
        TopSearchBar(
            cartItemCount = cartItemCount,
            onOpenCart = onOpenCart
        )

        // ── Scrollable content ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))

            // Banner carousel
            BannerSection()

            Spacer(Modifier.height(20.dp))

            // Quick categories
            QuickCategories()

            Spacer(Modifier.height(24.dp))

            // "For You" header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "For You",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        "Curated from your interests",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                Text(
                    "View All",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary,
                    modifier = Modifier.clickable(onClick = onOpenCategories)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Category filter chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val selected = uiState.selectedCategory == cat
                    FilterChip(
                        selected = selected,
                        onClick = { marketplaceViewModel.onCategorySelected(cat) },
                        label = {
                            Text(
                                cat,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = CardBg,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = Color(0xFFDDDDDD),
                            selectedBorderColor = GreenPrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Products grid
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
                    ) { EmptyContent("Belum ada produk atau jasa") }
                }
                else -> {
                    // 2-column grid using Column + chunked rows
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
                                    ProductCardItem(
                                        modifier = Modifier.weight(1f),
                                        imageUrl = product.imageUrl,
                                        name = product.title,
                                        price = product.price,
                                        rating = null,
                                        location = product.location,
                                        badge = null,
                                        onClick = { onProductClick(product.id) }
                                    )
                                }
                                // Fill empty slot if odd number
                                if (rowItems.size == 1) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

// ─── Top Search Bar ───────────────────────────────────────────────────────────
@Composable
private fun TopSearchBar(
    cartItemCount: Int,
    onOpenCart: () -> Unit
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
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(CardBg)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Organic Market",
                    fontSize = 14.sp,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }
        }

        // QR code button
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Cart button with badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .clickable(onClick = onOpenCart),
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = {
                    if (cartItemCount > 0) {
                        Badge(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        ) {
                            Text(
                                if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Banner Section ────────────────────────────────────────────────────────────
@Composable
private fun BannerSection() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { BannerCard(
            title = "New Season Sale",
            subtitle = "SEASONAL SELECTION",
            bgStart = Color(0xFF1B5E20),
            bgEnd = Color(0xFF388E3C),
            emoji = "🥬"
        ) }
        item { BannerCard(
            title = "Fresh Fruits",
            subtitle = "LIMITED OFFER",
            bgStart = Color(0xFFAD1457),
            bgEnd = Color(0xFFE91E63),
            emoji = "🍓"
        ) }
    }
}

@Composable
private fun BannerCard(
    title: String,
    subtitle: String,
    bgStart: Color,
    bgEnd: Color,
    emoji: String
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(bgStart, bgEnd)))
    ) {
        // Text
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                subtitle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f),
                letterSpacing = 1.sp
            )
            Text(
                title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                lineHeight = 26.sp
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    "Shop Now",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        // Emoji decoration
        Text(
            emoji,
            fontSize = 64.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )
    }
}

// ─── Quick Categories ─────────────────────────────────────────────────────────
@Composable
private fun QuickCategories() {
    val items = listOf(
        Triple(Icons.Default.Apps,        "All\nCategories", false),
        Triple(Icons.Default.LocalOffer,  "Daily Deals",     false),
        Triple(Icons.Default.Bolt,        "Flash Sale",      false),
        Triple(Icons.Default.Star,        "Top Sellers",     false)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items.forEachIndexed { index, (icon, label, _) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBg)
                        .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    label,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 15.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

// ─── Product Card ─────────────────────────────────────────────────────────────
@Composable
private fun ProductCardItem(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    name: String,
    price: Double,
    rating: Double?,
    location: String?,
    badge: String?,
    onClick: () -> Unit
) {
    val formattedPrice = remember(price) {
        "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(price.toLong())
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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
                            .background(GreenSurface)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌿", fontSize = 36.sp)
                    }
                }

                // Badge
                if (!badge.isNullOrBlank()) {
                    val badgeColor = when (badge.uppercase()) {
                        "HOT"  -> Color(0xFFAD1457)
                        "SALE" -> GreenPrimary
                        "NEW"  -> Color(0xFF1565C0)
                        else   -> GreenPrimary
                    }
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopStart)
                            .clip(RoundedCornerShape(20.dp))
                            .background(badgeColor)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            badge.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Info
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                            Text("│", fontSize = 10.sp, color = TextSecondary.copy(alpha = 0.3f))
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