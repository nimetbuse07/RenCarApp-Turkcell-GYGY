package com.nimetatila.rencarapp_turkcell_gygy.ui.features


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.otpDigitTextStyle
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.VerifyEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.VerifyIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.VerifyState
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.VerifyViewModel
import kotlinx.coroutines.delay

@Composable
fun VerifyScreen(
    phoneNumber: String,
    onBackClick: () -> Unit,
    onChangeNumberClick: () -> Unit,
    onVerifySuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VerifyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is VerifyEffect.NavigateToDashboard -> {
                    onVerifySuccess()
                }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onIntent(VerifyIntent.ClearError)
        }
    }

    VerifyScreenContent(
        state = state,
        phoneNumber = phoneNumber,
        onIntent = { viewModel.onIntent(it) },
        onBackClick = onBackClick,
        onChangeNumberClick = onChangeNumberClick,
        modifier = modifier
    )
}

@Composable
fun VerifyScreenContent(
    state: VerifyState,
    phoneNumber: String,
    onIntent: (VerifyIntent) -> Unit,
    onBackClick: () -> Unit,
    onChangeNumberClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0D0D0D)
    val spacing = LocalRencarSpacing.current

    var isTextFieldFocused by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(42) } // Starts at 0:42 as in design

    val focusRequester = remember { FocusRequester() }

    // Countdown Timer LaunchedEffect
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    // Format phone number to "+90 532 000 00 00" structure
    val formattedPhone = remember(phoneNumber) {
        val cleanNumber = phoneNumber
            .removePrefix("+90")
            .removePrefix("90")
            .filter { it.isDigit() }

        if (cleanNumber.length == 10) {
            "+90 ${cleanNumber.substring(0, 3)} ${cleanNumber.substring(3, 6)} ${cleanNumber.substring(6, 8)} ${cleanNumber.substring(8, 10)}"
        } else {
            if (phoneNumber.startsWith("+")) {
                phoneNumber
            } else {
                "+$phoneNumber"
            }
        }
    }

    // Annotated subtitle text to bold the phone number
    val subtitleText = remember(formattedPhone) {
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(formattedPhone)
            }
            append(" numarasına gönderdiğimiz 6 haneli kodu gir.")
        }
    }

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

            Spacer(modifier = Modifier.height(spacing.xl))

            // Verification Icon Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
            ) {
                Icon(
                    imageVector = RenCarAppIcons.PhoneVerify,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            // Title
            Text(
                text = "Telefonunu doğrula",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            // Subtitle with bold phone number
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.xxl))

            // Hidden BasicTextField to receive keyboard input
            BasicTextField(
                value = state.code,
                onValueChange = { input ->
                    val cleanInput = input.filter { it.isDigit() }
                    if (cleanInput.length <= 6) {
                        onIntent(VerifyIntent.CodeChanged(cleanInput))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { isTextFieldFocused = it.isFocused }
                    .size(1.dp) // Keep it hidden but focusable
                    .background(Color.Transparent),
                decorationBox = { }
            )

            // 6 OTP Digit Boxes Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { focusRequester.requestFocus() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 6) {
                    val isFocusedBox = isTextFieldFocused && state.code.length == i
                    val char = if (state.code.length > i) state.code[i].toString() else ""

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(52.dp) // OtpBoxSize = 52.dp
                            .border(
                                width = if (isFocusedBox) 2.dp else 1.dp,
                                color = if (isFocusedBox) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = MaterialTheme.shapes.small
                            )
                            .clip(MaterialTheme.shapes.small)
                            .background(
                                if (isDark) MaterialTheme.colorScheme.surface else Color.White
                            )
                    ) {
                        if (isFocusedBox) {
                            // Custom cursor vertical line
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(24.dp)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        } else {
                            Text(
                                text = char,
                                style = otpDigitTextStyle,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            // Resend timer row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = RenCarAppIcons.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    text = if (timeLeft > 0) {
                        "Kodu tekrar gönder · 0:${timeLeft.toString().padStart(2, '0')}"
                    } else {
                        "Kodu tekrar gönder"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (timeLeft > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(enabled = timeLeft == 0) {
                        timeLeft = 42
                        onIntent(VerifyIntent.CodeChanged(""))
                    }
                )
            }

            Spacer(modifier = Modifier.height(spacing.xxl))

            // Primary Button: "Doğrula ve Devam Et"
            Button(
                onClick = { onIntent(VerifyIntent.Verify(phoneNumber)) },
                enabled = state.code.length == 6 && !state.isLoading,
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
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Doğrula ve Devam Et",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            // Footer Change Phone Number redirection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Numara yanlış mı? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Değiştir",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onChangeNumberClick() }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun VerifyScreenLightPreview() {
    RenCarAppTheme(darkTheme = false) {
        VerifyScreenContent(
            state = VerifyState(),
            phoneNumber = "5320000000",
            onIntent = {},
            onBackClick = {},
            onChangeNumberClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun VerifyScreenDarkPreview() {
    RenCarAppTheme(darkTheme = true) {
        VerifyScreenContent(
            state = VerifyState(),
            phoneNumber = "5320000000",
            onIntent = {},
            onBackClick = {},
            onChangeNumberClick = {}
        )
    }
}
