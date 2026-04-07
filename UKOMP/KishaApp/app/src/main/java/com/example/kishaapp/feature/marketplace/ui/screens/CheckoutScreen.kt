package com.example.kishaapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
private val DividerColor  = Color(0xFFF0F0F0)

private fun formatRupiah(amount: Double): String =
    "Rp " + NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount.toLong())

// ─── Main Screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    transactionViewModel: TransactionViewModel,
    onCheckoutSuccess: (String) -> Unit
) {
    val uiState by transactionViewModel.uiState.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }
    var selectedPayment by remember { mutableStateOf("gopay") }

    LaunchedEffect(Unit) { transactionViewModel.loadCart() }

    // ── Confirm dialog ────────────────────────────────────────────────────
    if (showConfirm) {
        ConfirmCheckoutDialog(
            total = uiState.cartSubtotal,
            isLoading = uiState.isCheckoutLoading,
            onConfirm = {
                showConfirm = false
                transactionViewModel.checkout(onSuccess = onCheckoutSuccess)
            },
            onDismiss = { showConfirm = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ───────────────────────────────────────────────────
            CheckoutTopBar()

            when {
                uiState.isCartLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { LoadingContent() }
                }
                uiState.cartItems.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { EmptyContent("Tidak ada item untuk checkout") }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(Modifier.height(8.dp))

                        // ── Delivery Address ──────────────────────────────
                        SectionCard {
                            SectionHeader(
                                icon = Icons.Default.LocationOn,
                                title = "Delivery Address",
                                actionIcon = Icons.Default.Edit,
                                onAction = {}
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Sarah Jenkins  |  +62 812 3456 7890",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                "Greenwich Village Residences, Block C No. 12,\nKebayoran Baru, South Jakarta, 12150",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        // ── Order Summary ─────────────────────────────────
                        SectionCard {
                            SectionHeader(
                                icon = Icons.Default.ShoppingBag,
                                title = "Order Summary"
                            )
                            Spacer(Modifier.height(12.dp))
                            uiState.cartItems.forEach { item ->
                                CheckoutItemRow(
                                    imageUrl = item.productImageUrl,
                                    title = item.productTitle,
                                    price = item.price,
                                    quantity = item.quantity
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // ── Shipping Method ───────────────────────────────
                        SectionCard {
                            SectionHeader(
                                icon = Icons.Default.LocalShipping,
                                title = "Shipping Method"
                            )
                            Spacer(Modifier.height(10.dp))
                            ShippingOptionRow(
                                courier = "J&T Express - Regular",
                                eta = "Estimated arrival: Oct 24 - Oct 26",
                                price = "Rp 22.000"
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        // ── Payment Method ────────────────────────────────
                        SectionCard {
                            SectionHeader(
                                icon = Icons.Default.Wallet,
                                title = "Payment Method"
                            )
                            Spacer(Modifier.height(10.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                PaymentOption(
                                    icon = Icons.Default.AccountBalanceWallet,
                                    label = "GoPay / OVO (E-Wallet)",
                                    value = "gopay",
                                    selected = selectedPayment,
                                    onSelect = { selectedPayment = it }
                                )
                                PaymentOption(
                                    icon = Icons.Default.AccountBalance,
                                    label = "Virtual Account (Bank Transfer)",
                                    value = "va",
                                    selected = selectedPayment,
                                    onSelect = { selectedPayment = it }
                                )
                                PaymentOption(
                                    icon = Icons.Default.Money,
                                    label = "Cash on Delivery (COD)",
                                    value = "cod",
                                    selected = selectedPayment,
                                    onSelect = { selectedPayment = it }
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // ── Order Notes ───────────────────────────────────
                        SectionCard {
                            SectionHeader(
                                icon = Icons.Default.EditNote,
                                title = "Order Notes"
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                placeholder = {
                                    Text(
                                        "e.g. Please leave at the security desk...",
                                        fontSize = 13.sp,
                                        color = TextSecondary.copy(alpha = 0.5f)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenPrimary,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedContainerColor = GreenSurface,
                                    unfocusedContainerColor = Color(0xFFF9F9F9),
                                    cursorColor = GreenPrimary
                                ),
                                maxLines = 3
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        // ── Price breakdown ───────────────────────────────
                        SectionCard {
                            val subtotal = uiState.cartSubtotal
                            val shipping = 22000.0
                            val admin = 1000.0
                            val total = subtotal + shipping + admin

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                PriceLine(
                                    label = "Subtotal (${uiState.cartItems.size} item)",
                                    value = formatRupiah(subtotal)
                                )
                                PriceLine(label = "Shipping Fee", value = formatRupiah(shipping))
                                PriceLine(label = "Admin Fee", value = formatRupiah(admin))
                                HorizontalDivider(color = DividerColor, thickness = 1.dp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "Total Payment",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        formatRupiah(total),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GreenPrimary
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }

                    // ── Bottom CTA ────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgColor)
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Button(
                            onClick = { showConfirm = true },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp),
                            enabled = !uiState.isCheckoutLoading
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF388E3C), Color(0xFF1B5E20))
                                        ),
                                        RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (uiState.isCheckoutLoading) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Text(
                                        "Buat Pesanan",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────
@Composable
private fun CheckoutTopBar() {
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
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            "Checkout",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GreenPrimary
        )
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            tint = TextPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ─── Section Card ─────────────────────────────────────────────────────────────
@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    actionIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GreenSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
            }
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        if (actionIcon != null && onAction != null) {
            IconButton(onClick = onAction, modifier = Modifier.size(28.dp)) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = "Edit",
                    tint = GreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ─── Checkout Item Row ────────────────────────────────────────────────────────
@Composable
private fun CheckoutItemRow(
    imageUrl: String?,
    title: String,
    price: Double,
    quantity: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(GreenSurface),
                    contentAlignment = Alignment.Center
                ) { Text("🌿", fontSize = 26.sp) }
            }
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary,
                maxLines = 2, overflow = TextOverflow.Ellipsis
            )
            Text(formatRupiah(price), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = GreenPrimary)
        }

        // Quantity display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) { Text("−", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold) }
            Text(quantity.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) { Text("+", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold) }
        }
    }
}

// ─── Shipping Option ──────────────────────────────────────────────────────────
@Composable
private fun ShippingOptionRow(courier: String, eta: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF9F9F9))
            .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(courier, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(eta, fontSize = 11.sp, color = TextSecondary)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(price, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(14.dp).graphicsLayer { rotationZ = 180f }
            )
        }
    }
}

// ─── Payment Option ───────────────────────────────────────────────────────────
@Composable
private fun PaymentOption(
    icon: ImageVector,
    label: String,
    value: String,
    selected: String,
    onSelect: (String) -> Unit
) {
    val isSelected = value == selected
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) GreenSurface else Color(0xFFF9F9F9))
            .border(
                1.dp,
                if (isSelected) GreenPrimary else Color(0xFFE8E8E8),
                RoundedCornerShape(14.dp)
            )
            .clickable { onSelect(value) }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
        Text(
            label, fontSize = 13.sp, fontWeight = FontWeight.Medium,
            color = TextPrimary, modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(value) },
            colors = RadioButtonDefaults.colors(
                selectedColor = GreenPrimary,
                unselectedColor = Color(0xFFBBBBBB)
            ),
            modifier = Modifier.size(20.dp)
        )
    }
}

// ─── Price Line ───────────────────────────────────────────────────────────────
@Composable
private fun PriceLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text(value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

// ─── Confirm Dialog ───────────────────────────────────────────────────────────
@Composable
private fun ConfirmCheckoutDialog(
    total: Double,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = !isLoading)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(GreenSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingCart, null, tint = GreenPrimary, modifier = Modifier.size(26.dp))
                }
                Text("Konfirmasi Checkout", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text(
                    "Total pembayaran ${formatRupiah(total)}. Lanjutkan checkout?",
                    fontSize = 13.sp, color = TextSecondary, lineHeight = 20.sp
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFDDDDDD)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) { Text("Batal", fontWeight = FontWeight.SemiBold) }
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                        else Text("Checkout", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}