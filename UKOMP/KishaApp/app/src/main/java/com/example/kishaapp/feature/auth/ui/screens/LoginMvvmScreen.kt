package com.example.kishaapp.ui.screens.auth

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.kishaapp.R
import com.example.kishaapp.utils.isValidEmail
import com.example.kishaapp.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException

// ─── Brand colors ───────────────────────────────────────────────────────────
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

private const val FALLBACK_WEB_CLIENT_ID =
    "849684754606-mnniilus036rejs0jnb66kgvldtb0qhj.apps.googleusercontent.com"

// ─── Main Screen ────────────────────────────────────────────────────────────
@Composable
fun LoginMvvmScreen(
    authViewModel: AuthViewModel,
    onSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit,
    onNavigateToEmailVerification: () -> Unit
) {
    val state by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isGoogleSignInLaunching by remember { mutableStateOf(false) }

    val trimmedEmail = email.trim()
    val isEmailValid = isValidEmail(trimmedEmail)
    val showEmailError = trimmedEmail.isNotBlank() && !isEmailValid

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            isGoogleSignInLaunching = false
            authViewModel.setErrorMessage("Login Google dibatalkan")
            return@rememberLauncherForActivityResult
        }
        try {
            isGoogleSignInLaunching = false
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) authViewModel.loginWithGoogle(idToken)
            else authViewModel.setErrorMessage("Gagal mendapatkan token Google.")
        } catch (e: ApiException) {
            isGoogleSignInLaunching = false
            val msg = when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Login Google dibatalkan"
                GoogleSignInStatusCodes.NETWORK_ERROR -> "Koneksi internet bermasalah"
                GoogleSignInStatusCodes.DEVELOPER_ERROR ->
                    "Konfigurasi Google Sign-In tidak valid. Tambahkan SHA-1 & SHA-256 di Firebase Console."
                else -> "Login Google gagal (${e.statusCode})"
            }
            authViewModel.setErrorMessage(msg)
        }
    }

    if (state.isLoggedIn) onSuccess()
    LaunchedEffect(state.requiresEmailVerification) {
        if (state.requiresEmailVerification) onNavigateToEmailVerification()
    }

    // ── Root background ──────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Decorative blurred blobs
        BlobDecoration()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // ── App icon ─────────────────────────────────────────────────
            AppIcon()

            Spacer(Modifier.height(20.dp))

            // ── Welcome text ─────────────────────────────────────────────
            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = (-0.5).sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Sign in to continue your organic journey",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(32.dp))

            // ── Card form ────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Email field
                    FieldLabel("Email Address")
                    OrganicTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "hello@organicmarket.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        isError = showEmailError,
                        errorText = if (showEmailError) "Format email belum valid" else null
                    )

                    // Password field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FieldLabel("Password")
                        TextButton(
                            onClick = onNavigateToForgot,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "Forgot password?",
                                color = GreenPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    OrganicTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        passwordVisible = passwordVisible,
                        onTogglePassword = { passwordVisible = !passwordVisible }
                    )

                    Spacer(Modifier.height(4.dp))

                    // Sign In button
                    SignInButton(
                        isLoading = state.isLoading && !isGoogleSignInLaunching,
                        onClick = {
                            if (!isEmailValid || password.length < 6) return@SignInButton
                            authViewModel.login(trimmedEmail, password)
                        }
                    )

                    // Divider
                    OrDivider()

                    // Google button
                    GoogleSignInButton(
                        isLoading = isGoogleSignInLaunching,
                        enabled = !state.isLoading && !isGoogleSignInLaunching,
                        onClick = {
                            val webClientIdRes = context.resources.getIdentifier(
                                "default_web_client_id", "string", context.packageName
                            )
                            val webClientId = when {
                                webClientIdRes != 0 && context.getString(webClientIdRes).isNotBlank() ->
                                    context.getString(webClientIdRes)
                                else -> FALLBACK_WEB_CLIENT_ID
                            }
                            if (webClientId.isBlank()) {
                                authViewModel.setErrorMessage("Google Sign-In belum dikonfigurasi.")
                                return@GoogleSignInButton
                            }
                            isGoogleSignInLaunching = true
                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(webClientId).requestEmail().build()
                            val client = GoogleSignIn.getClient(context, gso)
                            client.signOut()
                            googleSignInLauncher.launch(client.signInIntent)
                        }
                    )

                    // Error / Success messages
                    AnimatedVisibility(
                        visible = !state.errorMessage.isNullOrBlank(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        MessageBanner(
                            text = state.errorMessage ?: "",
                            isError = true
                        )
                    }

                    AnimatedVisibility(
                        visible = !state.successMessage.isNullOrBlank(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        MessageBanner(
                            text = state.successMessage ?: "",
                            isError = false
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Register link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't have an account?",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        "Sign Up",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Sub-composables ─────────────────────────────────────────────────────────

@Composable
private fun BlobDecoration() {
    Box(Modifier.fillMaxSize()) {
        // Top-right blob
        Box(
            Modifier
                .size(220.dp)
                .offset(x = 120.dp, y = (-60).dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GreenContainer.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
                .blur(40.dp)
        )
        // Bottom-left blob
        Box(
            Modifier
                .size(200.dp)
                .offset(x = (-80).dp, y = 80.dp)
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
private fun AppIcon() {
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
            .size(88.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
                )
            )
            .shadow(12.dp, RoundedCornerShape(28.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Plant icon using text emoji (replace with actual icon/drawable if available)
        Text(text = "🌱", fontSize = 36.sp)
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
private fun OrganicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null
) {
    val visualTransformation = if (isPassword && !passwordVisible)
        PasswordVisualTransformation() else VisualTransformation.None

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
        visualTransformation = visualTransformation,
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
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
        supportingText = {
            if (isError && !errorText.isNullOrBlank()) {
                Text(
                    text = errorText,
                    color = ErrorColor,
                    fontSize = 11.sp
                )
            }
        }
    )
}

@Composable
private fun SignInButton(isLoading: Boolean, onClick: () -> Unit) {
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
                Text(
                    "Sign In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DividerColor,
            thickness = 1.dp
        )
        Text(
            "OR CONTINUE WITH",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DividerColor,
            thickness = 1.dp
        )
    }
}

@Composable
private fun GoogleSignInButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFFF5F5F5),
            contentColor = TextPrimary
        ),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = GreenPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google "G" colored text as stand-in (replace with actual Google logo drawable)
                Text(
                    "G",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF4285F4)
                )
                Text(
                    "Google Account",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
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
        Text(
            if (isError) "⚠" else "✓",
            fontSize = 14.sp,
            color = textColor
        )
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}