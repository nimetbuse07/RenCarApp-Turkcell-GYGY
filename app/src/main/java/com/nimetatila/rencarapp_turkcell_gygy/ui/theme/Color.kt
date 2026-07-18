package com.nimetatila.rencarapp_turkcell_gygy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf

// ── Brand Blue ──────────────────────────────────────────────
val Blue50  = Color(0xFFEFF6FF)
val Blue100 = Color(0xFFDBEAFE)
val Blue500 = Color(0xFF3B82F6)
val Blue600 = Color(0xFF2563EB)   // Primary / CTA
val Blue700 = Color(0xFF1D4ED8)
val Blue800 = Color(0xFF1E40AF)

// ── Neutrals ─────────────────────────────────────────────────
val Neutral0   = Color(0xFFFFFFFF)
val Neutral50  = Color(0xFFF9FAFB)
val Neutral100 = Color(0xFFF2F4F7)  // Light background
val Neutral200 = Color(0xFFE5E7EB)  // Divider / border
val Neutral300 = Color(0xFFD1D5DB)
val Neutral400 = Color(0xFF9CA3AF)  // Placeholder
val Neutral500 = Color(0xFF6B7280)  // Secondary text
val Neutral700 = Color(0xFF374151)
val Neutral900 = Color(0xFF111827)  // Dark surface
val Neutral950 = Color(0xFF0D0D0D)  // Dark background

// ── Semantic ─────────────────────────────────────────────────
val Success500 = Color(0xFF22C55E)  // "Yüklendi" badge, checkmark
val Success100 = Color(0xFFDCFCE7)
val Error500   = Color(0xFFEF4444)  // "Kiralamayı Bitir" button
val Error100   = Color(0xFFFEE2E2)
val Warning500 = Color(0xFFF59E0B)

// ── Vehicle Category ─────────────────────────────────────────
val CategoryEkonomik = Color(0xFFFF6B35)   // Turuncu marker
val CategoryKonfor   = Color(0xFF7C3AED)   // Mor marker
val CategorySUV      = Color(0xFFEAB308)   // Sarı marker
val CategoryYesil    = Color(0xFF10B981)   // Yeşil marker (₺26)

// ── Map Marker ───────────────────────────────────────────────
val MapMarkerSelected   = Color(0xFF2563EB)
val MapMarkerUnselected = Color(0xFFFFFFFF)

val RencarLightColorScheme = lightColorScheme(
    primary              = Blue600,
    onPrimary            = Neutral0,
    primaryContainer     = Blue50,
    onPrimaryContainer   = Blue700,
    secondary            = Neutral500,
    onSecondary          = Neutral0,
    background           = Neutral100,
    onBackground         = Neutral900,
    surface              = Neutral0,
    onSurface            = Neutral900,
    onSurfaceVariant     = Neutral500,
    outline              = Neutral200,
    outlineVariant       = Neutral300,
    error                = Error500,
    onError              = Neutral0,
    surfaceVariant       = Neutral50,
)

val RencarDarkColorScheme = darkColorScheme(
    primary              = Blue500,
    onPrimary            = Neutral0,
    primaryContainer     = Blue800,
    onPrimaryContainer   = Blue100,
    secondary            = Neutral400,
    onSecondary          = Neutral950,
    background           = Neutral950,
    onBackground         = Neutral0,
    surface              = Neutral900,
    onSurface            = Neutral0,
    onSurfaceVariant     = Neutral400,
    outline              = Neutral700,
    outlineVariant       = Neutral700,
    error                = Error500,
    onError              = Neutral0,
    surfaceVariant       = Color(0xFF1F2937),
)

data class RencarExtendedColors(
    val categoryEkonomik: Color,
    val categoryKonfor:   Color,
    val categorySuv:      Color,
    val categoryYesil:    Color,
    val success:          Color,
    val successContainer: Color,
    val warning:          Color,
    val errorStrong:      Color,     // "Kiralamayı Bitir" kırmızısı
)

val LocalRencarColors = staticCompositionLocalOf {
    RencarExtendedColors(
        categoryEkonomik = CategoryEkonomik,
        categoryKonfor   = CategoryKonfor,
        categorySuv      = CategorySUV,
        categoryYesil    = CategoryYesil,
        success          = Success500,
        successContainer = Success100,
        warning          = Warning500,
        errorStrong      = Error500,
    )
}

val rencarLightExtended = RencarExtendedColors(
    categoryEkonomik = CategoryEkonomik,
    categoryKonfor   = CategoryKonfor,
    categorySuv      = CategorySUV,
    categoryYesil    = CategoryYesil,
    success          = Success500,
    successContainer = Success100,
    warning          = Warning500,
    errorStrong      = Error500,
)

val rencarDarkExtended = rencarLightExtended