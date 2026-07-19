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
import androidx.compose.runtime.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.contract.LicenseIntent
import com.nimetatila.rencarapp_turkcell_gygy.contract.LicenseState
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarColors
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.LicenseViewModel

@Composable
fun LicenseVerificationScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LicenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(LicenseIntent.GetStatus)
    }

    LaunchedEffect(state.statusError) {
        state.statusError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onIntent(LicenseIntent.ClearError)
        }
    }

    LaunchedEffect(state.uploadError) {
        state.uploadError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onIntent(LicenseIntent.ClearError)
        }
    }

    LicenseVerificationScreenContent(
        state = state,
        onIntent = { viewModel.onIntent(it) },
        onBackClick = onBackClick,
        onContinueClick = onContinueClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseVerificationScreenContent(
    state: LicenseState,
    onIntent: (LicenseIntent) -> Unit,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background == Color(0xFF0D0D0D)
    val spacing = LocalRencarSpacing.current
    val context = LocalContext.current

    var activePhotoSlot by remember { mutableStateOf<String?>(null) } // "front", "back", or null
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

    val frontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onIntent(LicenseIntent.FrontImageChanged(uri))
        }
    }

    val backLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onIntent(LicenseIntent.BackImageChanged(uri))
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUri?.let { uri ->
                if (activePhotoSlot == "front") {
                    onIntent(LicenseIntent.FrontImageChanged(uri))
                } else if (activePhotoSlot == "back") {
                    onIntent(LicenseIntent.BackImageChanged(uri))
                }
            }
        }
        activePhotoSlot = null
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
                        text = "Ehliyet doğrulama",
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

            if (state.isStatusLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                val status = state.statusResponse?.status ?: "NOT_SUBMITTED"
                if (status == "APPROVED") {
                    // Already Approved UI
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE6F4EA))
                        ) {
                            Icon(
                                imageVector = RenCarAppIcons.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.lg))
                        Text(
                            text = "Ehliyetiniz Onaylandı",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Text(
                            text = "Harika! Ehliyetiniz başarıyla doğrulandı ve artık araç kiralayabilirsiniz.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = spacing.md),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(spacing.xl))
                        Button(
                            onClick = onContinueClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Devam Et", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                } else if (status == "UNDER_REVIEW") {
                    // Under Review UI
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7))
                        ) {
                            Icon(
                                imageVector = RenCarAppIcons.Shield,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.lg))
                        Text(
                            text = "Belgeleriniz İnceleniyor",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Text(
                            text = "Ehliyet bilgileriniz ekibimiz tarafından kontrol ediliyor. Bu işlem genellikle birkaç dakika sürer.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = spacing.md),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(spacing.xl))
                        Button(
                            onClick = onContinueClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            Text("Ana Sayfaya Git", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                } else {
                    // NOT_SUBMITTED or REJECTED: Show upload flow
                    UploadFlowContent(
                        isDark = isDark,
                        spacing = spacing,
                        rejectReason = state.statusResponse?.rejectReason,
                        frontBitmap = state.frontBitmap,
                        backBitmap = state.backBitmap,
                        onFrontClick = { activePhotoSlot = "front" },
                        onBackClick = { activePhotoSlot = "back" },
                        onUploadClick = onContinueClick,
                        isLoading = false
                    )
                }
            }
        }

        if (activePhotoSlot != null) {
            ModalBottomSheet(
                onDismissRequest = { activePhotoSlot = null },
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
                        text = if (activePhotoSlot == "front") "Ehliyet Ön Yüzü" else "Ehliyet Arka Yüzü",
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
                                if (activePhotoSlot == "front") {
                                    frontLauncher.launch("image/*")
                                } else {
                                    backLauncher.launch("image/*")
                                }
                                activePhotoSlot = null
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
fun UploadFlowContent(
    isDark: Boolean,
    spacing: com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RencarSpacing,
    rejectReason: String?,
    frontBitmap: Bitmap?,
    backBitmap: Bitmap?,
    onFrontClick: () -> Unit,
    onBackClick: () -> Unit,
    onUploadClick: () -> Unit,
    isLoading: Boolean
) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant
    val infoText = rememberInfoText()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Step Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Step 1: Active
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text("1", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(spacing.xxs))
                Text("Ehliyet", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }

            // Line
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .padding(horizontal = spacing.xs)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            )

            // Step 2: Inactive
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .background(Color.Transparent)
                ) {
                    Text("2", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(spacing.xxs))
                Text("Selfie", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Line
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

        // Red rejection alert box if rejectReason is present
        if (!rejectReason.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.md)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0xFFFDE8E8))
                    .border(1.dp, Color(0xFFF8B4B4), MaterialTheme.shapes.medium)
                    .padding(spacing.md)
            ) {
                Column {
                    Text(
                        text = "Ehliyetiniz Reddedildi",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9B1C1C)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Gerekçe: $rejectReason",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC81E1E)
                    )
                }
            }
        }

        // Section 1: Ehliyet ön yüz
        Text(
            text = "Ehliyet ön yüz",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = spacing.xs)
        )

        // Dotted card container for front page
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
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
                .clickable { onFrontClick() }
        ) {
            if (frontBitmap != null) {
                Image(
                    bitmap = frontBitmap.asImageBitmap(),
                    contentDescription = "Ön yüz önizleme",
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
                        text = "Ön yüzü çek veya yükle",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // Section 2: Ehliyet arka yüz
        Text(
            text = "Ehliyet arka yüz",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = spacing.xs)
        )

        // Dotted card container for back page
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
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
                .clickable { onBackClick() }
        ) {
            if (backBitmap != null) {
                Image(
                    bitmap = backBitmap.asImageBitmap(),
                    contentDescription = "Arka yüz önizleme",
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
                        text = "Arka yüzü çek veya yükle",
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

        // CTA Button: "Devam Et"
        Button(
            onClick = onUploadClick,
            enabled = !isLoading && frontBitmap != null && backBitmap != null,
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
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = "Devam Et",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))
    }
}

@Composable
private fun rememberInfoText() = remember {
    buildAnnotatedString {
        append("Bilgilerin güvenle saklanır. Doğrulama genelde ")
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))) {
            append("birkaç dakika")
        }
        append(" sürer.")
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun LicenseVerificationScreenLightPreview() {
    RenCarAppTheme(darkTheme = false) {
        LicenseVerificationScreenContent(
            state = LicenseState(),
            onIntent = {},
            onBackClick = {},
            onContinueClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun LicenseVerificationScreenDarkPreview() {
    RenCarAppTheme(darkTheme = true) {
        LicenseVerificationScreenContent(
            state = LicenseState(),
            onIntent = {},
            onBackClick = {},
            onContinueClick = {}
        )
    }
}
