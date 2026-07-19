package com.nimetatila.rencarapp_turkcell_gygy.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.Blue50
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.Blue800
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme

@Composable
fun SplashScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    // Background glow color based on the design system
    val glowColor = if (isDarkTheme) Blue800.copy(alpha = 0.15f) else Blue50.copy(alpha = 0.8f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Radial Gradient for premium background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(glowColor, Color.Transparent),
                        radius = 1200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Spacer to preserve vertical spacing in the SpaceBetween layout
            Spacer(modifier = Modifier.height(56.dp))

            // Logo and App Info Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Logo Container using RencarShapes.medium (16.dp)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(
                            elevation = if (isDarkTheme) 20.dp else 4.dp,
                            shape = MaterialTheme.shapes.medium,
                            clip = false,
                            ambientColor = MaterialTheme.colorScheme.primary,
                            spotColor = MaterialTheme.colorScheme.primary
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.medium
                        )
                ) {
                    Icon(
                        imageVector = RenCarAppIcons.Car,
                        contentDescription = "RenCar Logo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // App Title using RencarTypography.displayLarge
                Text(
                    text = "Rencar",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // App Subtitle using RencarTypography.bodyLarge
                Text(
                    text = "Yakındaki aracı bul,\ndakikalar içinde yola çık.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Section (Indicator, Button, Footer)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Page Indicator dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Active dot
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Inactive dots
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Primary Action Button using RencarShapes.extraLarge (pill)
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Hemen Başla",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer login redirection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Zaten hesabım var · ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Giriş yap",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun WelcomeScreenLightPreview() {
    RenCarAppTheme(darkTheme = false) {
        SplashScreen(
            onRegisterClick = {},
            onLoginClick = {},
            isDarkTheme = false
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun WelcomeScreenDarkPreview() {
    RenCarAppTheme(darkTheme = true) {
        SplashScreen(
            onRegisterClick = {},
            onLoginClick = {},
            isDarkTheme = true
        )
    }
}
