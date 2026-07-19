package com.nimetatila.rencarapp_turkcell_gygy.ui.features

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.ui.intent.HistoryIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.VehicleRecordsViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRecordsScreen(
    viewModel: VehicleRecordsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val spacing = LocalRencarSpacing.current
    val isDark = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoadingRentals && state.rentals.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else if (state.rentalsError != null && state.rentals.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.xl),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.rentalsError ?: "Yükleme hatası",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(spacing.md))
                Button(
                    onClick = { viewModel.onIntent(HistoryIntent.Refresh) }
                ) {
                    Text(text = "Tekrar Dene")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = spacing.md),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(spacing.xl))
                    // Screen Title
                    Text(
                        text = "Kiralamalarım",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Stats Summary
                    val tripCount = state.stats?.tripCount ?: 0
                    val totalSpent = state.stats?.totalSpent?.toInt() ?: 0
                    Text(
                        text = "Bu ay $tripCount yolculuk • ₺$totalSpent harcama",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(spacing.lg))
                }

                if (state.rentals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Henüz kiralama geçmişiniz bulunmuyor.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(state.rentals) { rental ->
                        RentalHistoryItem(rental = rental, isDark = isDark)
                    }
                }
            }
        }
    }
}

@Composable
fun RentalHistoryItem(
    rental: RentalResponseDto,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Route Preview Left Graphic
            RoutePreview(
                rentalId = rental.id,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Vehicle Details & Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Vehicle Brand & Model
                Text(
                    text = "${rental.vehicle.brand} ${rental.vehicle.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date & Time
                Text(
                    text = formatRentalDate(rental.startedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Duration & Distance Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PillBadge(text = "${rental.durationMinutes.toInt()} dk")
                    PillBadge(text = formatDistance(rental.distanceKm))
                }
            }

            // Total Price Right Aligned
            Text(
                text = formatPrice(rental.totalPrice),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun RoutePreview(
    rentalId: String,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val routeColor = if (isDark) Color(0xFF4C95F0) else Color(0xFF0F62CD)
    val startDotColor = if (isDark) Color(0xFF4C95F0) else Color(0xFF0F62CD)
    val endDotColor = Color(0xFF10B981) // Green

    val hash = rentalId.hashCode()
    val isStartBottom = hash % 2 == 0
    val controlPointOffset = (hash.coerceAtLeast(0) % 100) / 100f

    Box(
        modifier = modifier
            .background(if (isDark) Color(0xFF141A26) else Color(0xFFF3F5F9))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val path = Path()
            val startX = width * 0.25f
            val startY = if (isStartBottom) height * 0.75f else height * 0.25f
            val endX = width * 0.75f
            val endY = if (isStartBottom) height * 0.25f else height * 0.75f

            val controlX = width * (0.3f + controlPointOffset * 0.4f)
            val controlY = if (isStartBottom) height * 0.35f else height * 0.65f

            path.moveTo(startX, startY)
            path.quadraticTo(controlX, controlY, endX, endY)

            drawPath(
                path = path,
                color = routeColor,
                style = Stroke(width = 3.dp.toPx())
            )

            // Start dot
            drawCircle(
                color = startDotColor,
                radius = 3.5.dp.toPx(),
                center = Offset(startX, startY)
            )

            // End dot
            drawCircle(
                color = endDotColor,
                radius = 3.5.dp.toPx(),
                center = Offset(endX, endY)
            )
        }
    }
}

@Composable
fun PillBadge(text: String) {
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .background(
                color = if (isDark) Color(0xFF1F2937) else Color(0xFFF3F4F6),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )
    }
}

private fun formatRentalDate(startedAt: String): String {
    return try {
        val cleaned = startedAt.replace("Z", "+0000")
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = parser.parse(cleaned) ?: return startedAt
        val outputFormatter = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.forLanguageTag("tr"))
        outputFormatter.timeZone = TimeZone.getDefault()
        outputFormatter.format(date)
    } catch (e: Exception) {
        startedAt
    }
}

private fun formatPrice(price: Double?): String {
    if (price == null) return "₺0,00"
    return String.format(Locale.forLanguageTag("tr"), "₺%.2f", price)
}

private fun formatDistance(distance: Double): String {
    return String.format(Locale.forLanguageTag("tr"), "%.1f km", distance)
}
