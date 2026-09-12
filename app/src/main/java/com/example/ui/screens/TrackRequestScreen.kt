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
import androidx.compose.material.icons.outlined.*
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
import com.example.data.model.AppLanguage
import com.example.data.model.ConnectionRequestEntity
import com.example.data.model.RequestStatus
import com.example.ui.AppScreen
import com.example.ui.strings.SeneauStrings
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackRequestScreen(
    requests: List<ConnectionRequestEntity>,
    selectedRequest: ConnectionRequestEntity?,
    lang: AppLanguage,
    onSelectRequest: (ConnectionRequestEntity) -> Unit,
    onAdvanceStage: () -> Unit,
    onPayQuote: (ConnectionRequestEntity) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val currentRequest = selectedRequest ?: requests.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("track_request_screen_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Request Selector if multiple
        if (requests.size > 1) {
            item {
                Text(
                    text = if (lang == AppLanguage.WO) "Say dosiye yépp :" else "Vos dossiers enregistrés :",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    requests.forEach { req ->
                        val isSelected = currentRequest?.id == req.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectRequest(req) },
                            label = { Text(req.fileNumber, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (req.isQuotePaid) Icons.Default.CheckCircle else Icons.Default.Pending,
                                    contentDescription = null,
                                    tint = if (req.isQuotePaid) SeneauSuccess else SeneauGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        if (currentRequest == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = SeneauStrings.noActiveRequest(lang),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigate(AppScreen.NEW_REQUEST) },
                            colors = ButtonDefaults.buttonColors(containerColor = SeneauDeepBlue)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(SeneauStrings.startRequestBtn(lang))
                        }
                    }
                }
            }
        } else {
            // Dossier Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dossier_header_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SeneauAqua.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "DOSSIER SEN'EAU",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SeneauDeepBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentRequest.fileNumber,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            // Percentage Pill
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SeneauDeepBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${currentRequest.progressPercentage}%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.FRANCE)
                        Text(
                            text = "Demandeur : ${currentRequest.applicantName}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Emplacement : ${currentRequest.district}, ${currentRequest.city} (${currentRequest.region})",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "Date de dépôt : ${dateFormat.format(Date(currentRequest.createdAt))}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Text(
                            text = "Diamètre : ${currentRequest.pipeDiameter} • Distance estimée : ${currentRequest.distanceToNetworkMeters}m",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Current Agent Note Banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Engineering,
                                    contentDescription = null,
                                    tint = SeneauPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = currentRequest.assignedAgent,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = currentRequest.agentNotes,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }
                        }

                        // Direct Pay Quote Button if quote is ready and not paid
                        val reqStatus = try { RequestStatus.valueOf(currentRequest.status) } catch (_: Exception) { RequestStatus.DEPOT }
                        if (reqStatus == RequestStatus.DEVIS_DISPONIBLE && !currentRequest.isQuotePaid) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { onPayQuote(currentRequest) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("pay_quote_direct_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = SeneauGold),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == AppLanguage.WO)
                                        "Fey saytu bi : ${String.format("%,.0f", currentRequest.estimatedCost)} FCFA"
                                    else
                                        "Régler le devis : ${String.format("%,.0f", currentRequest.estimatedCost)} FCFA",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // Real-Time Stepper Tracker (The 6 Stages of SEN'EAU Connection)
            item {
                Text(
                    text = if (lang == AppLanguage.WO) "Ay téego yu lëkkalekaay bi (6 téego) :" else "Étapes de raccordement en direct :",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            val currentStage = try { RequestStatus.valueOf(currentRequest.status).stage } catch (_: Exception) { 1 }

            RequestStatus.values().forEach { stageItem ->
                item {
                    val isPassed = stageItem.stage < currentStage
                    val isCurrent = stageItem.stage == currentStage
                    val isFuture = stageItem.stage > currentStage

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("stage_card_${stageItem.stage}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                isPassed -> MaterialTheme.colorScheme.surface
                                else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrent) 3.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Step Indicator Icon
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isPassed -> SeneauSuccess
                                            isCurrent -> SeneauDeepBlue
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when {
                                        isPassed -> Icons.Default.Check
                                        isCurrent -> Icons.Default.Pending
                                        else -> Icons.Default.Schedule
                                    },
                                    contentDescription = null,
                                    tint = if (isPassed || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${stageItem.stage}. " + if (lang == AppLanguage.WO) stageItem.titleWo else stageItem.titleFr,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stageItem.descriptionFr,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }

                            if (isCurrent) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SeneauGold
                                ) {
                                    Text(
                                        text = "EN COURS",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Real-time Simulation Action Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (lang == AppLanguage.WO) "Saytu ci saa si (Simulation) :" else "Contrôle temps réel & Test :",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (lang == AppLanguage.WO)
                                "Bësal ngir yóbbal dosiye bi kanam ak jot bataaxal SMS ci saa si."
                            else
                                "Faites progresser l'étape du dossier pour tester les mises à jour en direct et la réception du SMS instantané.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onAdvanceStage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("simulate_advance_stage_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = SeneauStrings.simulateProgress(lang),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
