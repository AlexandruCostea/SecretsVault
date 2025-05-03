package com.alexcostea.secretsvault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SoftDarkBackground = Color(0xFF1A1A1A)       // Slightly lighter than pure black
val SoftDarkSurface = Color(0xFF2A2A2A)          // Used for cards, sheets
val VibrantOrange = Color(0xFFFF6F00)            // Bright, bold orange
val DeepOrange = Color(0xFFE65100)               // Rich, warm orange
val PaleOrange = Color(0xFFFFB74D)               // Subtle highlight
val LightText = Color(0xFFE6D3A3)               // General on-dark text
val MutedGray = Color(0xFF8A8A8A)               // Subtle text, borders
val ErrorRed = Color(0xFFCF6679)                 // Standard dark-mode error

private val OrangeDarkColorScheme = darkColorScheme(
    primary = VibrantOrange,
    onPrimary = Color.Black,

    secondary = DeepOrange,
    onSecondary = Color.Black,

    tertiary = PaleOrange,
    onTertiary = Color.Black,

    background = SoftDarkBackground,
    onBackground = LightText,

    surface = SoftDarkSurface,
    onSurface = LightText,

    error = ErrorRed,
    onError = Color.Black,

    primaryContainer = Color(0xFFFFA040),
    onPrimaryContainer = Color.Black,

    secondaryContainer = Color(0xFFAD4400),
    onSecondaryContainer = Color.White,

    tertiaryContainer = Color(0xFFFFCC80),
    onTertiaryContainer = Color.Black,

    surfaceVariant = Color(0xFF3A3A3A),
    onSurfaceVariant = MutedGray,

    outline = MutedGray,
    inverseOnSurface = SoftDarkSurface,
    inverseSurface = LightText,
    inversePrimary = Color(0xFFFFB74D)
)

@Composable
fun SecretsVaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OrangeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
