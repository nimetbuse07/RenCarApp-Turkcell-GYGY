package com.nimetatila.rencarapp_turkcell_gygy.ui.features

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.RegisterEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.RegisterIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.RegisterState
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.NavigateToLicenseVerification -> {
                    onRegisterSuccess()
                }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onIntent(RegisterIntent.ClearError)
        }
    }

    RegisterScreenContent(
        state = state,
        onIntent = { viewModel.onIntent(it) },
        onBackClick = onBackClick,
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreenContent(
    state: RegisterState,
    onIntent: (RegisterIntent) -> Unit,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0D0D0D)
    val spacing = LocalRencarSpacing.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.md)
                .navigationBarsPadding()
                .statusBarsPadding()
        ) {
            // Header / Back Button Row
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = RenCarAppIcons.ArrowBack,
                        contentDescription = "Geri Dön",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Text(
                    text = "Kayıt Ol",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(spacing.xs))

                // Subtitle
                Text(
                    text = "Bilgilerini gir ve dakikalar içinde yola çıkmaya hazırlan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(spacing.xl))

                // Ad Soyad Input
                Text(
                    text = "Ad Soyad",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = spacing.xs)
                )
                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = { onIntent(RegisterIntent.FullNameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ahmet Yılmaz") },
                    shape = MaterialTheme.shapes.small,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // E-posta Input
                Text(
                    text = "E-posta",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = spacing.xs)
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = { onIntent(RegisterIntent.EmailChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("ahmet.yilmaz@example.com") },
                    shape = MaterialTheme.shapes.small,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Telefon Numarası Input
                Text(
                    text = "Telefon numarası",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = spacing.xs)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(90.dp)
                            .fillMaxHeight()
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = MaterialTheme.shapes.small
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        Text(
                            text = "TR +90",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.width(spacing.xs))

                    OutlinedTextField(
                        value = state.phoneNumber,
                        onValueChange = { input ->
                            val cleanInput = input.filter { it.isDigit() }
                            if (cleanInput.length <= 10) {
                                onIntent(RegisterIntent.PhoneChanged(cleanInput))
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        placeholder = {
                            Text(
                                "532 000 00 00",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        visualTransformation = PhoneVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(spacing.md))

                // Şifre Input
                Text(
                    text = "Şifre",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = spacing.xs)
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onIntent(RegisterIntent.PasswordChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Şifre123!") },
                    shape = MaterialTheme.shapes.small,
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { onIntent(RegisterIntent.TogglePasswordVisibility) }) {
                            Icon(
                                imageVector = if (state.passwordVisible) RenCarAppIcons.VisibilityOff else RenCarAppIcons.Visibility,
                                contentDescription = if (state.passwordVisible) "Şifreyi gizle" else "Şifreyi göster"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Referans Kodu Input
                Text(
                    text = "Referans Kodu (Opsiyonel)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = spacing.xs)
                )
                OutlinedTextField(
                    value = state.referralCode,
                    onValueChange = { onIntent(RegisterIntent.ReferralCodeChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("REN-K7M2XQ") },
                    shape = MaterialTheme.shapes.small,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                    )
                )

                Spacer(modifier = Modifier.height(spacing.xxl))

                // Kayıt Ol Butonu
                Button(
                    onClick = {
                        onIntent(RegisterIntent.Register)
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = if (isDark) 16.dp else 4.dp,
                            shape = MaterialTheme.shapes.extraLarge,
                            clip = false,
                            ambientColor = MaterialTheme.colorScheme.primary,
                            spotColor = MaterialTheme.colorScheme.primary
                        ),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Kayıt Ol ve Devam Et",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.xl))
            }

            // Footer Giriş Yap Yönlendirmesi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.xl),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Zaten hesabın var mı? ",
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

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun RegisterScreenLightPreview() {
    RenCarAppTheme(darkTheme = false) {
        RegisterScreenContent(
            state = RegisterState(),
            onIntent = {},
            onBackClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun RegisterScreenDarkPreview() {
    RenCarAppTheme(darkTheme = true) {
        RegisterScreenContent(
            state = RegisterState(),
            onIntent = {},
            onBackClick = {},
            onLoginClick = {}
        )
    }
}
