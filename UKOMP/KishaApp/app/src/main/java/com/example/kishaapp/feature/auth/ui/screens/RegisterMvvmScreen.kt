package com.example.kishaapp.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.kishaapp.data.model.UserRoles
import com.example.kishaapp.utils.isValidEmail
import com.example.kishaapp.viewmodel.AuthViewModel

// ─── Brand colors (same as Login) ───────────────────────────────────────────
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
fun RegisterMvvmScreen(
    authViewModel: AuthViewModel,
    onNavigateToEmailVerification: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf(UserRoles.CUSTOMER) }

    val trimmedEmail = email.trim()
    val isEmailValid = isValidEmail(trimmedEmail)
    val showEmailError = trimmedEmail.isNotBlank() && !isEmailValid
    val passwordMismatch = confirmPassword.isNotEmpty() && password != confirmPassword

    LaunchedEffect(state.requiresEmailVerification) {
        if (state.requiresEmailVerification) onNavigateToEmailVerification()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Decorative blobs
        RegisterBlobDecoration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(52.dp))

            // ── Icon ─────────────────────────────────────────────────────
            RegisterIcon()

            Spacer(Modifier.height(20.dp))

            // ── Header ───────────────────────────────────────────────────
            Text(
                text = "Join Emerald Market",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Start your journey toward sustainable living\nand fresh organic produce today.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Card ─────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Full Name
                    FieldLabel("Full Name")
                    RegisterTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "John Doe",
                        leadingIcon = Icons.Default.Person,
                        keyboardType = KeyboardType.Text
                    )

                    // Email
                    FieldLabel("Email Address")
                    RegisterTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "example@email.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        isError = showEmailError,
                        errorText = if (showEmailError) "Format email belum valid" else null
                    )

                    // Role selector
                    FieldLabel("I want to join as")
                    RoleSelector(
                        selected = role,
                        onSelect = { role = it }
                    )

                    // Password
                    FieldLabel("Password")
                    RegisterTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )

                    // Confirm Password
                    FieldLabel("Confirm")
                    RegisterTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.LockReset,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        passwordVisible = confirmPasswordVisible,
                        onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible },
                        isError = passwordMismatch,
                        errorText = if (passwordMismatch) "Password tidak cocok" else null
                    )

                    Spacer(Modifier.height(4.dp))

                    // Create Account button
                    CreateAccountButton(
                        isLoading = state.isLoading,
                        onClick = {
                            if (name.isBlank() || !isEmailValid ||
                                password.length < 6 || password != confirmPassword
                            ) return@CreateAccountButton
                            authViewModel.register(name, trimmedEmail, password, role)
                        }
                    )

                    // Messages
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
                }
            }

            Spacer(Modifier.height(24.dp))

            // Login link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Already have an account?",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToLogin) {
                    Text(
                        "Log In",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Terms
            Text(
                text = "By joining, you agree to our Terms of Service,\nPrivacy Policy and Community Guidelines.",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Sub-composables ─────────────────────────────────────────────────────────

@Composable
private fun RegisterBlobDecoration() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .size(200.dp)
                .offset(x = 100.dp, y = (-50).dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GreenContainer.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
                .blur(40.dp)
        )
        Box(
            Modifier
                .size(180.dp)
                .offset(x = (-70).dp, y = 60.dp)
                .align(Alignment.BottomStart)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GreenLight.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
                .blur(50.dp)
        )
    }
}

@Composable
private fun RegisterIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(CircleShape)
            .background(GreenSurface),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        letterSpacing = 0.2.sp
    )
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = TextSecondary.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isError) ErrorColor else TextSecondary.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { onTogglePassword?.invoke() }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = if (isError) ErrorColor else DividerColor,
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
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = ErrorColor,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
    }
}

@Composable
private fun RoleSelector(
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RoleChip(
            label = "Customer",
            icon = "🛒",
            isSelected = selected == UserRoles.CUSTOMER,
            onClick = { onSelect(UserRoles.CUSTOMER) },
            modifier = Modifier.weight(1f)
        )
        RoleChip(
            label = "Seller",
            icon = "🏪",
            isSelected = selected == UserRoles.SELLER,
            onClick = { onSelect(UserRoles.SELLER) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RoleChip(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) GreenSurface else Color(0xFFF5F5F5)
    val borderColor = if (isSelected) GreenPrimary else DividerColor
    val textColor = if (isSelected) GreenPrimary else TextSecondary
    val borderWidth = if (isSelected) 2.dp else 1.dp

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = bgColor,
            contentColor = textColor
        ),
        border = BorderStroke(borderWidth, borderColor),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Text(icon, fontSize = 16.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

@Composable
private fun CreateAccountButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp),
        enabled = !isLoading
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
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Create Account",
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