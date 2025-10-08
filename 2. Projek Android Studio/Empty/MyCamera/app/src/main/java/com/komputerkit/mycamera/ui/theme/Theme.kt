package com.komputerkit.mycamera.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Surface,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Surface,
    
    secondary = SecondaryOrange,
    onSecondary = Surface,
    secondaryContainer = SecondaryOrangeDark,
    onSecondaryContainer = Surface,
    
    tertiary = AccentTeal,
    onTertiary = Surface,
    tertiaryContainer = AccentPurple,
    onTertiaryContainer = Surface,
    
    background = BackgroundDark,
    onBackground = Surface,
    surface = SurfaceDark,
    onSurface = Surface,
    surfaceVariant = NeutralMedium,
    onSurfaceVariant = NeutralLighter,
    
    outline = NeutralLight,
    outlineVariant = NeutralMedium,
    
    error = ErrorRed,
    onError = Surface,
    errorContainer = Color(0xFF5D1A1A),
    onErrorContainer = Surface
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Surface,
    primaryContainer = PrimaryBlueSoft,
    onPrimaryContainer = PrimaryBlueDark,
    
    secondary = SecondaryOrange,
    onSecondary = Surface,
    secondaryContainer = SecondaryOrangeSoft,
    onSecondaryContainer = SecondaryOrangeDark,
    
    tertiary = AccentTeal,
    onTertiary = Surface,
    tertiaryContainer = Color(0xFFE0F2F1),
    onTertiaryContainer = Color(0xFF00695C),
    
    background = Background,
    onBackground = NeutralDark,
    surface = Surface,
    onSurface = NeutralDark,
    surfaceVariant = NeutralLightest,
    onSurfaceVariant = NeutralMedium,
    
    outline = NeutralLight,
    outlineVariant = NeutralLighter,
    
    error = ErrorRed,
    onError = Surface,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C)
)

@Composable
fun ComposeCameraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
