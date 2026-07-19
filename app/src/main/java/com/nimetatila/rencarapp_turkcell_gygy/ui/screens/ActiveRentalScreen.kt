package com.nimetatila.rencarapp_turkcell_gygy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.ui.components.RenCarAppMap
import com.nimetatila.rencarapp_turkcell_gygy.ui.components.rememberRencarMapController
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ActiveRentalEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ActiveRentalIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.ActiveRentalViewModel
import org.maplibre.android.geometry.LatLng
import java.util.Locale

@Composable
fun ActiveRentalScreen(
    rentalId: String,
    onEndSuccess: (String) -> Unit,
    viewModel: ActiveRentalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalRencarSpacing.current
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(rentalId) {
        viewModel.onIntent(ActiveRentalIntent.LoadActiveRental(rentalId))
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ActiveRentalEffect.NavigateToPaymentSummary -> {
                    Toast.makeText(context, "Kiralama sonlandırıldı!", Toast.LENGTH_SHORT).show()
                    onEndSuccess(effect.rentalId)
                }
                is ActiveRentalEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Local ticker for duration
    var elapsedSecondsLocal by remember { mutableStateOf(0) }

    // Sync local ticker with server data when activeRental updates
    LaunchedEffect(uiState.activeRental) {
        uiState.activeRental?.let {
            elapsedSecondsLocal = it.elapsedSeconds.toInt()
        }
    }

    // Tick local timer every second if rental status is ACTIVE
    LaunchedEffect(uiState.activeRental?.status) {
        if (uiState.activeRental?.status == "ACTIVE") {
            while (true) {
                kotlinx.coroutines.delay(1000)
                elapsedSecondsLocal++
            }
        }
    }

    val carLatLng = remember(uiState.carLocation) {
        uiState.carLocation?.let { LatLng(it.latitude, it.longitude) }
    }

    val mapController = rememberRencarMapController()

    // Animate map camera to the car location when it changes
    LaunchedEffect(carLatLng) {
        carLatLng?.let {
            mapController.animateTo(it, zoom = 10.5)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Map
        RenCarAppMap(
            myLocation = carLatLng, // Draws blue dot on the car
            modifier = Modifier.fillMaxSize(),
            controller = mapController,
            initialZoom = 10.5
        )

        // 2. Top Banner Card
        uiState.activeRental?.let { rental ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md, vertical = spacing.sm)
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(100.dp))
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .padding(horizontal = spacing.md, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Green pulsating dot indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    text = "Kiralama aktif - ${rental.vehicle.brand} ${rental.vehicle.model}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 3. Bottom Summary Info Sheet
        uiState.activeRental?.let { rental ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        clip = false
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(horizontal = spacing.md, vertical = spacing.sm)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .size(40.dp, 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Trip Duration (HH:mm:ss)
                Text(
                    text = "Geçen süre",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val formattedTime = remember(elapsedSecondsLocal) {
                    val hours = elapsedSecondsLocal / 3600
                    val minutes = (elapsedSecondsLocal % 3600) / 60
                    val seconds = elapsedSecondsLocal % 60
                    String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
                }

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(spacing.md))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    // Current Cost
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(spacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Anlık ücret",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val formattedCost = String.format(Locale.US, "₺%.2f", rental.currentCost).replace('.', ',')
                        Text(
                            text = formattedCost,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Distance Accumulation
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(spacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Mesafe",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val formattedDistance = String.format(Locale.US, "%.1f km", rental.distanceKm).replace('.', ',')
                        Text(
                            text = formattedDistance,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.lg))

                // Actions Button
                Button(
                    onClick = {
                        viewModel.onIntent(ActiveRentalIntent.EndRental)
                    },
                    enabled = !uiState.isEndingRental,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (uiState.isEndingRental) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Kiralamayı Bitir",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spacing.xs))
            }
        }

        // 4. Loading Overlay (Initial load)
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
