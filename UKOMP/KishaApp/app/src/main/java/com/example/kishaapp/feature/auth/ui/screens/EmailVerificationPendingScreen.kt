package com.example.kishaapp.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.kishaapp.viewmodel.AuthViewModel

// ─── Brand colors ────────────────────────────────────────────────────────────
private val GreenPrimary   = Color(0xFF2E7D32)
private val GreenLight     = Color(0xFFCFD3D8)
private val GreenSurface   = Color(0xFFF3F4F6)
private val GreenContainer = Color(0xFFDDE1E6)
private val BgColor        = Color(0xFFF6F7F9)
private val CardBg         = Color(0xFFFFFFFF)
private val TextPrimary    = Color(0xFF1F2328)
private val TextSecondary  = Color(0xFF5F6368)
private val DividerColor   = Color(0xFFE1E4E8)
private val ErrorColor     = Color(0xFFD32F2F)

// ─── Main Screen ─────────────────────────────────────────────────────────────
@Composable
fun EmailVerificationPendingScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onEnterApp: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val canContinue = state.canEnterApp || state.isLoggedIn

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Blob decoration
        VerifyBlobDecoration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // ── Illustration ─────────────────────────────────────────────
            EmailIllustration()

            Spacer(Modifier.height(28.dp))

            // ── Title ─────────────────────────────────────────────────────
            Text(
                text = "Verify Your Email",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "We've sent a verification link to\n${state.pendingVerificationEmail ?: "your email address"}.\nPlease check your inbox and click the link.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(36.dp))

            OutlinedButton(
                onClick = { authViewModel.checkPendingEmailVerification() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("Saya sudah verifikasi, cek sekarang")
            }

            Spacer(Modifier.height(12.dp))

            // ── Continue button (enabled only after verification) ────────
            ContinueButton(
                isLoading = state.isLoading,
                canContinue = canContinue,
                onClick = {
                    authViewModel.loginFromPendingVerification()
                    onEnterApp()
                }
            )

            if (!canContinue) {
                Text(
                    text = "Continue akan aktif setelah email terverifikasi.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Resend ────────────────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "Didn't receive the code?",
                    fontSize = 13.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
                TextButton(
                    onClick = { authViewModel.resendPendingEmailVerification() },
                    enabled = !state.isLoading
                ) {
                    Text(
                        "Resend verification",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Error / info message ──────────────────────────────────────
            AnimatedVisibility(
                visible = !state.errorMessage.isNullOrBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                MessageBanner(text = state.errorMessage ?: "", isError = true)
            }

            AnimatedVisibility(
                visible = !state.successMessage.isNullOrBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                MessageBanner(text = state.successMessage ?: "", isError = false)
            }

            Spacer(Modifier.height(24.dp))

            // ── Need Help card ────────────────────────────────────────────
            NeedHelpCard()

            Spacer(Modifier.height(32.dp))

            // ── Logout ────────────────────────────────────────────────────
            TextButton(
                onClick = {
                    authViewModel.cancelPendingVerification()
                    onBack()
                },
                enabled = !state.isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = TextSecondary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Logout option",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }

    }
}

// ─── Sub-composables ─────────────────────────────────────────────────────────

@Composable
private fun VerifyBlobDecoration() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .size(180.dp)
                .offset(x = 100.dp, y = (-40).dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GreenContainer.copy(alpha = 0.45f), Color.Transparent)
                    )
                )
                .blur(40.dp)
        )
        Box(
            Modifier
                .size(160.dp)
                .offset(x = (-60).dp, y = 50.dp)
                .align(Alignment.BottomStart)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GreenLight.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
                .blur(48.dp)
        )
    }
}

@Composable
private fun EmailIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 140.dp)
            .graphicsLayer { translationY = offsetY }
    ) {
        // White card background
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Green circle with email icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GreenSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // Badge dot (green circle with checkmark) — bottom right
        Box(
            modifier = Modifier
                .size(38.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 6.dp, y = 6.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(GreenPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ContinueButton(isLoading: Boolean, canContinue: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp),
        enabled = canContinue && !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (canContinue) {
                        Brush.horizontalGradient(listOf(Color(0xFF388E3C), Color(0xFF1B5E20)))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xFFBDBDBD), Color(0xFF9E9E9E)))
                    },
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Memeriksa verifikasi...", fontSize = 14.sp, color = Color.White)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text("→", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NeedHelpCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF1B5E20)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "NEED HELP?",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Text(
                    "Our team is available 24/7",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            // Chat button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text("💬", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun VerificationSuccessDialog(onGetStarted: () -> Unit) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        val scale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "dialogScale"
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Checkmark icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(GreenSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    "Verification Successful",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    "Your email has been verified. You can now start exploring the marketplace.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(4.dp))

                // Get Started button
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF388E3C), Color(0xFF1B5E20))
                                ),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Get Started",
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

@Composable
private fun MessageBanner(text: String, isError: Boolean) {
    val bgColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val textColor = if (isError) ErrorColor else GreenPrimary
    val borderColor = if (isError) ErrorColor.copy(alpha = 0.3f) else GreenPrimary.copy(alpha = 0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(if (isError) "⚠" else "✓", fontSize = 14.sp, color = textColor)
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}