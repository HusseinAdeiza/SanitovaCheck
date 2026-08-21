package com.sanitova.sanitovacheck.ui.theme

import androidx.compose.ui.graphics.Color

// ── Core palette: warm, professional earth tones ──

// Primary — Terracotta (warmth, action, brand identity)
val Terracotta = Color(0xFFC1622D)
val TerracottaDark = Color(0xFF8F441C)
val TerracottaLight = Color(0xFFE8A374)

// Secondary — Olive (trust, "safe/compliant" association)
val Olive = Color(0xFF6B7A4F)
val OliveDark = Color(0xFF4A5636)
val OliveLight = Color(0xFFA3B27F)

// Tertiary — Muted gold (accents, highlights)
val Gold = Color(0xFFC79A45)
val GoldLight = Color(0xFFE6C583)

// Neutrals — warm sand/cream rather than cold grey
val Sand = Color(0xFFFAF5EE)
val SandDark = Color(0xFFF0E6D8)
val Charcoal = Color(0xFF3A2E27)
val CharcoalLight = Color(0xFF6B5D53)
val Cream = Color(0xFFFFFDF9)

// ── Risk-level semantic colors (fit naturally into earth palette) ──
val RiskHigh = Color(0xFFB3452F)      // deep rust red
val RiskHighBg = Color(0xFFF7E5DF)
val RiskMedium = Color(0xFFC79A45)    // muted gold/amber
val RiskMediumBg = Color(0xFFF7EFDC)
val RiskLow = Color(0xFF6B7A4F)       // olive green
val RiskLowBg = Color(0xFFE9EEE0)

// Error (distinct from RiskHigh — used for real app errors, not risk levels)
val ErrorRed = Color(0xFFA23B2E)
