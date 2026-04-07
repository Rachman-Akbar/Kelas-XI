package com.example.kishaapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import com.example.kishaapp.viewmodel.AuthViewModel

// ─── Brand colors ────────────────────────────────────────────────────────────
private val GreenPrimary   = Color(0xFF2E7D32)
private val GreenLight     = Color(0xFF81C784)
private val GreenSurface   = Color(0xFFE8F5E9)
private val BgColor        = Color(0xFFF1F8F1)
private val CardBg         = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1B2E1C)
private val TextSecondary  = Color(0xFF5A7A5C)
private val DividerColor   = Color(0xFFEEEEEE)
private val LogoutRed      = Color(0xFFD32F2F)

// ─── Main Screen ─────────────────────────────────────────────────────────────
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToSellerDashboard: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCategories: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsState()
    var newPassword by remember { mutableStateOf("") }
    val profile = authState.userProfile

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        if (profile == null) {
            // ── Not logged in ─────────────────────────────────────────────
            NotLoggedInContent(onNavigateToLogin)
        } else {
            // ── Logged in ─────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(20.dp))

                // ── Profile header ────────────────────────────────────────
                ProfileHeader(
                    name = profile.name,
                    email = profile.email,
                    role = profile.role
                )

                Spacer(Modifier.height(20.dp))

                // ── My Purchases ──────────────────────────────────────────
                MyPurchasesCard(onViewHistory = onNavigateToOrders)

                Spacer(Modifier.height(16.dp))

                // ── Menu items ────────────────────────────────────────────
                MenuCard(
                    isSeller = profile.role == "seller" || profile.role == "admin",
                    onSellerDashboard = onNavigateToSellerDashboard,
                    onOpenCart = onNavigateToCart,
                    onOpenOrders = onNavigateToOrders,
                    onOpenCategories = onNavigateToCategories
                )

                Spacer(Modifier.height(16.dp))

                // ── Google account password setup ─────────────────────────
                if (authState.isGoogleOnlyAccount) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        GooglePasswordCard(
                            newPassword = newPassword,
                            onPasswordChange = { newPassword = it },
                            isLoading = authState.isLoading,
                            onSubmit = {
                                authViewModel.setupPasswordForGoogleAccount(newPassword.trim())
                                if (!authState.isLoading) newPassword = ""
                            }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // ── Messages ──────────────────────────────────────────────
                if (!authState.errorMessage.isNullOrBlank()) {
                    MessageBanner(
                        text = authState.errorMessage ?: "",
                        isError = true,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (!authState.successMessage.isNullOrBlank()) {
                    MessageBanner(
                        text = authState.successMessage ?: "",
                        isError = false,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // ── Logout button ─────────────────────────────────────────
                LogoutButton(onClick = { authViewModel.logout() })

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// ─── Profile Header ───────────────────────────────────────────────────────────
@Composable
private fun ProfileHeader(name: String, email: String, role: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar
        Box(modifier = Modifier.size(76.dp)) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(GreenSurface)
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name.take(1).uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenPrimary
                )
            }
            // Verified badge
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.BottomEnd)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(GreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            // Member badge
            val badgeLabel = when (role.lowercase()) {
                "seller" -> "🏪  Seller"
                "admin"  -> "⚙️  Admin"
                else     -> "🏅  Member Level: Gold"
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GreenSurface
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = badgeLabel,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GreenPrimary
                    )
                }
            }
        }
    }
}

// ─── My Purchases Card ────────────────────────────────────────────────────────
@Composable
private fun MyPurchasesCard(onViewHistory: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Purchases",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "View History",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary,
                    modifier = Modifier.clickable(onClick = onViewHistory)
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PurchaseItem(icon = "💳", label = "To Pay")
                PurchaseItem(icon = "🚚", label = "To Ship")
                PurchaseItem(icon = "📦", label = "To Receive")
                PurchaseItem(icon = "⭐", label = "Reviews")
            }
        }
    }
}

@Composable
private fun PurchaseItem(icon: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(GreenSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 22.sp)
        }
        Text(
            label,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─── Menu Card ────────────────────────────────────────────────────────────────
@Composable
private fun MenuCard(
    isSeller: Boolean,
    onSellerDashboard: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenCategories: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            if (isSeller) {
                MenuItem(
                    icon = Icons.Default.Store,
                    label = "My Selling",
                    onClick = onSellerDashboard
                )
                MenuDivider()
            }
            MenuItem(icon = Icons.Default.ShoppingCart, label = "My Cart", onClick = onOpenCart)
            MenuDivider()
            MenuItem(icon = Icons.Default.Receipt, label = "Order History", onClick = onOpenOrders)
            MenuDivider()
            MenuItem(icon = Icons.Default.Category, label = "Categories", onClick = onOpenCategories)
            MenuDivider()
            MenuItem(icon = Icons.Default.HelpOutline, label = "Help Center")
            MenuDivider()
            MenuItem(icon = Icons.Default.Settings, label = "Settings")
        }
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp, end = 16.dp),
        color = DividerColor,
        thickness = 0.8.dp
    )
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(GreenSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── Google Password Card ────────────────────────────────────────────────────
@Composable
private fun GooglePasswordCard(
    newPassword: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Aktifkan Login Email/Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                "Akun Anda login via Google. Buat password agar bisa login dengan email juga.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
            OutlinedTextField(
                value = newPassword,
                onValueChange = onPasswordChange,
                label = { Text("Password baru") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = Color(0xFFCCE5CC),
                    focusedContainerColor = GreenSurface,
                    cursorColor = GreenPrimary
                )
            )
            Button(
                onClick = onSubmit,
                enabled = !isLoading && newPassword.length >= 6,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Buat Password", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── Logout Button ────────────────────────────────────────────────────────────
@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = LogoutRed,
                modifier = Modifier.size(20.dp)
            )
            Text(
                "Logout",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = LogoutRed
            )
        }
    }
}

// ─── Not Logged In ────────────────────────────────────────────────────────────
@Composable
private fun NotLoggedInContent(onNavigateToLogin: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(GreenSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(44.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "Kamu belum login",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Login untuk melihat profil dan riwayat pembelianmu.",
            fontSize = 14.sp,
            color = TextSecondary,
            lineHeight = 20.sp
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Text("Login Sekarang", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// ─── Message Banner ───────────────────────────────────────────────────────────
@Composable
private fun MessageBanner(text: String, isError: Boolean, modifier: Modifier = Modifier) {
    val bgColor    = if (isError) Color(0xFFFFEBEE) else GreenSurface
    val textColor  = if (isError) Color(0xFFD32F2F) else GreenPrimary
    val borderColor = textColor.copy(alpha = 0.3f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(if (isError) "⚠" else "✓", fontSize = 14.sp, color = textColor)
        Text(text, color = textColor, fontSize = 13.sp, lineHeight = 18.sp,
            modifier = Modifier.weight(1f))
    }
}