package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.SeneauDatabase
import com.example.data.model.AppLanguage
import com.example.data.model.ConnectionRequestEntity
import com.example.data.model.PaymentMethod
import com.example.data.model.RequestStatus
import com.example.data.model.SmsNotificationEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.UserAccountEntity
import com.example.data.repository.SeneauRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    NEW_REQUEST,
    TRACK_REQUEST,
    PAYMENT,
    TRANSACTIONS,
    SMS_NOTIFICATIONS,
    SECURITY_SETTINGS
}

class SeneauViewModel(application: Application) : AndroidViewModel(application) {
    private val database = SeneauDatabase.getDatabase(application)
    val repository = SeneauRepository(database)

    val requests: StateFlow<List<ConnectionRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val smsList: StateFlow<List<SmsNotificationEntity>> = repository.allSms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadSmsCount: StateFlow<Int> = repository.unreadSmsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val userAccount: StateFlow<UserAccountEntity?> = repository.userAccount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedRequest = MutableStateFlow<ConnectionRequestEntity?>(null)
    val selectedRequest: StateFlow<ConnectionRequestEntity?> = _selectedRequest.asStateFlow()

    private val _isBalanceHidden = MutableStateFlow(false)
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private val _incomingSmsBanner = MutableStateFlow<SmsNotificationEntity?>(null)
    val incomingSmsBanner: StateFlow<SmsNotificationEntity?> = _incomingSmsBanner.asStateFlow()

    private val _isBiometricPromptVisible = MutableStateFlow(false)
    val isBiometricPromptVisible: StateFlow<Boolean> = _isBiometricPromptVisible.asStateFlow()

    private var pendingAction: (() -> Unit)? = null

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectRequestForTracking(request: ConnectionRequestEntity) {
        _selectedRequest.value = request
        _currentScreen.value = AppScreen.TRACK_REQUEST
    }

    fun toggleHideBalance() {
        _isBalanceHidden.value = !_isBalanceHidden.value
    }

    fun dismissSmsBanner() {
        _incomingSmsBanner.value = null
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    fun requestSecureAction(action: () -> Unit) {
        val account = userAccount.value
        if (account?.isBiometricEnabled == true) {
            pendingAction = action
            _isBiometricPromptVisible.value = true
        } else {
            action()
        }
    }

    fun confirmBiometricSuccess() {
        _isBiometricPromptVisible.value = false
        triggerVibration()
        val action = pendingAction
        pendingAction = null
        action?.invoke()
    }

    fun dismissBiometricPrompt() {
        _isBiometricPromptVisible.value = false
        pendingAction = null
    }

    fun submitNewRequest(
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
    ) {
        viewModelScope.launch {
            val newReq = repository.submitNewRequest(
                applicantName, nationalId, phone, usageType, region,
                city, district, plotNumber, pipeDiameter, distanceMeters,
                hasNearbyNetwork, neighborMeterRef
            )
            triggerVibration()
            _selectedRequest.value = newReq
            _currentScreen.value = AppScreen.TRACK_REQUEST

            // Show real-time SMS pop-up banner
            val lang = if (userAccount.value?.preferredLanguage == "WO") AppLanguage.WO else AppLanguage.FR
            val smsText = if (lang == AppLanguage.WO) {
                "SEN'EAU : Sa dosiye ${newReq.fileNumber} yeb nañu ko. Mën nga ko toppte leegi."
            } else {
                "SEN'EAU : Votre demande ${newReq.fileNumber} est enregistrée. Suivez-la en temps réel."
            }

            _incomingSmsBanner.value = SmsNotificationEntity(
                sender = "SEN'EAU",
                recipientPhone = phone,
                messageFr = smsText,
                messageWo = smsText,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    fun advanceSelectedRequestStage() {
        val current = _selectedRequest.value ?: requests.value.firstOrNull() ?: return
        viewModelScope.launch {
            val nextStatus = repository.advanceRequestStage(current.id)
            triggerVibration()
            // reload selected request
            val updated = requests.value.find { it.id == current.id }
            if (updated != null) {
                _selectedRequest.value = updated
            }
            if (nextStatus != null) {
                _incomingSmsBanner.value = SmsNotificationEntity(
                    sender = "SEN'EAU",
                    recipientPhone = current.phoneNumber,
                    messageFr = "SEN'EAU : Dossier ${current.fileNumber} mis à jour -> ${nextStatus.titleFr}",
                    messageWo = "SEN'EAU : Dosiye ${current.fileNumber} yeesal nañu ko -> ${nextStatus.titleWo}",
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }

    fun payQuote(request: ConnectionRequestEntity, paymentMethod: PaymentMethod, payFromBalance: Boolean) {
        requestSecureAction {
            viewModelScope.launch {
                val success = repository.payQuoteOrBill(
                    amount = request.estimatedCost,
                    paymentMethod = paymentMethod.name,
                    type = TransactionType.PAIEMENT_DEVIS,
                    relatedFileNumber = request.fileNumber,
                    description = "Devis raccordement eau (${request.fileNumber})",
                    payFromBalance = payFromBalance
                )
                if (success) {
                    triggerVibration()
                    _toastMessage.value = "Paiement de ${repository.formatCurrency(request.estimatedCost)} validé avec succès !"
                    // Show incoming SMS banner
                    val account = userAccount.value
                    _incomingSmsBanner.value = SmsNotificationEntity(
                        sender = "SEN'EAU",
                        recipientPhone = account?.phone ?: "+221 77 645 89 20",
                        messageFr = "SEN'EAU : Paiement devis de ${repository.formatCurrency(request.estimatedCost)} confirme via ${paymentMethod.displayName}.",
                        messageWo = "SEN'EAU : Fey saytu ${repository.formatCurrency(request.estimatedCost)} ci ${paymentMethod.displayName} jàll na.",
                        timestamp = System.currentTimeMillis(),
                        movementAmount = -request.estimatedCost
                    )
                    // Refresh selected
                    val updated = requests.value.find { it.id == request.id }
                    if (updated != null) _selectedRequest.value = updated
                } else {
                    _toastMessage.value = "Solde insuffisant sur votre compte usager."
                }
            }
        }
    }

    fun rechargeWallet(amount: Double, paymentMethod: PaymentMethod) {
        requestSecureAction {
            viewModelScope.launch {
                repository.rechargeBalance(amount, paymentMethod.name)
                triggerVibration()
                _toastMessage.value = "Recharge de ${repository.formatCurrency(amount)} confirmée !"
                val account = userAccount.value
                _incomingSmsBanner.value = SmsNotificationEntity(
                    sender = "SEN'EAU",
                    recipientPhone = account?.phone ?: "+221 77 645 89 20",
                    messageFr = "SEN'EAU : Solde crédité de ${repository.formatCurrency(amount)} via ${paymentMethod.displayName}.",
                    messageWo = "SEN'EAU : Sa kàmp yokku na ${repository.formatCurrency(amount)} ci ${paymentMethod.displayName}.",
                    timestamp = System.currentTimeMillis(),
                    movementAmount = amount
                )
            }
        }
    }

    fun exportTransactionsCsv(context: Context) {
        viewModelScope.launch {
            val csvContent = repository.generateCsvData()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csvContent)
                putExtra(Intent.EXTRA_TITLE, "seneau_transactions_export.csv")
                type = "text/csv"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Exporter relevé des transactions SEN'EAU")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
            _toastMessage.value = "Export CSV prêt pour le partage !"
        }
    }

    fun toggleLanguage() {
        val current = userAccount.value?.preferredLanguage ?: "FR"
        val newLang = if (current == "FR") "WO" else "FR"
        viewModelScope.launch {
            repository.setLanguage(newLang)
        }
    }

    fun toggleDarkMode() {
        val current = userAccount.value?.isDarkMode ?: false
        viewModelScope.launch {
            repository.setDarkMode(!current)
        }
    }

    fun toggleBiometric() {
        val current = userAccount.value?.isBiometricEnabled ?: true
        viewModelScope.launch {
            repository.setBiometric(!current)
            _toastMessage.value = if (!current) "Sécurité biométrique activée" else "Sécurité biométrique désactivée"
        }
    }

    fun syncCloud() {
        viewModelScope.launch {
            repository.syncCloud()
            triggerVibration()
            _toastMessage.value = "Données synchronisées avec le serveur sécurisé SEN'EAU."
        }
    }

    fun markAllSmsRead() {
        viewModelScope.launch {
            repository.markAllSmsRead()
        }
    }
}
