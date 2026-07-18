package com.nimetatila.rencarapp_turkcell_gygy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CardResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.PaymentIntent
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.PaymentState
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.RenCarAppTheme
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.PaymentViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSummaryScreen(
    rentalId: String,
    onPaymentSuccess: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val spacing = LocalRencarSpacing.current

    LaunchedEffect(rentalId) {
        viewModel.onIntent(PaymentIntent.LoadDetails(rentalId))
        viewModel.onIntent(PaymentIntent.LoadCards)
    }

    LaunchedEffect(state.paymentSuccess) {
        if (state.paymentSuccess) {
            onPaymentSuccess()
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isRentalLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (state.rentalError != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing.xl),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.rentalError ?: "Yükleme hatası",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(spacing.md))
                    Button(
                        onClick = { viewModel.onIntent(PaymentIntent.LoadDetails(rentalId)) }
                    ) {
                        Text(text = "Tekrar Dene")
                    }
                }
            } else {
                state.rentalDetails?.let { rental ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = spacing.md, vertical = spacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Success checkmark icon in circle
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    if (MaterialTheme.colorScheme.background == Color(0xFF0D0D0D) || MaterialTheme.colorScheme.background == Color(0xFF0F172A)) {
                                        Color(0xFF22C55E).copy(alpha = 0.15f)
                                    } else {
                                        Color(0xFFDCFCE7)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = RencarIcons.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(spacing.md))

                        // Title
                        Text(
                            text = "Yolculuk tamamlandı",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(spacing.xxs))

                        // Vehicle Info
                        Text(
                            text = "${rental.vehicle.brand} ${rental.vehicle.model} · ${rental.vehicle.plate}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // Summary Cards Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md)
                        ) {
                            // Duration Card
                            SummaryDetailCard(
                                title = "Süre",
                                value = "${rental.durationMinutes.toInt()} dk",
                                modifier = Modifier.weight(1f)
                            )

                            // Distance Card
                            val formattedDistance = String.format(Locale.US, "%.1f", rental.distanceKm)
                            SummaryDetailCard(
                                title = "Mesafe",
                                value = "$formattedDistance km",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Breakdown Items
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = spacing.xs)
                        ) {
                            val duration = rental.durationMinutes.toInt()
                            val startFee = rental.startFee
                            val serviceFee = rental.serviceFee ?: 0.0
                            val totalPrice = rental.totalPrice ?: (startFee + serviceFee)

                            // Kiralama Ücreti
                            val kiralamaUcreti = maxOf(0.0, totalPrice - startFee - serviceFee)
                            BreakdownRow(
                                label = "Kiralama ücreti ($duration dk)",
                                value = String.format(Locale.US, "₺%.2f", kiralamaUcreti)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Başlangıç Ücreti
                            BreakdownRow(
                                label = "Başlangıç ücreti",
                                value = String.format(Locale.US, "₺%.2f", startFee)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Hizmet Bedeli
                            BreakdownRow(
                                label = "Hizmet bedeli",
                                value = String.format(Locale.US, "₺%.2f", serviceFee)
                            )

                            // Discount Row (if any applied discount)
                            if (rental.discountAmount > 0.0) {
                                Spacer(modifier = Modifier.height(14.dp))
                                BreakdownRow(
                                    label = "İndirim · ${state.discountCode.ifBlank { "İLKSÜRÜŞ" }}",
                                    value = String.format(Locale.US, "-₺%.2f", rental.discountAmount),
                                    isGreen = true
                                )
                            } else if (state.discountCode.isNotBlank()) {
                                // Show pending or applied discount simulation if they typed something
                                Spacer(modifier = Modifier.height(14.dp))
                                BreakdownRow(
                                    label = "İndirim · ${state.discountCode}",
                                    value = "-₺20.00",
                                    isGreen = true
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                thickness = 1.dp
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Total Price
                            val finalPrice = maxOf(0.0, totalPrice - (if (rental.discountAmount > 0.0) rental.discountAmount else if (state.discountCode.isNotBlank()) 20.0 else 0.0))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Toplam",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = String.format(Locale.US, "₺%.2f", finalPrice),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Discount Code Entry (if no discount applied yet)
                        if (rental.discountAmount == 0.0 && state.discountCode.isBlank()) {
                            OutlinedTextField(
                                value = state.discountCode,
                                onValueChange = { viewModel.onIntent(PaymentIntent.ChangeDiscountCode(it)) },
                                label = { Text("İndirim Kodu Ekle") },
                                placeholder = { Text("İLKSÜRÜŞ") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = spacing.xs),
                                shape = MaterialTheme.shapes.small,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }

                        // Payment Method Selection Card
                        PaymentMethodCard(
                            selectedCard = state.selectedCard,
                            onChangeClick = { showBottomSheet = true }
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        // Error message
                        if (state.paymentError != null) {
                            Text(
                                text = state.paymentError ?: "Bir hata oluştu",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = spacing.md)
                            )
                        }

                        // Pay Button
                        val totalPriceVal = rental.totalPrice ?: (rental.startFee + (rental.serviceFee ?: 0.0))
                        val finalPriceVal = maxOf(0.0, totalPriceVal - (if (rental.discountAmount > 0.0) rental.discountAmount else if (state.discountCode.isNotBlank()) 20.0 else 0.0))
                        Button(
                            onClick = { viewModel.onIntent(PaymentIntent.PayRental) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            enabled = !state.isPaying
                        ) {
                            if (state.isPaying) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = String.format(Locale.US, "₺%.2f Öde", finalPriceVal),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Ödeme Yöntemi Seçin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                if (state.isCardsLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(24.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (state.cards.isEmpty()) {
                    Text(
                        text = "Kayıtlı kartınız bulunmamaktadır.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    state.cards.forEach { card ->
                        CardRow(
                            card = card,
                            isSelected = state.selectedCard?.id == card.id,
                            onClick = {
                                viewModel.onIntent(PaymentIntent.SelectCard(card))
                                showBottomSheet = false
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryDetailCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun BreakdownRow(
    label: String,
    value: String,
    isGreen: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isGreen) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isGreen) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PaymentMethodCard(
    selectedCard: CardResponseDto?,
    onChangeClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // VISA / Mastercard icon simulation
                Box(
                    modifier = Modifier
                        .size(44.dp, 30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedCard?.brand == "MASTERCARD") "MC" else "VISA",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = selectedCard?.let { "•••• ${it.last4}" } ?: "Ödeme Yöntemi Ekle",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = selectedCard?.let { "Kişisel kart" } ?: "Kayıtlı kart bulunamadı",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Text(
                text = "Değiştir",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onChangeClick() }
            )
        }
    }
}

@Composable
fun CardRow(
    card: CardResponseDto,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp, 26.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (card.brand == "MASTERCARD") "MC" else "VISA",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "•••• ${card.last4}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Son Kullanma: ${card.expMonth}/${card.expYear}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
