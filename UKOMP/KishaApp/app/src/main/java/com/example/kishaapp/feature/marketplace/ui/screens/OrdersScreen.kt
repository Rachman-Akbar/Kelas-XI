package com.example.kishaapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import com.example.kishaapp.ui.components.EmptyContent
import com.example.kishaapp.ui.components.LoadingContent
import com.example.kishaapp.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

// ─── Brand colors ─────────────────────────────────────────────────────────────
private val GreenPrimary  = Color(0xFF2E7D32)
private val GreenSurface  = Color(0xFFE8F5E9)
private val BgColor       = Color(0xFFF1F8F1)
private val CardBg        = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1B2E1C)
private val TextSecondary = Color(0xFF5A7A5C)

// ─── Status filter tabs ────────────────────────────────────────────────────────
private val statusFilters = listOf("Semua", "Diproses", "Dikirim", "Selesai", "Dibatalkan")

// ─── Main Screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    transactionViewModel: TransactionViewModel,
    onOrderClick: (String) -> Unit
) {
    val uiState by transactionViewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Semua") }

    LaunchedEffect(Unit) { transactionViewModel.loadOrders() }

    val filteredOrders = remember(uiState.orders, selectedFilter) {
        if (selectedFilter == "Semua") uiState.orders
        else uiState.orders.filter {
            it.status.equals(selectedFilter, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ───────────────────────────────────────────────────
            OrdersTopBar(onRefresh = transactionViewModel::loadOrders)

            // ── Header text ───────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                Text(
                    "Daftar Transaksi",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    "Pantau status pesanan Emerald Anda",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(Modifier.height(14.dp))

            // ── Status filter chips ───────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusFilters) { filter ->
                    val selected = filter == selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (selected) GreenPrimary else CardBg
                            )
                            .border(
                                1.dp,
                                if (selected) GreenPrimary else Color(0xFFDDDDDD),
                                RoundedCornerShape(24.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 18.dp, vertical = 9.dp)
                    ) {
                        Text(
                            filter,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Content ───────────────────────────────────────────────────
            when {
                uiState.isOrdersLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { LoadingContent() }
                }
                filteredOrders.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { EmptyContent("Belum ada pesanan") }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        filteredOrders.forEach { order ->
                            OrderCard(
                                order = order,
                                onClick = { onOrderClick(order.id) }
                            )
                        }

                        // ── Promo tip card ──────────────────────────────
                        PromoTipCard()

                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@Composable
private fun OrdersTopBar(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgColor)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            "Marketplace",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GreenPrimary
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ─── Order Card ───────────────────────────────────────────────────────────────
@Composable
private fun OrderCard(
    order: com.example.kishaapp.data.model.Order,
    onClick: () -> Unit
) {
    val statusConfig = getStatusConfig(order.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Store name + status badge ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GreenSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Store,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        "Marketplace",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusConfig.bgColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        statusConfig.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusConfig.textColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Product row ───────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val firstItem = order.items.firstOrNull()
                val totalQty = order.items.sumOf { it.quantity }

                // Product image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    if (!firstItem?.productImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = firstItem?.productImageUrl,
                            contentDescription = null,
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
                            Text("🌿", fontSize = 28.sp)
                        }
                    }
                }

                // Product info
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        firstItem?.productTitle ?: "Produk",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 19.sp
                    )
                    Text(
                        "${if (totalQty > 0) totalQty else 1} Barang",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    val formattedTotal = remember(order.totalPrice) {
                        "Total: Rp " + NumberFormat.getNumberInstance(Locale("id", "ID"))
                            .format(order.totalPrice.toLong())
                    }
                    Text(
                        formattedTotal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenPrimary
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // ── Action buttons ────────────────────────────────────────────
            OrderActionButtons(status = order.status, onClick = onClick)
        }
    }
}

// ─── Action Buttons per status ────────────────────────────────────────────────
@Composable
private fun OrderActionButtons(status: String, onClick: () -> Unit) {
    val normalizedStatus = status.lowercase()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when {
            normalizedStatus.contains("menunggu") || normalizedStatus.contains("unpaid") -> {
                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Bayar Sekarang", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            normalizedStatus.contains("dikirim") || normalizedStatus.contains("shipped") -> {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Lacak", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            normalizedStatus.contains("selesai") || normalizedStatus.contains("completed") -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Beri Ulasan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Beli Lagi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            else -> {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Lihat Detail", fontSize = 13.sp)
                }
            }
        }
    }
}

// ─── Promo Tip Card ───────────────────────────────────────────────────────────
@Composable
private fun PromoTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GreenSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("🌿", fontSize = 22.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Rawat tanaman Anda?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "Cek panduan perawatan terbaru untuk pesanan Anda.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

// ─── Status config ────────────────────────────────────────────────────────────
private data class StatusConfig(
    val label: String,
    val bgColor: Color,
    val textColor: Color
)

private fun getStatusConfig(status: String): StatusConfig {
    val s = status.lowercase()
    return when {
        s.contains("menunggu") || s.contains("unpaid") || s.contains("pending") ->
            StatusConfig("MENUNGGU PEMBAYARAN", Color(0xFFFFEBEE), Color(0xFFD32F2F))
        s.contains("diproses") || s.contains("processing") ->
            StatusConfig("DIPROSES", Color(0xFFFFF3E0), Color(0xFFE65100))
        s.contains("dikirim") || s.contains("shipped") || s.contains("delivery") ->
            StatusConfig("SEDANG DIKIRIM", Color(0xFFE3F2FD), Color(0xFF1565C0))
        s.contains("selesai") || s.contains("completed") || s.contains("delivered") ->
            StatusConfig("SELESAI", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        s.contains("dibatalkan") || s.contains("cancelled") ->
            StatusConfig("DIBATALKAN", Color(0xFFF5F5F5), Color(0xFF757575))
        else ->
            StatusConfig(status.uppercase(), Color(0xFFF5F5F5), Color(0xFF757575))
    }
}