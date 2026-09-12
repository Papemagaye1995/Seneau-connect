package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppLanguage
import com.example.data.model.PaymentMethod
import com.example.ui.AppScreen
import com.example.ui.SeneauViewModel
import com.example.ui.components.BiometricAuthDialog
import com.example.ui.components.SeneauBottomNavigation
import com.example.ui.components.SeneauHeader
import com.example.ui.components.SmsPushBanner
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SeneauViewModel = viewModel()
            val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
            val isDarkMode = userAccount?.isDarkMode ?: false

            MyApplicationTheme(darkTheme = isDarkMode) {
                SeneauApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SeneauApp(viewModel: SeneauViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
    val requests by viewModel.requests.collectAsStateWithLifecycle()
    val selectedRequest by viewModel.selectedRequest.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val smsList by viewModel.smsList.collectAsStateWithLifecycle()
    val unreadSmsCount by viewModel.unreadSmsCount.collectAsStateWithLifecycle()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsStateWithLifecycle()
    val incomingSms by viewModel.incomingSmsBanner.collectAsStateWithLifecycle()
    val isBiometricOpen by viewModel.isBiometricPromptVisible.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val lang = if (userAccount?.preferredLanguage == "WO") AppLanguage.WO else AppLanguage.FR

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            SeneauHeader(
                currentScreen = currentScreen,
                userAccount = userAccount,
                unreadSms = unreadSmsCount,
                lang = lang,
                onNavigate = { viewModel.navigateTo(it) },
                onToggleLang = { viewModel.toggleLanguage() },
                onToggleDarkMode = { viewModel.toggleDarkMode() },
                onSyncCloud = { viewModel.syncCloud() }
            )
        },
        bottomBar = {
            SeneauBottomNavigation(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) },
                lang = lang
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen router
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        userAccount = userAccount,
                        requests = requests,
                        isBalanceHidden = isBalanceHidden,
                        lang = lang,
                        onToggleHideBalance = { viewModel.toggleHideBalance() },
                        onNavigate = { viewModel.navigateTo(it) },
                        onSelectRequest = { viewModel.selectRequestForTracking(it) },
                        onQuickRecharge = { viewModel.navigateTo(AppScreen.PAYMENT) }
                    )
                }
                AppScreen.NEW_REQUEST -> {
                    NewRequestScreen(
                        userAccount = userAccount,
                        lang = lang,
                        onSubmitRequest = { name, cni, phone, usage, reg, city, dist, plot, diam, distM, net, neigh ->
                            viewModel.submitNewRequest(
                                applicantName = name,
                                nationalId = cni,
                                phone = phone,
                                usageType = usage,
                                region = reg,
                                city = city,
                                district = dist,
                                plotNumber = plot,
                                pipeDiameter = diam,
                                distanceMeters = distM,
                                hasNearbyNetwork = net,
                                neighborMeterRef = neigh
                            )
                        },
                        onBackToHome = { viewModel.navigateTo(AppScreen.HOME) }
                    )
                }
                AppScreen.TRACK_REQUEST -> {
                    TrackRequestScreen(
                        requests = requests,
                        selectedRequest = selectedRequest,
                        lang = lang,
                        onSelectRequest = { viewModel.selectRequestForTracking(it) },
                        onAdvanceStage = { viewModel.advanceSelectedRequestStage() },
                        onPayQuote = { req ->
                            viewModel.navigateTo(AppScreen.PAYMENT)
                        },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
                AppScreen.PAYMENT -> {
                    PaymentScreen(
                        userAccount = userAccount,
                        requests = requests,
                        lang = lang,
                        onPayQuote = { req, method, payFromBalance ->
                            viewModel.payQuote(req, method, payFromBalance)
                        },
                        onRechargeWallet = { amount, method ->
                            viewModel.rechargeWallet(amount, method)
                        }
                    )
                }
                AppScreen.TRANSACTIONS -> {
                    TransactionHistoryScreen(
                        transactions = transactions,
                        lang = lang,
                        onExportCsv = { ctx ->
                            viewModel.exportTransactionsCsv(ctx)
                        }
                    )
                }
                AppScreen.SMS_NOTIFICATIONS -> {
                    SmsCenterScreen(
                        smsList = smsList,
                        lang = lang,
                        onMarkAllRead = { viewModel.markAllSmsRead() }
                    )
                }
                AppScreen.SECURITY_SETTINGS -> {
                    SecuritySettingsScreen(
                        userAccount = userAccount,
                        lang = lang,
                        onToggleBiometric = { viewModel.toggleBiometric() },
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onToggleLang = { viewModel.toggleLanguage() },
                        onSyncCloud = { viewModel.syncCloud() }
                    )
                }
            }

            // Real-time SMS incoming toast banner overlay
            incomingSms?.let { sms ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                ) {
                    SmsPushBanner(
                        sms = sms,
                        lang = lang,
                        onDismiss = { viewModel.dismissSmsBanner() },
                        onViewInbox = {
                            viewModel.dismissSmsBanner()
                            viewModel.navigateTo(AppScreen.SMS_NOTIFICATIONS)
                        }
                    )
                }
            }

            // Biometric Authentication Dialog overlay
            BiometricAuthDialog(
                isOpen = isBiometricOpen,
                lang = lang,
                onSuccess = { viewModel.confirmBiometricSuccess() },
                onDismiss = { viewModel.dismissBiometricPrompt() }
            )
        }
    }
}

// Keep Greeting for GreetingScreenshotTest backward compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
