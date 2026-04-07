package com.example.kishaapp.ui.screens.auth

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.kishaapp.utils.isValidEmail
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

private fun openEmailApp(context: Context): Boolean {
    return try {
        val gmailIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_EMAIL)
            `package` = "com.google.android.gm"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(gmailIntent)
        true
    } catch (_: ActivityNotFoundException) {
        try {
            val emailAppIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_EMAIL)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(emailAppIntent)
            true
        } catch (_: ActivityNotFoundException) {
            try {
                val browserFallback = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://mail.google.com")
                ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                context.startActivity(browserFallback)
                true
            } catch (_: Exception) { false }
        }
    }
}

// ─── Main Screen ─────────────────────────────────────────────────────────────
@Composable
fun ForgotPasswordMvvmScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var hasAutoOpenedEmailApp by remember { mutableStateOf(false) }
    val trimmedEmail = email.trim()
    val isEmailValid = isValidEmail(trimmedEmail)
    val showEmailError = trimmedEmail.isNotBlank() && !isEmailValid
    val emailSent = !state.successMessage.isNullOrBlank()

    LaunchedEffect(state.successMessage) {
        if (!state.successMessage.isNullOrBlank() && !hasAutoOpenedEmailApp) {
            openEmailApp(context)
            hasAutoOpenedEmailApp = true
        } else if (state.successMessage.isNullOrBlank()) {
            hasAutoOpenedEmailApp = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Blobs
        ForgotBlobDecoration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // ── Illustration card ─────────────────────────────────────────
            IllustrationCard()

            Spacer(Modifier.height(28.dp))

            // ── Title ─────────────────────────────────────────────────────
            Text(
                text = "Forgot Password",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Enter your email address and we'll\nsend you a link to reset your\npassword.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(32.dp))

            // ── Form ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Email Address",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    letterSpacing = 0.2.sp
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        authViewModel.clearMessages()
                    },
                    placeholder = {
                        Text(
                            "hello@example.com",
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = if (showEmailError) ErrorColor else TextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = showEmailError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = if (showEmailError) ErrorColor else DividerColor,
                        errorBorderColor = ErrorColor,
                        focusedContainerColor = GreenSurface,
                        unfocusedContainerColor = Color(0xFFF9FBF9),
                        cursorColor = GreenPrimary,
                        errorCursorColor = ErrorColor,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        errorTextColor = TextPrimary
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )
                AnimatedVisibility(visible = showEmailError) {
                    Text(
                        "Format email belum valid",
                        color = ErrorColor,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Send button ───────────────────────────────────────────────
            SendResetButton(
                isLoading = state.isLoading,
                enabled = !state.isLoading && isEmailValid && !emailSent,
                onClick = { authViewModel.sendPasswordReset(trimmedEmail) }
            )

            Spacer(Modifier.height(16.dp))

            // ── Success state ─────────────────────────────────────────────
            AnimatedVisibility(
                visible = emailSent,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MessageBanner(text = state.successMessage ?: "", isError = false)
                    OutlinedButton(
                        onClick = { openEmailApp(context) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary),
                        border = BorderStroke(1.5.dp, GreenPrimary)
                    ) {
                        Text(
                            "Buka Aplikasi Email",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // ── Error state ───────────────────────────────────────────────
            AnimatedVisibility(
                visible = !state.errorMessage.isNullOrBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    MessageBanner(text = state.errorMessage ?: "", isError = true)
                    if (state.errorMessage?.contains("Google", ignoreCase = true) == true) {
                        Button(
                            onClick = onNavigateToLogin,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("Login dengan Google", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Back to Login ─────────────────────────────────────────────
            TextButton(onClick = onBack) {
                    Text("←", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        "Back to Login",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Dot indicators ────────────────────────────────────────────
            StepDots(activeIndex = 0)

            Spacer(Modifier.height(24.dp))

            // ── Footer ────────────────────────────────────────────────────
            Text(
                "© 2024 Organic Market. All rights reserved.",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))
        }
    }


// ─── Sub-composables ─────────────────────────────────────────────────────────

@Composable
private fun ForgotBlobDecoration() {
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
private fun IllustrationCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "angle"
    )

    Card(
        modifier = Modifier.size(width = 180.dp, height = 160.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lock icon in green circle
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(GreenSurface)
                    .graphicsLayer { rotationZ = angle },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Three dashes (step indicator inside card)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .width(28.dp).height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GreenLight)
                )
                Box(
                    Modifier
                        .width(36.dp).height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GreenPrimary)
                )
                Box(
                    Modifier
                        .width(28.dp).height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GreenLight)
                )
            }
        }
    }
}

@Composable
private fun SendResetButton(isLoading: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White.copy(alpha = 0.6f)
        ),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled)
                        Brush.horizontalGradient(listOf(Color(0xFF388E3C), Color(0xFF1B5E20)))
                    else
                        Brush.horizontalGradient(listOf(Color(0xFF81C784), Color(0xFF66BB6A))),
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
                    Text("Mengirim email reset...", fontSize = 14.sp, color = Color.White)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Send Reset Link",
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
private fun StepDots(activeIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == activeIndex) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == activeIndex) GreenPrimary
                        else GreenContainer.copy(alpha = 0.4f)
                    )
            )
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