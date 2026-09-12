package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.TransactionEntity
import com.example.ui.strings.SeneauStrings
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionHistoryScreen(
    transactions: List<TransactionEntity>,
    lang: AppLanguage,
    onExportCsv: (android.content.Context) -> Unit
) {
    val context = LocalContext.current
    var filterMode by remember { mutableStateOf("ALL") } // ALL, CREDIT, DEBIT
    var selectedTrxForDetails by remember { mutableStateOf<TransactionEntity?>(null) }

    val filteredList = when (filterMode) {
        "CREDIT" -> transactions.filter { it.isCredit }
        "DEBIT" -> transactions.filter { !it.isCredit }
        else -> transactions
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("transaction_history_screen_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header with CSV Export
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                            text = if (lang == AppLanguage.WO) "Jaar-jaaru koppar yi" else "Historique des transactions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${transactions.size} opération(s) enregistrée(s)",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    // CSV Export Button
                    Button(
                        onClick = { onExportCsv(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = SeneauDeepBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("export_csv_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(SeneauStrings.exportCsvBtn(lang), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterMode == "ALL",
                    onClick = { filterMode = "ALL" },
                    label = { Text(if (lang == AppLanguage.WO) "Yépp" else "Tous") },
                    modifier = Modifier.testTag("filter_all")
                )
                FilterChip(
                    selected = filterMode == "DEBIT",
                    onClick = { filterMode = "DEBIT" },
                    label = { Text(if (lang == AppLanguage.WO) "Fey yi" else "Paiements") },
                    modifier = Modifier.testTag("filter_debit")
                )
                FilterChip(
                    selected = filterMode == "CREDIT",
                    onClick = { filterMode = "CREDIT" },
                    label = { Text(if (lang == AppLanguage.WO) "Doolil yi" else "Recharges") },
                    modifier = Modifier.testTag("filter_credit")
                )
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lang == AppLanguage.WO) "Amul benn jaar-jaar." else "Aucune transaction trouvée.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            items(filteredList) { trx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedTrxForDetails = trx }
                        .testTag("transaction_item_${trx.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Direction Icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (trx.isCredit) SeneauSuccessLight else Color(0xFFFEE2E2)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (trx.isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (trx.isCredit) SeneauSuccess else SeneauError,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = trx.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${trx.paymentMethod} • ${dateFormat.format(Date(trx.timestamp))}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = "Réf: ${trx.reference}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        // Amount
                        Text(
                            text = (if (trx.isCredit) "+ " else "- ") + "${String.format("%,.0f", trx.amount)} F",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (trx.isCredit) SeneauSuccess else SeneauError
                            )
                        )
                    }
                }
            }
        }
    }

    // Detailed Receipt Modal Dialog
    if (selectedTrxForDetails != null) {
        val trx = selectedTrxForDetails!!
        Dialog(onDismissRequest = { selectedTrxForDetails = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("receipt_detail_dialog"),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SeneauSoftBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SeneauDeepBlue,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "QUITTANCE OFFICIELLE SEN'EAU",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = SeneauDeepBlue
                        )
                    )
                    Text(
                        text = "Société Nationale des Eaux du Sénégal",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(14.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReceiptRow("Référence", trx.reference)
                        ReceiptRow("Type d'opération", trx.title)
                        ReceiptRow("Mode de paiement", trx.paymentMethod)
                        ReceiptRow("Montant", "${String.format("%,.0f", trx.amount)} FCFA")
                        ReceiptRow("Statut", trx.status)
                        ReceiptRow("Date & Heure", dateFormat.format(Date(trx.timestamp)))
                        if (trx.relatedFileNumber != null) {
                            ReceiptRow("N° Dossier", trx.relatedFileNumber)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedTrxForDetails = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SeneauDeepBlue)
                    ) {
                        Text("Fermer le reçu")
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
    }
}
