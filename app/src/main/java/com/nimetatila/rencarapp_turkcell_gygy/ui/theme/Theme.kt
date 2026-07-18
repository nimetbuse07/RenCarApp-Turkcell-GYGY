package com.nimetatila.rencarapp_turkcell_gygy.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ── Shape System ─────────────────────────────────────────────
val RencarShapes = Shapes(
    // Chip, badge, küçük buton
    extraSmall = RoundedCornerShape(6.dp),

    // Input field, küçük kart
    small      = RoundedCornerShape(10.dp),

    // Standart kart (araç detay, kiralama geçmiş item)
    medium     = RoundedCornerShape(16.dp),

    // Bottom sheet, büyük modal kart
    large      = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),

    // Pill buton ("Hemen Başla", "Kod Gönder", "Kilidi Aç")
    extraLarge = RoundedCornerShape(100.dp),
)

// ── Spacing System ────────────────────────────────────────────
@Immutable
data class RencarSpacing(
    val xxxs: Dp =  2.dp,
    val xxs:  Dp =  4.dp,
    val xs:   Dp =  8.dp,
    val sm:   Dp = 12.dp,
    val md:   Dp = 16.dp,   // Standart yatay padding
    val lg:   Dp = 20.dp,
    val xl:   Dp = 24.dp,
    val xxl:  Dp = 32.dp,
    val xxxl: Dp = 48.dp,
)

val LocalRencarSpacing = staticCompositionLocalOf { RencarSpacing() }

// ── Global Dimens ─────────────────────────────────────────────
object RencarDimens {
    // Buton
    val ButtonHeight = 56.dp
    val ButtonHeightSmall = 44.dp

    // OTP
    val OtpBoxSize = 52.dp
    val OtpBoxSpacing = 8.dp

    // Kart
    val CardRadius = 16.dp
    val CardImageHeight = 180.dp

    // Bottom sheet handle
    val SheetHandleWidth = 40.dp
    val SheetHandleHeight = 4.dp

    // Nav bar
    val NavBarHeight = 64.dp

    // Avatar
    val AvatarSizeLarge = 56.dp
    val AvatarSizeSmall = 40.dp

    // Map marker bubble
    val MapMarkerHeight = 32.dp

    // Vehicle category dot
    val CategoryDotSize = 8.dp
}

// ── RencarTheme Entry Point ───────────────────────────────────
@Composable
fun RenCarAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) RencarDarkColorScheme else RencarLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            // Adjust status bar and navigation bar icon colors
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalRencarColors provides (if (darkTheme) rencarDarkExtended else rencarLightExtended),
        LocalRencarSpacing provides RencarSpacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = RencarTypography,
            shapes      = RencarShapes,
            content     = content,
        )
    }
}

// Compatibility wrapper for components referencing the old theme name
@Composable
fun RenCarAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    RenCarAppTheme(darkTheme = darkTheme, content = content)
}