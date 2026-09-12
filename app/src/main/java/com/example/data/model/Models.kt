package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UsageType(val labelFr: String, val labelWo: String) {
    DOMESTIQUE("Domestique (Ménage)", "Kër (Keur)"),
    COMMERCIAL("Commercial / Boutique", "Koom-koom"),
    INDUSTRIEL("Industriel", "Usine / Dëjji"),
    SOCIAL("Social / Borne-fontaine", "Doxeentu / Mbootaay")
}

enum class RequestStatus(val stage: Int, val titleFr: String, val titleWo: String, val descriptionFr: String) {
    DEPOT(1, "Dépôt de la demande", "Yebal dosiye bi", "Dossier enregistré et pièces justificatives validées."),
    ETUDE_TECHNIQUE(2, "Étude technique", "Saytu gu xarala", "Visite sur site effectuée par l'agent technique SEN'EAU."),
    DEVIS_DISPONIBLE(3, "Devis disponible", "Saytu koppar bi génn na", "Métré validé et devis de raccordement chiffré."),
    PAIEMENT_VALIDE(4, "Paiement validé", "Pey bi baax na", "Montant du devis acquitté par le client."),
    TRAVAUX_EN_COURS(5, "Travaux en cours", "Liggéey bi mi ngi dox", "Terrassement et pose de la conduite de raccordement."),
    COMPTEUR_POSE(6, "Compteur posé & Actif", "Kompteer bi samp nañu ko", "Compteur installé, eau potable disponible et contrat actif.")
}

enum class PaymentMethod(val displayName: String, val logoColorHex: Long) {
    WAVE("Wave", 0xFF1DA1F2),
    ORANGE_MONEY("Orange Money", 0xFFFF7900),
    FREE_MONEY("Free Money", 0xFFE60000),
    CARTE_BANCAIRE("Carte Bancaire (GIM)", 0xFF003E8A)
}

enum class TransactionType(val labelFr: String, val labelWo: String) {
    PAIEMENT_DEVIS("Paiement devis branchement", "Fey kopparu lëkkalekaay"),
    RECHARGE_SOLDE("Recharge de solde usager", "Doolil sa koppar"),
    FACTURE_EAU("Paiement facture d'eau", "Fey faktuur ndox"),
    FRAIS_DOSSIER("Frais de dossier administratif", "Frais dosiye")
}

enum class AppLanguage {
    FR,
    WO
}

@Entity(tableName = "connection_requests")
data class ConnectionRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileNumber: String,
    val applicantName: String,
    val nationalId: String,
    val phoneNumber: String,
    val usageType: String,
    val region: String,
    val city: String,
    val district: String,
    val plotNumber: String,
    val pipeDiameter: String,
    val distanceToNetworkMeters: Int,
    val hasNearbyNetwork: Boolean,
    val neighborMeterRef: String,
    val status: String,
    val progressPercentage: Int,
    val estimatedCost: Double,
    val isQuotePaid: Boolean,
    val createdAt: Long,
    val lastUpdatedAt: Long,
    val agentNotes: String,
    val assignedAgent: String,
    val encryptedHash: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reference: String,
    val type: String,
    val title: String,
    val description: String,
    val amount: Double,
    val isCredit: Boolean,
    val paymentMethod: String,
    val timestamp: Long,
    val status: String,
    val relatedFileNumber: String? = null
)

@Entity(tableName = "sms_notifications")
data class SmsNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String = "SEN'EAU",
    val recipientPhone: String,
    val messageFr: String,
    val messageWo: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val movementAmount: Double? = null,
    val newBalance: Double? = null
)

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val phone: String,
    val nationalId: String,
    val email: String,
    val balance: Double,
    val monthlyBudgetLimit: Double,
    val estimatedMonthlyM3: Double,
    val isBiometricEnabled: Boolean,
    val pinCode: String,
    val preferredLanguage: String, // "FR" or "WO"
    val isDarkMode: Boolean,
    val lastCloudSync: Long,
    val isOfflineMode: Boolean
)
