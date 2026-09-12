package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.SmsNotificationEntity
import com.example.data.model.UserAccountEntity
import com.example.ui.AppScreen
import com.example.ui.strings.SeneauStrings
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SeneauHeader(
    currentScreen: AppScreen,
    userAccount: UserAccountEntity?,
    unreadSms: Int,
    lang: AppLanguage,
    onNavigate: (AppScreen) -> Unit,
    onToggleLang: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSyncCloud: () -> Unit
) {
    val isDark = userAccount?.isDarkMode == true

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("seneau_top_app_bar"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onNavigate(AppScreen.HOME) }
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(SeneauDeepBlue, SeneauAqua)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "SEN'EAU Logo",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "SEN'EAU Connect",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(SeneauSuccess)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = SeneauStrings.onlineBadge(lang),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // Action controls: Language, SMS, Dark mode, Cloud
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Bilingual toggle (FR / WO)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(onClick = onToggleLang)
                            .testTag("lang_toggle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == AppLanguage.WO) "Wolof" else "Français",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }

                    // SMS notifications badge
                    IconButton(
                        onClick = { onNavigate(AppScreen.SMS_NOTIFICATIONS) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("sms_notifications_btn")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadSms > 0) {
                                    Badge(
                                        containerColor = SeneauError,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadSms")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (unreadSms > 0) Icons.Default.Sms else Icons.Outlined.Sms,
                                contentDescription = "SMS Inbox",
                                tint = if (unreadSms > 0) SeneauPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Dark mode toggle
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("dark_mode_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = if (isDark) SeneauGold else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Cloud sync
                    IconButton(
                        onClick = onSyncCloud,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("cloud_sync_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sync Cloud",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeneauBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    lang: AppLanguage
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("seneau_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(SeneauStrings.navHome(lang), fontSize = 11.sp) },
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            modifier = Modifier.testTag("nav_item_home")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircleOutline, contentDescription = "New Request") },
            label = { Text(SeneauStrings.navRequest(lang), fontSize = 11.sp) },
            selected = currentScreen == AppScreen.NEW_REQUEST,
            onClick = { onNavigate(AppScreen.NEW_REQUEST) },
            modifier = Modifier.testTag("nav_item_request")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Timeline, contentDescription = "Tracking") },
            label = { Text(SeneauStrings.navTrack(lang), fontSize = 11.sp) },
            selected = currentScreen == AppScreen.TRACK_REQUEST,
            onClick = { onNavigate(AppScreen.TRACK_REQUEST) },
            modifier = Modifier.testTag("nav_item_track")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Payments, contentDescription = "Payment") },
            label = { Text(SeneauStrings.navPay(lang), fontSize = 11.sp) },
            selected = currentScreen == AppScreen.PAYMENT,
            onClick = { onNavigate(AppScreen.PAYMENT) },
            modifier = Modifier.testTag("nav_item_pay")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "History") },
            label = { Text(SeneauStrings.navHistory(lang), fontSize = 11.sp) },
            selected = currentScreen == AppScreen.TRANSACTIONS,
            onClick = { onNavigate(AppScreen.TRANSACTIONS) },
            modifier = Modifier.testTag("nav_item_history")
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Security, contentDescription = "Security") },
            label = { Text(SeneauStrings.navSecurity(lang), fontSize = 11.sp) },
            selected = currentScreen == AppScreen.SECURITY_SETTINGS,
            onClick = { onNavigate(AppScreen.SECURITY_SETTINGS) },
            modifier = Modifier.testTag("nav_item_security")
        )
    }
}

@Composable
fun SmsPushBanner(
    sms: SmsNotificationEntity,
    lang: AppLanguage,
    onDismiss: () -> Unit,
    onViewInbox: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it })
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("sms_incoming_banner"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(SeneauDeepBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = "SMS",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SEN'EAU SMS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = SeneauPrimary
                            )
                        )
                        Text(
                            text = "Maintenant",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (lang == AppLanguage.WO) sms.messageWo else sms.messageFr,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BiometricAuthDialog(
    isOpen: Boolean,
    lang: AppLanguage,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("biometric_auth_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(SeneauSoftBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biométrie",
                        tint = SeneauDeepBlue,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = SeneauStrings.biometricPromptTitle(lang),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = SeneauStrings.biometricPromptSubtitle(lang),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // One-tap Biometric sensor simulate button
                Button(
                    onClick = {
                        errorMessage = null
                        onSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("biometric_fingerprint_confirm_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SeneauDeepBlue
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.WO) "Wéeral sa Baram" else "Scanner l'empreinte biométrique",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = if (lang == AppLanguage.WO) "Walla PIN" else "Ou code PIN",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 4) {
                            pinInput = it
                            if (it.length == 4) {
                                if (it == "1234") {
                                    errorMessage = null
                                    onSuccess()
                                } else {
                                    errorMessage = if (lang == AppLanguage.WO) "PIN bi baaxul (1234)" else "Code PIN incorrect (Code test : 1234)"
                                }
                            }
                        }
                    },
                    label = { Text("Code PIN (4 chiffres)") },
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pin_code_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = SeneauError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("biometric_cancel_btn")
                ) {
                    Text(
                        text = if (lang == AppLanguage.WO) "Neenal" else "Annuler",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun RgpdPrivacySheet(
    isOpen: Boolean,
    lang: AppLanguage,
    onClose: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("rgpd_privacy_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = SeneauSuccess,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Normes RGPD & CDP Sénégal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (lang == AppLanguage.WO)
                        "SEN'EAU Connect mi ngi sàmm say mbir ci kaarange gu wóor (loi sénégalaise sur les données personnelles n° 2008-12 ak RGPD). Mën nga yebal say mbir walla far leen saa su nekk."
                    else
                        "SEN'EAU Connect applique les protocoles de confidentialité les plus stricts conformément à la loi sénégalaise n° 2008-12 et au standard RGPD. Vos données de raccordement et coordonnées bancaires sont chiffrées en AES-256.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Garanties de sécurité appliquées :",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Chiffrement de bout en bout (E2EE)", style = MaterialTheme.typography.bodySmall)
                        Text("• Authentification biométrique renforcée", style = MaterialTheme.typography.bodySmall)
                        Text("• Aucune revente de données usagers", style = MaterialTheme.typography.bodySmall)
                        Text("• Droit d'accès, d'export et d'effacement garanti", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SeneauDeepBlue)
                ) {
                    Text("J'ai compris")
                }
            }
        }
    }
}
