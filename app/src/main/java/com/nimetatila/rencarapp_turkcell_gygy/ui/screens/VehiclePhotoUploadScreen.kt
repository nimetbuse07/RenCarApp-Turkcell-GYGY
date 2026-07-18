package com.nimetatila.rencarapp_turkcell_gygy.ui.screens

import android.net.Uri
import android.widget.Toast
import java.io.File
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ReservationIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehiclePhotoUploadScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit,
    viewModel: ReservationViewModel
) {
    val spacing = LocalRencarSpacing.current
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(vehicleId) {
        viewModel.onIntent(ReservationIntent.LoadVehicle(vehicleId))
    }

    val vehicle = uiState.vehicle
    val isLoadingVehicle = uiState.isLoadingVehicle
    val photos = uiState.photos
    val remainingPhotos = photos.values.count { it == null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = RenCarAppIcons.ArrowBack,
                            contentDescription = "Geri",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (isLoadingVehicle) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = spacing.md),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Araç durumu",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Başlamadan önce 4 yönü çek",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(spacing.md))

                    Text(
                        text = vehicle?.let { "${it.brand} ${it.model} - ${it.plate}" } ?: "",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (remainingPhotos == 0) "Tüm fotoğraflar çekildi" else "Kalan fotoğraf sayısı:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${4 - remainingPhotos} / 4 çekildi",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.lg))

                    // 2x2 grid of photo slots
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            PhotoSlotCard(
                                label = "Ön",
                                directionKey = "ön",
                                uri = photos["ön"],
                                onPhotoSelected = { uri ->
                                    viewModel.onIntent(ReservationIntent.PhotoSelected("ön", uri))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            PhotoSlotCard(
                                label = "Arka",
                                directionKey = "arka",
                                uri = photos["arka"],
                                onPhotoSelected = { uri ->
                                    viewModel.onIntent(ReservationIntent.PhotoSelected("arka", uri))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            PhotoSlotCard(
                                label = "Sol",
                                directionKey = "sol",
                                uri = photos["sol"],
                                onPhotoSelected = { uri ->
                                    viewModel.onIntent(ReservationIntent.PhotoSelected("sol", uri))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            PhotoSlotCard(
                                label = "Sağ",
                                directionKey = "sağ",
                                uri = photos["sağ"],
                                onPhotoSelected = { uri ->
                                    viewModel.onIntent(ReservationIntent.PhotoSelected("sağ", uri))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(bottom = spacing.xl)) {
                    // Warning section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = RenCarAppIcons.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = "Hasarları net çek - teslim sonrası anlaşmazlığı önler.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.md))

                    // Next step button
                    val isButtonEnabled = remainingPhotos == 0
                    Button(
                        onClick = onContinueClick,
                        enabled = isButtonEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isButtonEnabled) "Rezervasyon Onayına Geç" else "Kiralama Öncesi · $remainingPhotos foto kaldı",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSlotCard(
    label: String,
    directionKey: String,
    uri: Uri?,
    onPhotoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalRencarSpacing.current
    val isCaptured = uri != null
    val context = LocalContext.current

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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { resultUri ->
        if (resultUri != null) {
            onPhotoSelected(resultUri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempUri?.let { uri ->
                onPhotoSelected(uri)
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
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (isCaptured) {
                    if (isSystemInDarkTheme()) Color(0xFF0F2E22) else Color(0xFFE8F8F0)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                }
            )
            .border(
                width = 1.dp,
                color = if (isCaptured) {
                    Color(0xFF22C55E)
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { showSourceSheet = true }
    ) {
        if (isCaptured) {
            // Checked State
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSystemInDarkTheme()) Color(0xFF1E3F30) else Color(0xFFD1FAE5))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF10B981)
                )
            }

            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E))
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RenCarAppIcons.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }

            Icon(
                imageVector = RenCarAppIcons.Car,
                contentDescription = null,
                tint = if (isSystemInDarkTheme()) Color(0xFF1B4D3E) else Color(0xFFA7F3D0),
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.Center)
            )
        } else {
            // Empty upload state
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.Start)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RenCarAppIcons.Camera,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xxs))

                Text(
                    text = "Fotoğraf çek",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))
            }
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
                        text = "$label Fotoğrafı",
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
                                launcher.launch("image/*")
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
