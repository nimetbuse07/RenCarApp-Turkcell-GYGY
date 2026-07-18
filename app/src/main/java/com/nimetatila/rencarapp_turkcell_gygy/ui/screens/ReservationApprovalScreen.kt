package com.nimetatila.rencarapp_turkcell_gygy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ReservationEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.ReservationIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.ReservationViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationApprovalScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    onReservationSuccess: () -> Unit,
    viewModel: ReservationViewModel
) {
    val spacing = LocalRencarSpacing.current
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(vehicleId) {
        viewModel.onIntent(ReservationIntent.LoadVehicle(vehicleId))
    }

    val vehicle = uiState.vehicle
    val isLoadingVehicle = uiState.isLoadingVehicle

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ReservationEffect.NavigateToSuccess -> {
                    Toast.makeText(context, "Kiralama başarıyla başlatıldı!", Toast.LENGTH_SHORT).show()
                    onReservationSuccess()
                }
                is ReservationEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Rezervasyon Onayı",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
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
        if (isLoadingVehicle || vehicle == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val currentVehicle = vehicle!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = spacing.md),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Vehicle Info Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(spacing.md)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currentVehicle.brand} ${currentVehicle.model}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                // Fuel percent badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSystemInDarkTheme()) Color(0xFF064E3B) else Color(0xFFDCFCE7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Yakıt %${currentVehicle.fuelPercent.toInt()}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSystemInDarkTheme()) Color(0xFF34D399) else Color(0xFF16A34A)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.xxs))

                            val transVal = if (currentVehicle.transmission.lowercase() == "manual") "Manuel" else "Otomatik"
                            Text(
                                text = "${currentVehicle.plate} · $transVal · ${currentVehicle.seats} kişi",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.lg))

                    Text(
                        text = "Kiralama planı",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // 3-columns Rental Plan selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                    ) {
                        val formattedMin = String.format(Locale.US, "₺%.2f/dk", currentVehicle.pricePerMinute).replace('.', ',')
                        PlanSelectionCard(
                            title = "Dakikalık",
                            price = formattedMin,
                            isSelected = uiState.selectedPlan == "PER_MINUTE",
                            onClick = { viewModel.onIntent(ReservationIntent.PlanSelected("PER_MINUTE")) },
                            modifier = Modifier.weight(1f)
                        )
                        PlanSelectionCard(
                            title = "Saatlik",
                            price = "₺${currentVehicle.pricePerHour.toInt()}/sa",
                            isSelected = uiState.selectedPlan == "HOURLY",
                            onClick = { viewModel.onIntent(ReservationIntent.PlanSelected("HOURLY")) },
                            modifier = Modifier.weight(1f)
                        )
                        PlanSelectionCard(
                            title = "Günlük",
                            price = "₺${currentVehicle.pricePerDay.toInt()}",
                            isSelected = uiState.selectedPlan == "DAILY",
                            onClick = { viewModel.onIntent(ReservationIntent.PlanSelected("DAILY")) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.lg))

                    // Pricing Details Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(spacing.md)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            // Row 1: Free reservation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Ücretsiz rezervasyon",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "15 dk",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            // Row 2: Start fee
                            val startFee = if (uiState.selectedPlan == "PER_MINUTE") 15.0 else 0.0
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Başlangıç ücreti",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = String.format(Locale.US, "₺%.2f", startFee).replace('.', ','),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            // Row 3: Estimated fee (30 min or 1 hour or 1 day)
                            val (estimateLabel, estimateValue) = when (uiState.selectedPlan) {
                                "PER_MINUTE" -> {
                                    val valEst = startFee + (currentVehicle.pricePerMinute * 30)
                                    "Tahmini ücret (30 dk)" to String.format(Locale.US, "~₺%.0f", valEst)
                                }
                                "HOURLY" -> {
                                    "Tahmini ücret (1 sa)" to "~₺${currentVehicle.pricePerHour.toInt()}"
                                }
                                else -> {
                                    "Tahmini ücret (1 gün)" to "~₺${currentVehicle.pricePerDay.toInt()}"
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = estimateLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = estimateValue,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(bottom = spacing.xl)) {
                    // Terms Agreement Checkbox
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onIntent(
                                    ReservationIntent.AgreedToTermsChanged(!uiState.isAgreedToTerms)
                                )
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = uiState.isAgreedToTerms,
                            onCheckedChange = { isChecked ->
                                viewModel.onIntent(ReservationIntent.AgreedToTermsChanged(isChecked))
                            }
                        )
                        Spacer(modifier = Modifier.width(spacing.xs))
                        Text(
                            text = "Kullanım şartlarını ve kasko/sigorta koşullarını okudum, onaylıyorum.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.md))

                    // Reservation action button
                    val isBtnEnabled = uiState.isAgreedToTerms && !uiState.isSubmitting
                    Button(
                        onClick = {
                            viewModel.onIntent(ReservationIntent.SubmitReservation)
                        },
                        enabled = isBtnEnabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Rezervasyonunu Tamamla",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanSelectionCard(
    title: String,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalRencarSpacing.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = spacing.md, horizontal = spacing.xs)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(spacing.xxs))
            Text(
                text = price,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
