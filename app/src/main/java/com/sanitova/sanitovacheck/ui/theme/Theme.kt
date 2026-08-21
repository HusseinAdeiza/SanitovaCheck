package com.sanitova.sanitovacheck.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = Cream,
    primaryContainer = TerracottaLight,
    onPrimaryContainer = TerracottaDark,

    secondary = Olive,
    onSecondary = Cream,
    secondaryContainer = OliveLight,
    onSecondaryContainer = OliveDark,

    tertiary = Gold,
    onTertiary = Charcoal,
    tertiaryContainer = GoldLight,
    onTertiaryContainer = Charcoal,

    background = Sand,
    onBackground = Charcoal,

    surface = Cream,
    onSurface = Charcoal,
    surfaceVariant = SandDark,
    onSurfaceVariant = CharcoalLight,

    error = ErrorRed,
    onError = Cream
)

private val DarkColors = darkColorScheme(
    primary = TerracottaLight,
    onPrimary = TerracottaDark,
    primaryContainer = TerracottaDark,
    onPrimaryContainer = TerracottaLight,

    secondary = OliveLight,
    onSecondary = OliveDark,
    secondaryContainer = OliveDark,
    onSecondaryContainer = OliveLight,

    tertiary = GoldLight,
    onTertiary = Charcoal,

    background = Charcoal,
    onBackground = Sand,

    surface = Color(0xFF2E241E),
    onSurface = Sand,

    error = Color(0xFFE8897A),
    onError = Charcoal
)

@Composable
fun SanitovaCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Material You) intentionally OFF by default — we want
    // our own brand palette to show consistently, not the user's wallpaper colors.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
