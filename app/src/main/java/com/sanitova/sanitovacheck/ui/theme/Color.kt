package com.sanitova.sanitovacheck.ui.theme

import androidx.compose.ui.graphics.Color

// ── Core palette: water / ocean / WASH theme ──

// Primary — Deep Ocean Teal (water, trust, health)
val OceanTeal = Color(0xFF0D8A8A)
val OceanTealDark = Color(0xFF065F5F)
val OceanTealLight = Color(0xFF4ECDC4)

// Secondary — Fresh Aqua (cleanliness, clarity)
val Aqua = Color(0xFF1A9CB0)
val AquaDark = Color(0xFF0F6B7A)
val AquaLight = Color(0xFF7FDBE8)

// Tertiary — Cyan Wave (accents, highlights)
val CyanWave = Color(0xFF00BCD4)
val CyanWaveLight = Color(0xFF80DEEA)

// Neutrals — clean water / foam / sky tones instead of grey
val Foam = Color(0xFFF0F8FF)       // very light blue-white
val FoamDark = Color(0xFFD6ECF5)   // slightly deeper for cards/elevation
val DeepWater = Color(0xFF1A3A4A)  // dark navy for text
val DeepWaterLight = Color(0xFF4A6B7A) // medium for secondary text
val PureWhite = Color(0xFFFFFFFF)

// Surface / background variants
val WaterSurface = Color(0xFFE8F4F8)   // light water tint for surfaces
val WaterSurfaceDark = Color(0xFF132A35) // dark mode surface

// ── Risk-level semantic colors (adapted to water palette) ──
val RiskHigh = Color(0xFFB3452F)      // deep rust red — unchanged, works well
val RiskHighBg = Color(0xFFF7E5DF)
val RiskMedium = Color(0xFFD4A017)    // amber/warning — slightly adjusted
val RiskMediumBg = Color(0xFFF7EFDC)
val RiskLow = Color(0xFF2E8B57)       // sea green — fits water theme
val RiskLowBg = Color(0xFFE0F0E8)

// Error (distinct from RiskHigh — used for real app errors)
val ErrorRed = Color(0xFFA23B2E)
