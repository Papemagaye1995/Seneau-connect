package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.strings.SeneauStrings
import com.example.ui.theme.*

@Composable
fun PaymentScreen(
    userAccount: UserAccountEntity?,
    requests: List<ConnectionRequestEntity>,
    lang: AppLanguage,
    onPayQuote: (ConnectionRequestEntity, PaymentMethod, payFromBalance: Boolean) -> Unit,
    onRechargeWallet: (Double, PaymentMethod) -> Unit
) {
    var paymentCategory by remember { mutableStateOf(0) } // 0: Devis branchement, 1: Facture d'eau, 2: Recharge solde
    var selectedMethod by remember { mutableStateOf(PaymentMethod.WAVE) }
    var payFromBalance by remember { mutableStateOf(false) }

    // Recharge amounts
    val rechargeOptions = listOf(10000.0, 25000.0, 50000.0, 100000.0)
    var selectedRechargeAmount by remember { mutableStateOf(50000.0) }
    var customAmount by remember { mutableStateOf("") }

    val pendingQuoteRequest = requests.firstOrNull { !it.isQuotePaid }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("payment_screen_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payment_balance_header"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = SeneauStrings.balanceLabel(lang),
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
                        )
                        Text(
                            text = "${String.format("%,.0f", userAccount?.balance ?: 0.0)} FCFA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SeneauSuccessLight
                    ) {
                        Text(
                            text = "Sécurisé SSL 256-bit",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SeneauSuccess
                            )
                        )
                    }
                }
            }
        }

        // Category Tab Switcher
        item {
            TabRow(
                selectedTabIndex = paymentCategory,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = paymentCategory == 0,
                    onClick = { paymentCategory = 0 },
                    text = { Text(if (lang == AppLanguage.WO) "Saytu" else "Devis", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_pay_devis")
                )
                Tab(
                    selected = paymentCategory == 1,
                    onClick = { paymentCategory = 1 },
                    text = { Text(if (lang == AppLanguage.WO) "Faktuur" else "Facture", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_pay_facture")
                )
                Tab(
                    selected = paymentCategory == 2,
                    onClick = { paymentCategory = 2 },
                    text = { Text(if (lang == AppLanguage.WO) "Doolil" else "Recharger", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("tab_pay_recharge")
                )
            }
        }

        // Mode-specific Content
        when (paymentCategory) {
            0 -> {
                // Pay Connection Quote
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.WO) "Saytu lëkkalekaay ndox :" else "Règlement devis de branchement",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            if (pendingQuoteRequest != null) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Dossier : ${pendingQuoteRequest.fileNumber}",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Demandeur : ${pendingQuoteRequest.applicantName}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Text(
                                            text = "Diamètre : ${pendingQuoteRequest.pipeDiameter} • Distance : ${pendingQuoteRequest.distanceToNetworkMeters}m",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Montant du devis :",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                            Text(
                                                text = "${String.format("%,.0f", pendingQuoteRequest.estimatedCost)} FCFA",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = SeneauPrimary
                                                )
                                            )
                                        }
                                    }
                                }

                                // Toggle pay from balance
                                val currentBal = userAccount?.balance ?: 0.0
                                val canPayFromBalance = currentBal >= pendingQuoteRequest.estimatedCost

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (lang == AppLanguage.WO) "Waññi ko ci sa kàmp usager" else "Payer depuis mon solde usager",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text(
                                            text = if (canPayFromBalance) "Solde suffisant (${String.format("%,.0f", currentBal)} FCFA)" else "Solde insuffisant pour ce devis",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (canPayFromBalance) SeneauSuccess else SeneauError
                                            )
                                        )
                                    }
                                    Switch(
                                        checked = payFromBalance,
                                        onCheckedChange = { if (canPayFromBalance) payFromBalance = it },
                                        enabled = canPayFromBalance,
                                        modifier = Modifier.testTag("switch_pay_from_balance")
                                    )
                                }
                            } else {
                                Text(
                                    text = if (lang == AppLanguage.WO) "Amul benn saytu bu des ngir fey." else "Tous vos devis de branchement sont déjà réglés ou aucun dossier n'est en attente.",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Pay Water Bill
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.WO) "Faktuur ndoxu kër gi" else "Facture d'eau bimestrielle",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Police abonné : POL-77492-DKR", fontWeight = FontWeight.Bold)
                                    Text("Période : Juillet - Août 2026", style = MaterialTheme.typography.bodySmall)
                                    Text("Consommation : 18.5 m³", style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Net à payer :", fontWeight = FontWeight.SemiBold)
                                        Text("14 800 FCFA", fontWeight = FontWeight.Bold, color = SeneauPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Recharge Balance
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.WO) "Tannal koppar bi ngay doolil :" else "Montant de la recharge :",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rechargeOptions.take(2).forEach { amount ->
                                    val isSelected = selectedRechargeAmount == amount
                                    OutlinedButton(
                                        onClick = { selectedRechargeAmount = amount },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) SeneauSoftBlue else Color.Transparent
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) SeneauDeepBlue else MaterialTheme.colorScheme.outline)
                                        )
                                    ) {
                                        Text("${String.format("%,.0f", amount)} F", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rechargeOptions.drop(2).forEach { amount ->
                                    val isSelected = selectedRechargeAmount == amount
                                    OutlinedButton(
                                        onClick = { selectedRechargeAmount = amount },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isSelected) SeneauSoftBlue else Color.Transparent
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) SeneauDeepBlue else MaterialTheme.colorScheme.outline)
                                        )
                                    ) {
                                        Text("${String.format("%,.0f", amount)} F", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Payment Methods Selector
        if (!payFromBalance || paymentCategory == 2) {
            item {
                Text(
                    text = SeneauStrings.selectPaymentMethod(lang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PaymentMethod.values().forEach { method ->
                        val isSelected = selectedMethod == method
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method }
                                .testTag("payment_method_${method.name.lowercase()}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SeneauDeepBlue) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(method.logoColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = method.displayName,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = when (method) {
                                            PaymentMethod.WAVE -> "Paiement 0% de frais en 1-clic"
                                            PaymentMethod.ORANGE_MONEY -> "Code marchand #144#391#"
                                            PaymentMethod.FREE_MONEY -> "Paiement Free sécurisé"
                                            PaymentMethod.CARTE_BANCAIRE -> "GIM-UEMOA / Visa / Mastercard"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedMethod = method }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Confirmation Button
        item {
            Button(
                onClick = {
                    when (paymentCategory) {
                        0 -> {
                            if (pendingQuoteRequest != null) {
                                onPayQuote(pendingQuoteRequest, selectedMethod, payFromBalance)
                            }
                        }
                        1 -> {
                            // Bill payment dummy quote
                            if (requests.isNotEmpty()) {
                                onPayQuote(requests.first().copy(estimatedCost = 14800.0), selectedMethod, payFromBalance)
                            }
                        }
                        2 -> {
                            onRechargeWallet(selectedRechargeAmount, selectedMethod)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_payment_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SeneauDeepBlue),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                val actionLabel = when (paymentCategory) {
                    0 -> if (lang == AppLanguage.WO) "Fey saytu bi ci kaarange" else "Payer le devis sécurisé"
                    1 -> if (lang == AppLanguage.WO) "Fey faktuur bi ci kaarange" else "Payer la facture (14 800 FCFA)"
                    2 -> if (lang == AppLanguage.WO) "Doolil ${String.format("%,.0f", selectedRechargeAmount)} FCFA" else "Confirmer la recharge (${String.format("%,.0f", selectedRechargeAmount)} FCFA)"
                    else -> "Valider"
                }
                Text(
                    text = actionLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
