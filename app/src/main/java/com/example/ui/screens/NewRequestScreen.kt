package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.data.model.AppLanguage
import com.example.data.model.UsageType
import com.example.data.model.UserAccountEntity
import com.example.ui.strings.SeneauStrings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    userAccount: UserAccountEntity?,
    lang: AppLanguage,
    onSubmitRequest: (
        applicantName: String,
        nationalId: String,
        phone: String,
        usageType: String,
        region: String,
        city: String,
        district: String,
        plotNumber: String,
        pipeDiameter: String,
        distanceMeters: Int,
        hasNearbyNetwork: Boolean,
        neighborMeterRef: String
    ) -> Unit,
    onBackToHome: () -> Unit
) {
    var applicantName by remember { mutableStateOf(userAccount?.fullName ?: "Mamadou Lamine Diop") }
    var nationalId by remember { mutableStateOf(userAccount?.nationalId ?: "1 756 1992 04512") }
    var phoneNumber by remember { mutableStateOf(userAccount?.phone ?: "+221 77 645 89 20") }
    var selectedUsage by remember { mutableStateOf(UsageType.DOMESTIQUE) }

    val regions = listOf("Dakar", "Thiès", "Saint-Louis", "Kaolack", "Ziguinchor", "Diourbel", "Fatick", "Louga", "Tambacounda")
    var selectedRegion by remember { mutableStateOf("Dakar") }
    var city by remember { mutableStateOf("Rufisque") }
    var district by remember { mutableStateOf("Arafat, Cité des Enseignants") }
    var plotNumber by remember { mutableStateOf("Parcelle N° 204") }

    val diameters = listOf("15mm", "20mm", "25mm")
    var selectedDiameter by remember { mutableStateOf("15mm") }
    var distanceMeters by remember { mutableStateOf("8") }
    var hasNearbyNetwork by remember { mutableStateOf(true) }
    var neighborMeterRef by remember { mutableStateOf("CPT-RUF-39014") }

    var isSubmitting by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("new_request_screen_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SeneauDeepBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddLocationAlt,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = SeneauStrings.newRequestBanner(lang),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = SeneauStrings.newRequestDesc(lang),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        )
                    }
                }
            }
        }

        // Section 1: Demandeur & Usage
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
                        text = if (lang == AppLanguage.WO) "1. Boroom kër gi ak li mu bëgg" else "1. Identité & Type d'usage",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text(if (lang == AppLanguage.WO) "Sa tur ak sant" else "Nom complet du demandeur") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_applicant_name"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = nationalId,
                        onValueChange = { nationalId = it },
                        label = { Text(if (lang == AppLanguage.WO) "Nimero CNI / Passpor" else "N° CNI ou Passeport") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_national_id"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(if (lang == AppLanguage.WO) "Telefon (ngir SMS)" else "Téléphone portable (alertes SMS)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_phone_number"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )

                    Text(
                        text = if (lang == AppLanguage.WO) "Tannal nooy jëfandikoo ndox mi :" else "Usage prévu du branchement :",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UsageType.values().take(2).forEach { type ->
                            val isSelected = selectedUsage == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedUsage = type },
                                label = { Text(if (lang == AppLanguage.WO) type.labelWo else type.labelFr, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UsageType.values().drop(2).forEach { type ->
                            val isSelected = selectedUsage == type
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedUsage = type },
                                label = { Text(if (lang == AppLanguage.WO) type.labelWo else type.labelFr, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Localisation
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
                        text = if (lang == AppLanguage.WO) "2. Béréb bi ci Senegaal" else "2. Emplacement du bâtiment",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Region Selector
                    Text(
                        text = "Région SEN'EAU :",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        regions.take(4).forEach { reg ->
                            FilterChip(
                                selected = selectedRegion == reg,
                                onClick = { selectedRegion = reg },
                                label = { Text(reg, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text(if (lang == AppLanguage.WO) "Dëkk bi / Komiin" else "Ville / Commune") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_city"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text(if (lang == AppLanguage.WO) "Kogne bi" else "Quartier & Adresse précise") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_district"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = plotNumber,
                        onValueChange = { plotNumber = it },
                        label = { Text(if (lang == AppLanguage.WO) "Nimero Parsel walla Kéyit" else "N° Parcelle / Titre Foncier / Bail") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_plot_number"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }
                    )
                }
            }
        }

        // Section 3: Données techniques
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
                        text = if (lang == AppLanguage.WO) "3. Xarala ak tuyoo" else "3. Données techniques du branchement",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = if (lang == AppLanguage.WO) "Yaatuwaayu tuyoo bi :" else "Diamètre souhaité :",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        diameters.forEach { d ->
                            FilterChip(
                                selected = selectedDiameter == d,
                                onClick = { selectedDiameter = d },
                                label = {
                                    Text(
                                        text = if (d == "15mm") "15mm (Standard)" else d,
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = distanceMeters,
                        onValueChange = { distanceMeters = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (lang == AppLanguage.WO) "Tolluwaay bi ci tuyoo bi (meetar)" else "Distance estimée au réseau (mètres)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_distance_meters"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = neighborMeterRef,
                        onValueChange = { neighborMeterRef = it },
                        label = { Text(if (lang == AppLanguage.WO) "Réf. Kompteeru dëkkandoo" else "N° de compteur du voisin le plus proche") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_neighbor_meter"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (lang == AppLanguage.WO) "Am na tuyoo ndox ci mbedd mi ?" else "Présence de conduite dans la rue :",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Switch(
                            checked = hasNearbyNetwork,
                            onCheckedChange = { hasNearbyNetwork = it },
                            modifier = Modifier.testTag("switch_nearby_network")
                        )
                    }
                }
            }
        }

        // Section 4: Sécurité & RGPD Protection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SeneauSoftBlue)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = SeneauDeepBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (lang == AppLanguage.WO)
                            "Say mbir yépp dañu leen tëj ci kaarange AES-256 ak sàmm mbir (RGPD / CDP Sénégal)."
                        else
                            "Données cryptées en transit et au repos. Protection intégrale des renseignements d'identité selon les normes RGPD et CDP Sénégal.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SeneauDeepBlue,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Error message if any
        if (formError != null) {
            item {
                Text(
                    text = formError ?: "",
                    color = SeneauError,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Submit Button
        item {
            Button(
                onClick = {
                    if (applicantName.isBlank() || phoneNumber.isBlank() || city.isBlank()) {
                        formError = if (lang == AppLanguage.WO) "Fattalal sa tur, telefon ak dëkk bi." else "Veuillez renseigner votre nom, téléphone et commune."
                        return@Button
                    }
                    formError = null
                    isSubmitting = true
                    val dist = distanceMeters.toIntOrNull() ?: 8
                    onSubmitRequest(
                        applicantName, nationalId, phoneNumber, selectedUsage.name,
                        selectedRegion, city, district, plotNumber, selectedDiameter,
                        dist, hasNearbyNetwork, neighborMeterRef
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_request_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SeneauDeepBlue),
                shape = RoundedCornerShape(14.dp),
                enabled = !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (lang == AppLanguage.WO) "Yebal sama dosiye leegi" else "Soumettre ma demande de branchement",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
