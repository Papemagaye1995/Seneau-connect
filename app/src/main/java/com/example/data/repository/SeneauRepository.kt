package com.example.data.repository

import com.example.data.db.SeneauDatabase
import com.example.data.model.ConnectionRequestEntity
import com.example.data.model.RequestStatus
import com.example.data.model.SmsNotificationEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class SeneauRepository(private val database: SeneauDatabase) {
    private val requestDao = database.connectionRequestDao()
    private val transactionDao = database.transactionDao()
    private val smsDao = database.smsNotificationDao()
    private val accountDao = database.userAccountDao()

    val allRequests: Flow<List<ConnectionRequestEntity>> = requestDao.getAllRequests()
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allSms: Flow<List<SmsNotificationEntity>> = smsDao.getAllSms()
    val unreadSmsCount: Flow<Int> = smsDao.getUnreadCount()
    val userAccount: Flow<UserAccountEntity?> = accountDao.getUserAccount()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
        }
    }

    private suspend fun seedInitialDataIfEmpty() {
        val account = accountDao.getUserAccountDirect()
        if (account == null) {
            accountDao.insertOrUpdate(
                UserAccountEntity(
                    id = 1,
                    fullName = "Mamadou Lamine Diop",
                    phone = "+221 77 645 89 20",
                    nationalId = "1 756 1992 04512",
                    email = "m.diop@seneau-client.sn",
                    balance = 185000.0,
                    monthlyBudgetLimit = 60000.0,
                    estimatedMonthlyM3 = 19.4,
                    isBiometricEnabled = true,
                    pinCode = "1234",
                    preferredLanguage = "FR",
                    isDarkMode = false,
                    lastCloudSync = System.currentTimeMillis() - 3600000,
                    isOfflineMode = false
                )
            )

            // Initial connection request in progress
            val initialRequest = ConnectionRequestEntity(
                id = 1,
                fileNumber = "SN-DKR-2026-08412",
                applicantName = "Mamadou Lamine Diop",
                nationalId = "1 756 1992 04512",
                phoneNumber = "+221 77 645 89 20",
                usageType = "DOMESTIQUE",
                region = "Dakar",
                city = "Guédiawaye",
                district = "Hamo 4, Villa N° 124",
                plotNumber = "TF 1845/R",
                pipeDiameter = "15mm",
                distanceToNetworkMeters = 8,
                hasNearbyNetwork = true,
                neighborMeterRef = "CPT-DKR-774921",
                status = RequestStatus.DEVIS_DISPONIBLE.name,
                progressPercentage = 55,
                estimatedCost = 94500.0,
                isQuotePaid = false,
                createdAt = System.currentTimeMillis() - (4 * 86400000L),
                lastUpdatedAt = System.currentTimeMillis() - (12 * 3600000L),
                agentNotes = "Métré validé par l'agent technique Amadou Sow. Distance 8m du réseau de distribution DN100. Devis prêt pour règlement.",
                assignedAgent = "Amadou Sow (Chef secteur Dakar-Nord)",
                encryptedHash = generateEncryptedHash("SN-DKR-2026-08412" + System.currentTimeMillis())
            )
            requestDao.insertRequest(initialRequest)

            // Initial Transactions
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = 1,
                    reference = "TRX-WAVE-88412",
                    type = TransactionType.RECHARGE_SOLDE.name,
                    title = "Recharge Wave Portefeuille",
                    description = "Dépôt compte usager SEN'EAU Connect",
                    amount = 150000.0,
                    isCredit = true,
                    paymentMethod = "WAVE",
                    timestamp = System.currentTimeMillis() - (3 * 86400000L),
                    status = "CONFIRME"
                )
            )
            transactionDao.insertTransaction(
                TransactionEntity(
                    id = 2,
                    reference = "TRX-OM-55190",
                    type = TransactionType.FRAIS_DOSSIER.name,
                    title = "Frais de dossier administratif",
                    description = "Dossier N° SN-DKR-2026-08412",
                    amount = 10000.0,
                    isCredit = false,
                    paymentMethod = "ORANGE_MONEY",
                    timestamp = System.currentTimeMillis() - (4 * 86400000L),
                    status = "CONFIRME",
                    relatedFileNumber = "SN-DKR-2026-08412"
                )
            )

            // Initial SMS
            smsDao.insertSms(
                SmsNotificationEntity(
                    id = 1,
                    sender = "SEN'EAU",
                    recipientPhone = "+221 77 645 89 20",
                    messageFr = "SEN'EAU : Votre recharge de 150 000 FCFA par Wave est confirmee. Nouveau solde : 185 000 FCFA. Suivez votre budget en temps reel sur l'application.",
                    messageWo = "SEN'EAU : Sa doolilu 150 000 FCFA ci Wave baax na. Li nga am leegi : 185 000 FCFA ci sa kàmp SEN'EAU.",
                    timestamp = System.currentTimeMillis() - (3 * 86400000L),
                    isRead = true,
                    movementAmount = 150000.0,
                    newBalance = 185000.0
                )
            )
            smsDao.insertSms(
                SmsNotificationEntity(
                    id = 2,
                    sender = "SEN'EAU",
                    recipientPhone = "+221 77 645 89 20",
                    messageFr = "SEN'EAU INFO : Devis disponible pour votre demande de branchement SN-DKR-2026-08412 d'un montant de 94 500 FCFA. Reglez directement en ligne.",
                    messageWo = "SEN'EAU INFO : Saytu koppar bi génn na ngir sa dosiye SN-DKR-2026-08412, mu doon 94 500 FCFA. Mën nga ko fey ci lëkkalekaay bi.",
                    timestamp = System.currentTimeMillis() - (12 * 3600000L),
                    isRead = false
                )
            )
        }
    }

    suspend fun submitNewRequest(
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
    ): ConnectionRequestEntity = withContext(Dispatchers.IO) {
        val randomSuffix = (10000..99999).random()
        val regCode = when (region.lowercase()) {
            "dakar" -> "DKR"
            "thiès", "thies" -> "THS"
            "saint-louis" -> "STL"
            "kaolack" -> "KLC"
            "ziguinchor" -> "ZIG"
            else -> "SEN"
        }
        val fileNumber = "SN-$regCode-2026-$randomSuffix"

        // Base estimated cost based on diameter and distance
        val baseDiameterCost = when (pipeDiameter) {
            "15mm" -> 75000.0
            "20mm" -> 95000.0
            "25mm" -> 130000.0
            else -> 80000.0
        }
        val distanceCost = distanceMeters * 3500.0
        val estimatedTotal = baseDiameterCost + distanceCost

        val newRequest = ConnectionRequestEntity(
            fileNumber = fileNumber,
            applicantName = applicantName,
            nationalId = nationalId,
            phoneNumber = phone,
            usageType = usageType,
            region = region,
            city = city,
            district = district,
            plotNumber = plotNumber,
            pipeDiameter = pipeDiameter,
            distanceToNetworkMeters = distanceMeters,
            hasNearbyNetwork = hasNearbyNetwork,
            neighborMeterRef = neighborMeterRef,
            status = RequestStatus.DEPOT.name,
            progressPercentage = 15,
            estimatedCost = estimatedTotal,
            isQuotePaid = false,
            createdAt = System.currentTimeMillis(),
            lastUpdatedAt = System.currentTimeMillis(),
            agentNotes = "Demande enregistrée en ligne via SEN'EAU Connect. Dossier conforme aux normes RGPD et transmis au service technique régional.",
            assignedAgent = "Cellule Raccordement $region",
            encryptedHash = generateEncryptedHash(fileNumber + applicantName + System.currentTimeMillis())
        )

        val insertedId = requestDao.insertRequest(newRequest)

        // Send SMS Notification
        val smsFr = "SEN'EAU : Votre demande de branchement $fileNumber est enregistree avec succes. Vous pouvez suivre l'evolution en temps reel sur votre appli."
        val smsWo = "SEN'EAU : Sa dosiye lëkkalekaay $fileNumber yeb nañu ko ci jàmm. Mën nga ko toppte saa su nekk ci aplikasiyoŋ bi."

        smsDao.insertSms(
            SmsNotificationEntity(
                sender = "SEN'EAU",
                recipientPhone = phone,
                messageFr = smsFr,
                messageWo = smsWo,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )

        newRequest.copy(id = insertedId)
    }

    suspend fun advanceRequestStage(requestId: Long): RequestStatus? = withContext(Dispatchers.IO) {
        val current = requestDao.getAllRequests().firstOrNull()?.find { it.id == requestId } ?: return@withContext null
        val currentStatus = try { RequestStatus.valueOf(current.status) } catch (_: Exception) { RequestStatus.DEPOT }

        val nextStatus = when (currentStatus) {
            RequestStatus.DEPOT -> RequestStatus.ETUDE_TECHNIQUE
            RequestStatus.ETUDE_TECHNIQUE -> RequestStatus.DEVIS_DISPONIBLE
            RequestStatus.DEVIS_DISPONIBLE -> RequestStatus.PAIEMENT_VALIDE
            RequestStatus.PAIEMENT_VALIDE -> RequestStatus.TRAVAUX_EN_COURS
            RequestStatus.TRAVAUX_EN_COURS -> RequestStatus.COMPTEUR_POSE
            RequestStatus.COMPTEUR_POSE -> RequestStatus.COMPTEUR_POSE
        }

        val percentage = when (nextStatus) {
            RequestStatus.DEPOT -> 15
            RequestStatus.ETUDE_TECHNIQUE -> 35
            RequestStatus.DEVIS_DISPONIBLE -> 55
            RequestStatus.PAIEMENT_VALIDE -> 75
            RequestStatus.TRAVAUX_EN_COURS -> 90
            RequestStatus.COMPTEUR_POSE -> 100
        }

        val notes = when (nextStatus) {
            RequestStatus.ETUDE_TECHNIQUE -> "Visite technique planifiée. L'agent effectue le métré et vérifie le réseau."
            RequestStatus.DEVIS_DISPONIBLE -> "Devis établi : ${formatCurrency(current.estimatedCost)}. Prêt pour paiement."
            RequestStatus.PAIEMENT_VALIDE -> "Paiement validé avec succès. Ordre de travaux émis."
            RequestStatus.TRAVAUX_EN_COURS -> "Équipe sur le terrain pour le terrassement et la pose du tuyau."
            RequestStatus.COMPTEUR_POSE -> "Compteur posé et scellé. Eau potable ouverte. Bienvenue chez SEN'EAU !"
            RequestStatus.DEPOT -> current.agentNotes
        }

        val updated = current.copy(
            status = nextStatus.name,
            progressPercentage = percentage,
            isQuotePaid = (nextStatus == RequestStatus.PAIEMENT_VALIDE || nextStatus == RequestStatus.TRAVAUX_EN_COURS || nextStatus == RequestStatus.COMPTEUR_POSE),
            lastUpdatedAt = System.currentTimeMillis(),
            agentNotes = notes
        )
        requestDao.updateRequest(updated)

        // Dispatch SMS
        val smsFr = "SEN'EAU : Dossier ${current.fileNumber} mis a jour -> ${nextStatus.titleFr}. Consultez votre suivi en temps reel."
        val smsWo = "SEN'EAU : Dosiye ${current.fileNumber} yeesal nañu ko -> ${nextStatus.titleWo}. Xoolal ci sa kàmp bi."
        smsDao.insertSms(
            SmsNotificationEntity(
                sender = "SEN'EAU",
                recipientPhone = current.phoneNumber,
                messageFr = smsFr,
                messageWo = smsWo,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )

        nextStatus
    }

    suspend fun payQuoteOrBill(
        amount: Double,
        paymentMethod: String,
        type: TransactionType,
        relatedFileNumber: String?,
        description: String,
        payFromBalance: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        val account = accountDao.getUserAccountDirect() ?: return@withContext false

        var updatedBalance = account.balance
        if (payFromBalance) {
            if (account.balance < amount) {
                return@withContext false
            }
            updatedBalance -= amount
            accountDao.updateBalance(updatedBalance)
        }

        val refCode = "TRX-${paymentMethod.take(4)}-${(10000..99999).random()}"
        val transaction = TransactionEntity(
            reference = refCode,
            type = type.name,
            title = type.labelFr,
            description = description,
            amount = amount,
            isCredit = false,
            paymentMethod = paymentMethod,
            timestamp = System.currentTimeMillis(),
            status = "CONFIRME",
            relatedFileNumber = relatedFileNumber
        )
        transactionDao.insertTransaction(transaction)

        // If it's a quote payment for a connection request, update the request status
        if (relatedFileNumber != null && type == TransactionType.PAIEMENT_DEVIS) {
            val req = requestDao.getRequestByFileNumber(relatedFileNumber)
            if (req != null) {
                requestDao.updateRequest(
                    req.copy(
                        status = RequestStatus.PAIEMENT_VALIDE.name,
                        progressPercentage = 75,
                        isQuotePaid = true,
                        lastUpdatedAt = System.currentTimeMillis(),
                        agentNotes = "Paiement de ${formatCurrency(amount)} reçu par $paymentMethod (Ref: $refCode). Ordre de travaux généré."
                    )
                )
            }
        }

        // Send real-time SMS notification for balance movement
        val smsFr = if (payFromBalance) {
            "SEN'EAU : Paiement de ${formatCurrency(amount)} effectue avec succes ($description). Nouveau solde : ${formatCurrency(updatedBalance)}. Ref: $refCode."
        } else {
            "SEN'EAU : Paiement de ${formatCurrency(amount)} par $paymentMethod confirme pour $description. Ref: $refCode. Merci de votre confiance."
        }

        val smsWo = if (payFromBalance) {
            "SEN'EAU : Fey ${formatCurrency(amount)} baax na ($description). Li des ci sa kàmp : ${formatCurrency(updatedBalance)}. Ref: $refCode."
        } else {
            "SEN'EAU : Fey ${formatCurrency(amount)} ci $paymentMethod jàll na ngir $description. Ref: $refCode. Jërëjëf."
        }

        smsDao.insertSms(
            SmsNotificationEntity(
                sender = "SEN'EAU",
                recipientPhone = account.phone,
                messageFr = smsFr,
                messageWo = smsWo,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                movementAmount = -amount,
                newBalance = if (payFromBalance) updatedBalance else account.balance
            )
        )

        true
    }

    suspend fun rechargeBalance(amount: Double, paymentMethod: String): Boolean = withContext(Dispatchers.IO) {
        val account = accountDao.getUserAccountDirect() ?: return@withContext false
        val newBalance = account.balance + amount
        accountDao.updateBalance(newBalance)

        val refCode = "RCH-${paymentMethod.take(4)}-${(10000..99999).random()}"
        val transaction = TransactionEntity(
            reference = refCode,
            type = TransactionType.RECHARGE_SOLDE.name,
            title = TransactionType.RECHARGE_SOLDE.labelFr,
            description = "Recharge solde usager via $paymentMethod",
            amount = amount,
            isCredit = true,
            paymentMethod = paymentMethod,
            timestamp = System.currentTimeMillis(),
            status = "CONFIRME"
        )
        transactionDao.insertTransaction(transaction)

        val smsFr = "SEN'EAU : Votre solde a ete credite de ${formatCurrency(amount)} via $paymentMethod. Nouveau solde disponible : ${formatCurrency(newBalance)}. Ref: $refCode."
        val smsWo = "SEN'EAU : Sa kàmp yokku na ${formatCurrency(amount)} ci $paymentMethod. Li nga am leegi : ${formatCurrency(newBalance)}. Ref: $refCode."

        smsDao.insertSms(
            SmsNotificationEntity(
                sender = "SEN'EAU",
                recipientPhone = account.phone,
                messageFr = smsFr,
                messageWo = smsWo,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                movementAmount = amount,
                newBalance = newBalance
            )
        )

        true
    }

    suspend fun markAllSmsRead() = withContext(Dispatchers.IO) {
        smsDao.markAllAsRead()
    }

    suspend fun setLanguage(lang: String) = withContext(Dispatchers.IO) {
        accountDao.updateLanguage(lang)
    }

    suspend fun setDarkMode(darkMode: Boolean) = withContext(Dispatchers.IO) {
        accountDao.updateDarkMode(darkMode)
    }

    suspend fun setBiometric(enabled: Boolean) = withContext(Dispatchers.IO) {
        accountDao.updateBiometric(enabled)
    }

    suspend fun syncCloud(): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        accountDao.updateCloudSync(now)
        now
    }

    suspend fun generateCsvData(): String = withContext(Dispatchers.IO) {
        val transactions = transactionDao.getAllTransactions().firstOrNull().orEmpty()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
        val sb = StringBuilder()
        sb.append("ID;Date;Reference;Type;Description;Montant_FCFA;Sens;Moyen_Paiement;Statut;Dossier_Associe\n")
        transactions.forEach { t ->
            val dateStr = dateFormat.format(Date(t.timestamp))
            val sens = if (t.isCredit) "CREDIT" else "DEBIT"
            sb.append("${t.id};\"$dateStr\";\"${t.reference}\";\"${t.type}\";\"${t.description.replace("\"", "'")}\";${t.amount};$sens;\"${t.paymentMethod}\";\"${t.status}\";\"${t.relatedFileNumber ?: ""}\"\n")
        }
        sb.toString()
    }

    private fun generateEncryptedHash(data: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(data.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getIntegerInstance(Locale.FRANCE)
        return "${format.format(amount.toLong())} FCFA"
    }
}
