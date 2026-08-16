package com.example.authenticatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.authenticatorapp.data.local.AccountStorage
import com.example.authenticatorapp.domain.models.Account
import com.example.authenticatorapp.ui.navigation.BottomNavItem
import com.example.authenticatorapp.ui.screen.AccountsScreen
import com.example.authenticatorapp.ui.screen.AddScreen
import com.example.authenticatorapp.ui.screen.QrScannerScreen
import com.example.authenticatorapp.ui.screen.SettingsScreen
import com.example.authenticatorapp.ui.theme.AuthenticatorAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
class MainActivity : ComponentActivity() {

    private lateinit var accountStorage: AccountStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var isReady by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isReady }

        accountStorage = AccountStorage(this)

        lifecycleScope.launch {
            delay(500)
            isReady = true
        }

        super.onCreate(savedInstanceState)

        setContent {
            AuthenticatorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthenticatorApp()
                }
            }
        }
    }

    @Composable
    fun AuthenticatorApp() {
        var accounts by remember { mutableStateOf<List<Account>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        // Загружаем аккаунты из DataStore
        LaunchedEffect(Unit) {
            accountStorage.getAccounts().collect { loadedAccounts ->
                accounts = loadedAccounts
                isLoading = false
            }
        }

        fun addAccount(account: Account) {
            val newAccount = account.copy(
                id = accounts.maxOfOrNull { it.id }?.plus(1) ?: 1
            )
            val newList = accounts + newAccount
            accounts = newList
            lifecycleScope.launch {
                accountStorage.saveAccounts(newList)
            }
        }

        fun deleteAccount(account: Account) {
            val newList = accounts.filter { it.id != account.id }
            accounts = newList
            lifecycleScope.launch {
                accountStorage.saveAccounts(newList)
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            MainScreen(
                accounts = accounts,
                onAddAccount = { addAccount(it) },
                onDeleteAccount = { deleteAccount(it) }
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    accounts: List<Account>,
    onAddAccount: (Account) -> Unit,
    onDeleteAccount: (Account) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showQrScanner by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem.Accounts,
        BottomNavItem.Add,
        BottomNavItem.Settings
    )

    if (showQrScanner) {
        QrScannerScreen(
            onQrScanned = { qrData ->
                val parsed = parseOtpAuthUri(qrData)
                if (parsed != null) {
                    onAddAccount(parsed)
                    showQrScanner = false
                    selectedTab = 0
                }
            },
            onBack = { showQrScanner = false }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.height(64.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    bottomNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (selectedTab == index)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                                )
                            },
                            label = {
                                Text(
                                    item.title,
                                    color = if (selectedTab == index)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                                )
                            },
                            alwaysShowLabel = true
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        slideInVertically(
                            initialOffsetY = { if (targetState > initialState) it else -it },
                            animationSpec = tween(300)
                        ) togetherWith slideOutVertically(
                            targetOffsetY = { if (targetState > initialState) -it else it },
                            animationSpec = tween(300)
                        )
                    }
                ) { tabIndex ->
                    when (tabIndex) {
                        0 -> AccountsScreen(
                            accounts = accounts,
                            onAddAccount = { selectedTab = 1 },
                            onDeleteAccount = onDeleteAccount
                        )
                        1 -> AddScreen(
                            onAddAccount = onAddAccount,
                            onBack = { selectedTab = 0 },
                            onOpenQrScanner = { showQrScanner = true }
                        )
                        2 -> SettingsScreen()
                    }
                }
            }
        }
    }
}

private fun parseOtpAuthUri(uri: String): Account? {
    try {
        if (!uri.startsWith("otpauth://totp/")) {
            return null
        }

        val withoutPrefix = uri.removePrefix("otpauth://totp/")

        val parts = withoutPrefix.split("?")
        if (parts.size != 2) return null

        val servicePart = parts[0]
        val paramsPart = parts[1]

        val serviceSplit = servicePart.split(":")
        val service = if (serviceSplit.size > 1) {
            serviceSplit[0]
        } else {
            servicePart.split("/").firstOrNull() ?: "Unknown"
        }

        val username = if (serviceSplit.size > 1) {
            serviceSplit[1]
        } else {
            servicePart.split("/").lastOrNull() ?: ""
        }

        val params = paramsPart.split("&")
        var secret: String? = null
        var issuer: String? = null

        for (param in params) {
            val keyValue = param.split("=")
            if (keyValue.size == 2) {
                when (keyValue[0]) {
                    "secret" -> secret = keyValue[1]
                    "issuer" -> issuer = keyValue[1]
                }
            }
        }

        if (secret == null) return null

        val finalService = issuer ?: service

        return Account(
            service = finalService,
            username = username.ifBlank { finalService },
            secret = secret
        )
    } catch (e: Exception) {
        return null
    }
}