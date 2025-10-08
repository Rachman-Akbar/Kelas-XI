package com.komputerkit.easyshop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = DarkText,
    primaryContainer = Teal40,
    onPrimaryContainer = SoftWhite,
    secondary = TealGrey80,
    onSecondary = DarkText,
    secondaryContainer = TealGrey40,
    onSecondaryContainer = SoftWhite,
    tertiary = Mint80,
    onTertiary = DarkText,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF616161),
    error = SoftRed,
    onError = SoftWhite
)

private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    onPrimary = SoftWhite,
    primaryContainer = Teal80,
    onPrimaryContainer = DarkText,
    secondary = TealGrey40,
    onSecondary = SoftWhite,
    secondaryContainer = TealGrey80,
    onSecondaryContainer = DarkText,
    tertiary = Mint40,
    onTertiary = SoftWhite,
    tertiaryContainer = Mint80,
    onTertiaryContainer = DarkText,
    background = LightBackground,
    onBackground = DarkText,
    surface = SoftWhite,
    onSurface = DarkText,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = MediumText,
    outline = LightText,
    outlineVariant = Color(0xFFE0E0E0),
    scrim = Color(0x80000000),
    inverseSurface = DarkText,
    inverseOnSurface = SoftWhite,
    inversePrimary = Teal80,
    error = SoftRed,
    onError = SoftWhite,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C)
)

@Composable
fun EasyShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color for consistent e-commerce branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}