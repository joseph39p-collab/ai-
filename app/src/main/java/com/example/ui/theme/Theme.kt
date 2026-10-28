package com.example.ui.theme

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
    primary = CineRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF450A0A),
    onPrimaryContainer = Color(0xFFFFDADA),
    secondary = CineGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3D2E00),
    onSecondaryContainer = Color(0xFFFFE088),
    tertiary = CineDolbyCyan,
    onTertiary = Color.Black,
    background = DarkBg,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder,
    outlineVariant = Color(0xFF383A54)
)

private val LightColorScheme = lightColorScheme(
    primary = CineRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEAEA),
    onPrimaryContainer = Color(0xFF5C0000),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = CineDolbyBlue,
    onTertiary = Color.White,
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder,
    outlineVariant = Color(0xFFD1D5DB)
)

@Composable
fun CineFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent cinematic look
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Use dark theme by default for immersive cinema experience
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
