package com.nimetatila.rencarapp_turkcell_gygy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nimetatila.rencarapp_turkcell_gygy.data.card.CardResponseDto
import com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletTransactionDto
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.WalletEffect
import com.nimetatila.rencarapp_turkcell_gygy.ui.contract.WalletIntent
import com.nimetatila.rencarapp_turkcell_gygy.contract.WalletState
import com.nimetatila.rencarapp_turkcell_gygy.ui.icons.RenCarAppIcons
import com.nimetatila.rencarapp_turkcell_gygy.ui.theme.LocalRencarSpacing
import com.nimetatila.rencarapp_turkcell_gygy.ui.viewmodel.WalletViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    viewModel: WalletViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val spacing = LocalRencarSpacing.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.onIntent(WalletIntent.LoadWallet)
        viewModel.onIntent(WalletIntent.LoadCards)
    }

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WalletEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    var showTopupSheet by remember { mutableStateOf(false) }
    var showAddCardSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isWalletLoading && state.balance == 0.0) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.md, vertical = spacing.lg)
                ) {
                    // Header Title
                    Text(
                        text = "Cüzdan",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Rencar Balance Card (gradient)
                    BalanceGradientCard(
                        balance = state.balance,
                        onTopupClick = {
                            if (state.cards.isEmpty()) {
                                Toast.makeText(context, "Bakiye yüklemek için lütfen önce bir kart ekleyin", Toast.LENGTH_SHORT).show()
                            } else {
                                showTopupSheet = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Registered Cards Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kayıtlı kartlar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "+ Ekle",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showAddCardSheet = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Registered Cards List
                    if (state.isCardsLoading && state.cards.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(spacing.md),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else if (state.cards.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.03f),
                                    spotColor = Color.Black.copy(alpha = 0.05f)
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Kayıtlı kartınız bulunmuyor. Hemen bir kart ekleyin.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.cards.forEach { card ->
                                RegisteredCardItem(
                                    card = card,
                                    onCardClick = {
                                        if (!card.isDefault) {
                                            viewModel.onIntent(WalletIntent.SetDefaultCard(card.id))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Recent Transactions Header
                    Text(
                        text = "Son işlemler",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Recent Transactions List
                    if (state.transactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.03f),
                                    spotColor = Color.Black.copy(alpha = 0.05f)
                                )
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Henüz bir işlem kaydı bulunmuyor.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            state.transactions.take(10).forEach { transaction ->
                                TransactionItem(transaction = transaction)
                            }
                        }
                    }
                }
            }
        }
    }

    // Topup Bottom Sheet
    if (showTopupSheet) {
        TopupBottomSheet(
            onDismiss = { showTopupSheet = false },
            onTopupClick = { amount ->
                viewModel.onIntent(WalletIntent.TopupWallet(amount))
                showTopupSheet = false
            },
            isTopupLoading = state.isTopupLoading,
            topupError = state.topupError
        )
    }

    // Add Card Bottom Sheet
    if (showAddCardSheet) {
        AddCardBottomSheet(
            onDismiss = { showAddCardSheet = false },
            onAddCardClick = { brand, last4, expMonth, expYear ->
                viewModel.onIntent(WalletIntent.AddCard(brand, last4, expMonth, expYear))
                showAddCardSheet = false
            },
            isCardAdding = state.isCardAdding,
            cardAddingError = state.cardAddingError
        )
    }
}

@Composable
fun BalanceGradientCard(
    balance: Double,
    onTopupClick: () -> Unit
) {
    val spacing = LocalRencarSpacing.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color(0xFF0F62CD).copy(alpha = 0.2f),
                spotColor = Color(0xFF0F62CD).copy(alpha = 0.3f)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3B82F6),
                        Color(0xFF1D4ED8)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(spacing.md)
    ) {
        Column {
            Text(
                text = "Rencar bakiyesi",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format(Locale.US, "₺%.2f", balance),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onTopupClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                elevation = null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = RenCarAppIcons.AddBalance,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Bakiye Yükle",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RegisteredCardItem(
    card: CardResponseDto,
    onCardClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.05f)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCardClick() }
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
                // VISA / MasterCard Badge
                Box(
                    modifier = Modifier
                        .size(44.dp, 30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (card.brand == "MASTERCARD") Color(0xFFEA580C) else Color(0xFF0F172A)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (card.brand == "MASTERCARD") "MC" else "VISA",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "•••• ${card.last4}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val formattedMonth = String.format("%02d", card.expMonth)
                    val formattedYear = card.expYear.toString().takeLast(2)
                    Text(
                        text = "Son kullanma $formattedMonth/$formattedYear",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            if (card.isDefault) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Varsayılan",
                        color = Color(0xFF15803D),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: WalletTransactionDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = Color.Black.copy(alpha = 0.03f),
                spotColor = Color.Black.copy(alpha = 0.05f)
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
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isNegative = transaction.amount < 0
                val iconBg = if (isNegative) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                val iconTint = if (isNegative) Color(0xFFEF4444) else Color(0xFF22C55E)
                val icon = if (isNegative) RenCarAppIcons.Car else RenCarAppIcons.AddBalance

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatIsoDate(transaction.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            val isNegative = transaction.amount < 0
            val amountText = if (isNegative) {
                String.format(Locale.US, "-₺%.2f", -transaction.amount)
            } else {
                String.format(Locale.US, "+₺%.2f", transaction.amount)
            }
            Text(
                text = amountText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isNegative) MaterialTheme.colorScheme.onSurface else Color(0xFF22C55E),
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

fun formatIsoDate(isoString: String): String {
    if (isoString.length < 16) return isoString
    val datePart = isoString.substring(0, 10)
    val timePart = isoString.substring(11, 16)

    // Simplified translation
    val year = datePart.substring(0, 4)
    val month = datePart.substring(5, 7)
    val day = datePart.substring(8, 10)

    val monthName = when (month) {
        "01" -> "Ocak"
        "02" -> "Şubat"
        "03" -> "Mart"
        "04" -> "Nisan"
        "05" -> "Mayıs"
        "06" -> "Haziran"
        "07" -> "Temmuz"
        "08" -> "Ağustos"
        "09" -> "Eylül"
        "10" -> "Ekim"
        "11" -> "Kasım"
        "12" -> "Aralık"
        else -> month
    }

    return "$day $monthName - $timePart"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopupBottomSheet(
    onDismiss: () -> Unit,
    onTopupClick: (Double) -> Unit,
    isTopupLoading: Boolean,
    topupError: String?
) {
    var amountText by remember { mutableStateOf("") }
    val amounts = listOf(50.0, 100.0, 200.0, 500.0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bakiye Yükle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Fast amount chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                amounts.forEach { amt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { amountText = amt.toInt().toString() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₺${amt.toInt()}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Yüklenecek Tutar (TL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            if (topupError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = topupError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val amtVal = amountText.toDoubleOrNull()
                    if (amtVal != null) {
                        onTopupClick(amtVal)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !isTopupLoading
            ) {
                if (isTopupLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Yüklemeyi Tamamla",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardBottomSheet(
    onDismiss: () -> Unit,
    onAddCardClick: (String, String, Int, Int) -> Unit,
    isCardAdding: Boolean,
    cardAddingError: String?
) {
    var brand by remember { mutableStateOf("VISA") }
    var last4 by remember { mutableStateOf("") }
    var expMonth by remember { mutableStateOf("") }
    var expYear by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Yeni Kart Ekle",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )

            // Brand Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("VISA", "MASTERCARD").forEach { br ->
                    val isSelected = br == brand
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { brand = br }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = br,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Last 4 Digits
            OutlinedTextField(
                value = last4,
                onValueChange = { if (it.length <= 4) last4 = it },
                label = { Text("Kart Numarasının Son 4 Hanesi") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Expiry Month & Year
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = expMonth,
                    onValueChange = { if (it.length <= 2) expMonth = it },
                    label = { Text("Ay (MM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                OutlinedTextField(
                    value = expYear,
                    onValueChange = { if (it.length <= 4) expYear = it },
                    label = { Text("Yıl (YYYY)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            if (cardAddingError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = cardAddingError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val mVal = expMonth.toIntOrNull()
                    val yVal = expYear.toIntOrNull()
                    if (last4.length == 4 && mVal != null && yVal != null) {
                        onAddCardClick(brand, last4, mVal, yVal)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                enabled = !isCardAdding
            ) {
                if (isCardAdding) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Kartı Kaydet",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
