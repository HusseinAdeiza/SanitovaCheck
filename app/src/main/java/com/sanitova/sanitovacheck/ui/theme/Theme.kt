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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = OceanTeal,
    onPrimary = PureWhite,
    primaryContainer = OceanTealLight,
    onPrimaryContainer = OceanTealDark,

    secondary = Aqua,
    onSecondary = PureWhite,
    secondaryContainer = AquaLight,
    onSecondaryContainer = AquaDark,

    tertiary = CyanWave,
    onTertiary = DeepWater,
    tertiaryContainer = CyanWaveLight,
    onTertiaryContainer = DeepWater,

    background = Foam,
    onBackground = DeepWater,

    surface = PureWhite,
    onSurface = DeepWater,
    surfaceVariant = WaterSurface,
    onSurfaceVariant = DeepWaterLight,

    error = ErrorRed,
    onError = PureWhite
)

private val DarkColors = darkColorScheme(
    primary = OceanTealLight,
    onPrimary = OceanTealDark,
    primaryContainer = OceanTealDark,
    onPrimaryContainer = OceanTealLight,

    secondary = AquaLight,
    onSecondary = AquaDark,
    secondaryContainer = AquaDark,
    onSecondaryContainer = AquaLight,

    tertiary = CyanWaveLight,
    onTertiary = DeepWater,

    background = DeepWater,
    onBackground = Foam,

    surface = WaterSurfaceDark,
    onSurface = Foam,

    error = Color(0xFFE8897A),
    onError = DeepWater
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
            // Edge-to-edge is enabled via enableEdgeToEdge() in MainActivity; the status bar
            // is transparent by default on SDK 35+, so we only need to control icon contrast here.
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
