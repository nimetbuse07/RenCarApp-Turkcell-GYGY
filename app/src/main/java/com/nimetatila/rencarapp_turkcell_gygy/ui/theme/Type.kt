package com.nimetatila.rencarapp_turkcell_gygy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Font Family - Fallback to system-ui (SansSerif) to avoid missing local ttf resource errors
val InterFamily = FontFamily.SansSerif

val RencarTypography = Typography(
    // ── Display ─────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize   = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.5).sp,
    ),

    // ── Headline ─────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.3).sp,
    ),

    headlineMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.2).sp,
    ),

    // ── Title ────────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp,
    ),

    titleMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),

    titleSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),

    // ── Body ─────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),

    bodyMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),

    bodySmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.sp,
    ),

    // ── Label ────────────────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),

    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.1.sp,
    ),

    labelSmall = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)

// ── Custom Text Styles (Non-M3 scale) ─────────────────────────

val timerTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.ExtraBold,
    fontSize   = 48.sp,
    lineHeight = 56.sp,
    letterSpacing = (-1).sp,
)

val priceLargeTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.3).sp,
)

val priceUnitTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Normal,
    fontSize   = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
)

val walletBalanceTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.5).sp,
)

val otpDigitTextStyle = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Bold,
    fontSize   = 24.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.sp,
)