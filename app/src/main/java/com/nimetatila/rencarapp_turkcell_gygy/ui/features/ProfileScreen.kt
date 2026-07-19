package com.nimetatila.rencarapp_turkcell_gygy.ui.features

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme

@Composable
fun ProfileScreen(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogoutClick: () -> Unit,
    userFullName: String,
    userPhone: String,
    userRole: String,
    licenseStatus: String,
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalRencarSpacing.current
    val context = LocalContext.current
    var isSettingsExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Çıkış Yap",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Hesabınızdan çıkış yapmak istediğinize emin misiniz?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text(
                        text = "Çıkış Yap",
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = "İptal",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.md)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        Spacer(modifier = Modifier.height(spacing.xs))

        // 1. User Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(size = 72.dp)

            Spacer(modifier = Modifier.width(spacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userFullName.ifEmpty { "Kullanıcı" },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userPhone.ifEmpty { "Telefon Numarası Yok" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Edit Profile Button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF3F5F9))
                    .clickable {
                        Toast.makeText(context, "Profil düzenleme yakında!", Toast.LENGTH_SHORT).show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = RenCarAppIcons.Edit,
                    contentDescription = "Profili Düzenle",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. Driving License Verification Card
        val shieldBgColor = when (licenseStatus) {
            "APPROVED" -> if (isDarkTheme) Color(0xFF064E3B).copy(alpha = 0.3f) else Color(0xFFE6F4EA)
            "UNDER_REVIEW" -> if (isDarkTheme) Color(0xFF78350F).copy(alpha = 0.3f) else Color(0xFFFEF3C7)
            "REJECTED" -> if (isDarkTheme) Color(0xFF7F1D1D).copy(alpha = 0.3f) else Color(0xFFFDE8E8)
            else -> if (isDarkTheme) Color(0xFF374151).copy(alpha = 0.3f) else Color(0xFFF3F4F6)
        }
        val shieldIconColor = when (licenseStatus) {
            "APPROVED" -> Color(0xFF10B981)
            "UNDER_REVIEW" -> Color(0xFFD97706)
            "REJECTED" -> Color(0xFFEF4444)
            else -> Color(0xFF9CA3AF)
        }
        val statusTitle = when (licenseStatus) {
            "APPROVED" -> "Ehliyet doğrulandı"
            "UNDER_REVIEW" -> "Ehliyet onay bekliyor"
            "REJECTED" -> "Ehliyet reddedildi"
            else -> "Ehliyet yüklenmedi"
        }
        val statusSubtitle = when (licenseStatus) {
            "APPROVED" -> "B sınıfı · geçerli"
            "UNDER_REVIEW" -> "Belgeleriniz inceleniyor"
            "REJECTED" -> "Yeniden yüklemek için dokunun"
            else -> "Kiralamak için ehliyet yükleyin"
        }
        val badgeText = when (licenseStatus) {
            "APPROVED" -> "Onaylı"
            "UNDER_REVIEW" -> "Beklemede"
            "REJECTED" -> "Reddedildi"
            else -> "Yükle"
        }
        val badgeBgColor = when (licenseStatus) {
            "APPROVED" -> if (isDarkTheme) Color(0xFF064E3B).copy(alpha = 0.5f) else Color(0xFFDCFCE7)
            "UNDER_REVIEW" -> if (isDarkTheme) Color(0xFF78350F).copy(alpha = 0.5f) else Color(0xFFFEF3C7)
            "REJECTED" -> if (isDarkTheme) Color(0xFF7F1D1D).copy(alpha = 0.5f) else Color(0xFFFDE8E8)
            else -> if (isDarkTheme) Color(0xFF374151).copy(alpha = 0.5f) else Color(0xFFE5E7EB)
        }
        val badgeTextColor = when (licenseStatus) {
            "APPROVED" -> Color(0xFF10B981)
            "UNDER_REVIEW" -> Color(0xFFD97706)
            "REJECTED" -> Color(0xFFEF4444)
            else -> Color(0xFF6B7280)
        }

        val isClickable = licenseStatus == "NOT_SUBMITTED" || licenseStatus == "REJECTED"

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isClickable) {
                        Modifier.clickable { onLicenseClick() }
                    } else Modifier
                ),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shield status icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(shieldBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RenCarAppIcons.Shield,
                        contentDescription = null,
                        tint = shieldIconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(spacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = statusTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = statusSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status pill badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(badgeBgColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor
                        )
                    )
                }
            }
        }

        // 3. Menu Options Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Item 1: Ödeme Yöntemleri
                MenuItemRow(
                    icon = RenCarAppIcons.CreditCard,
                    title = "Ödeme yöntemleri",
                    onClick = {
                        Toast.makeText(context, "Ödeme yöntemleri tıklanıldı.", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = spacing.md),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    thickness = 0.5.dp
                )

                // Item 2: Ayarlar (Normal row that expands on click)
                MenuItemRow(
                    icon = RenCarAppIcons.Settings,
                    title = "Ayarlar",
                    onClick = { isSettingsExpanded = !isSettingsExpanded },
                    showChevron = true,
                    isExpanded = isSettingsExpanded
                )

                if (isSettingsExpanded) {
                    // Under the settings row, nested item for Dark Theme toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeToggle() }
                            .padding(start = 54.dp, end = spacing.md, top = 8.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Karanlık mod",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onThemeToggle() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = spacing.md),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    thickness = 0.5.dp
                )

                // Item 3: Yardım & destek
                MenuItemRow(
                    icon = RenCarAppIcons.Help,
                    title = "Yardım & destek",
                    onClick = {
                        Toast.makeText(context, "Yardım & destek tıklanıldı.", Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = spacing.md),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                    thickness = 0.5.dp
                )

                // Item 4: Davet et
                MenuItemRow(
                    icon = RenCarAppIcons.Share,
                    title = "Davet et · ₺50 kazan",
                    onClick = {
                        Toast.makeText(context, "Davet et tıklanıldı.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // 4. Logout Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showLogoutDialog = true
                },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = RenCarAppIcons.Logout,
                    contentDescription = "Çıkış yap",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    text = "Çıkış yap",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFEF4444)
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))
    }
}

@Composable
fun MenuItemRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showSwitch: Boolean = false,
    isSwitchChecked: Boolean = false,
    showChevron: Boolean = true,
    isExpanded: Boolean = false
) {
    val spacing = LocalRencarSpacing.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = spacing.md, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(spacing.md))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        if (showSwitch) {
            Switch(
                checked = isSwitchChecked,
                onCheckedChange = { onClick() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        } else if (showChevron) {
            val rotationAngle = if (isExpanded) 90f else 0f
            Icon(
                imageVector = RenCarAppIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(rotationZ = rotationAngle)
            )
        }
    }
}

/**
 * Custom vector portrait drawn with Compose Canvas to match the cartoon-avatar design.
 */
@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val radius = w / 2f
        val center = Offset(w / 2f, h / 2f)

        // 1. Beautiful gradient background circle
        drawCircle(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(Color(0xFF6366F1), Color(0xFF3B82F6)),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            ),
            radius = radius,
            center = center
        )

        // Clip everything inside the boundary
        val clipPath = Path().apply {
            addOval(Rect(0f, 0f, w, h))
        }

        drawContext.canvas.save()
        drawContext.canvas.clipPath(clipPath)

        // 2. Shoulders (neutral white shape)
        drawOval(
            color = Color.White.copy(alpha = 0.9f),
            topLeft = Offset(w * 0.15f, h * 0.7f),
            size = Size(w * 0.7f, h * 0.5f)
        )

        // 3. Head (neutral white circle)
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = w * 0.2f,
            center = Offset(w / 2f, h * 0.42f)
        )

        drawContext.canvas.restore()
    }
}

@Preview(showBackground = true, name = "Light Profile")
@Composable
fun ProfileScreenLightPreview() {
    RenCarAppTheme(darkTheme = false) {
        ProfileScreen(
            isDarkTheme = false,
            onThemeToggle = {},
            onLogoutClick = {},
            userFullName = "Deniz Yılmaz",
            userPhone = "+90 532 000 00 00",
            userRole = "CUSTOMER",
            licenseStatus = "APPROVED",
            onLicenseClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Profile")
@Composable
fun ProfileScreenDarkPreview() {
    RenCarAppTheme(darkTheme = true) {
        ProfileScreen(
            isDarkTheme = true,
            onThemeToggle = {},
            onLogoutClick = {},
            userFullName = "Deniz Yılmaz",
            userPhone = "+90 532 000 00 00",
            userRole = "PENDING",
            licenseStatus = "NOT_SUBMITTED",
            onLicenseClick = {}
        )
    }
}
