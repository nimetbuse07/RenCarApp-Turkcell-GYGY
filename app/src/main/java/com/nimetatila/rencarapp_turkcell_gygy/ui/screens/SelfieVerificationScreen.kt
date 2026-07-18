package com.nimetatila.rencarapp_turkcell_gygy.ui.screens

import android.widget.Toast
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarColors
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.LicenseViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.LicenseIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.LicenseEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.LicenseState

@Composable
fun SelfieVerificationScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LicenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LicenseEffect.NavigateToDashboard -> {
                    Toast.makeText(context, "Ehliyet ve selfie başarıyla yüklendi", Toast.LENGTH_SHORT).show()
                    onContinueClick()
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(state.uploadError) {
        state.uploadError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onIntent(LicenseIntent.ClearError)
        }
    }

    SelfieVerificationScreenContent(
        state = state,
        onIntent = { viewModel.onIntent(it) },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfieVerificationScreenContent(
    state: LicenseState,
    onIntent: (LicenseIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0D0D0D)
    val spacing = LocalRencarSpacing.current
    val extColors = LocalRencarColors.current
    val context = LocalContext.current

    // Information box description with bold warnings
    val infoText = rememberInfoText()

    var showSourceSheet by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    fun createImageUri(): Uri {
        val directory = File(context.cacheDir, "camera_photos")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "captured_photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    val selfieLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onIntent(LicenseIntent.SelfieImageChanged(uri))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUri?.let { uri ->
                onIntent(LicenseIntent.SelfieImageChanged(uri))
            }
        }
        showSourceSheet = false
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri()
            tempUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Kamera izni verilmedi. Fotoğraf çekmek için kameraya izin vermelisiniz.", Toast.LENGTH_LONG).show()
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
            Row(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
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
                Spacer(modifier = Modifier.width(spacing.md))
                Column {
                    Text(
                        text = "Selfie doğrulama",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kiralamadan önce tek seferlik",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            // Step Progress Bar (Step 2 Active)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Step 1: Completed / Checked
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = RenCarAppIcons.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.xxs))
                    Text(
                        text = "Ehliyet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Active Line
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.5.dp)
                        .padding(horizontal = spacing.xs)
                        .background(MaterialTheme.colorScheme.primary)
                )

                // Step 2: Active (Selfie)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Text("2", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(spacing.xxs))
                    Text(
                        text = "Selfie",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Inactive Line
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .padding(horizontal = spacing.xs)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                )

                // Step 3: Inactive
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .background(Color.Transparent)
                    ) {
                        Text("3", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(spacing.xxs))
                    Text("Onay", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(spacing.xl))

            // Section 2: Selfie çekimi
            Text(
                text = "Selfie çekimi",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = spacing.xs)
            )

            // Kesikli Kenarlıklı Kamera Buton Alanı
            val outlineColor = MaterialTheme.colorScheme.outlineVariant
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        if (isDark) Color(0xFF111827).copy(alpha = 0.3f) else Color(0xFFF9FAFB).copy(alpha = 0.5f)
                    )
                    .drawWithContent {
                        val stroke = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                        )
                        drawContent()
                        drawRoundRect(
                            color = outlineColor,
                            style = stroke,
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                    .clickable { showSourceSheet = true }
            ) {
                if (state.selfieBitmap != null) {
                    Image(
                        bitmap = state.selfieBitmap.asImageBitmap(),
                        contentDescription = "Selfie önizleme",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = RenCarAppIcons.Camera,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Text(
                            text = "Fotoğrafını çek veya yükle",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // Info Warning Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(spacing.md)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = RenCarAppIcons.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.xs))
                    Text(
                        text = infoText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // CTA Button: "Gönder ve Tamamla"
            Button(
                onClick = { onIntent(LicenseIntent.UploadLicense) },
                enabled = !state.isUploading && state.selfieBitmap != null,
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
                if (state.isUploading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Text(
                        text = "Gönder ve Tamamla",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))
        }

        if (showSourceSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSourceSheet = false },
                sheetState = rememberModalBottomSheetState(),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = "Selfie Fotoğrafı",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val hasCameraPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                if (hasCameraPermission) {
                                    val uri = createImageUri()
                                    tempUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                            }
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = RenCarAppIcons.Camera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Kamerayı Aç ve Fotoğraf Çek",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selfieLauncher.launch("image/*")
                                showSourceSheet = false
                            }
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = RenCarAppIcons.Upload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Galeriden Fotoğraf Seç",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberInfoText() = remember {
    buildAnnotatedString {
        append("Yüzünün ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))) {
            append("net göründüğünden")
        }
        append(" ve ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))) {
            append("iyi ışık")
        }
        append(" aldığından emin ol.")
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun SelfieVerificationScreenLightPreview() {
    RenCarAppTheme(darkTheme = false) {
        SelfieVerificationScreenContent(
            state = LicenseState(),
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun SelfieVerificationScreenDarkPreview() {
    RenCarAppTheme(darkTheme = true) {
        SelfieVerificationScreenContent(
            state = LicenseState(),
            onIntent = {},
            onBackClick = {}
        )
    }
}
