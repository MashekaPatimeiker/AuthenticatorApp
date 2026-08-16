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
import com.example.authenticatorapp.domain.models.Account
import com.example.authenticatorapp.ui.navigation.BottomNavItem
import com.example.authenticatorapp.ui.screen.AccountsScreen
import com.example.authenticatorapp.ui.screen.AddScreen
import com.example.authenticatorapp.ui.screen.SettingsScreen
import com.example.authenticatorapp.ui.theme.AuthenticatorAppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var isReady by mutableStateOf(false)
        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            delay(800)
            isReady = true
        }

        super.onCreate(savedInstanceState)

        val testAccounts = listOf(
            Account(
                id = 1,
                service = "Google",
                username = "test@gmail.com",
                secret = "JBSWY3DPEHPK3PXP"
            ),
            Account(
                id = 2,
                service = "GitHub",
                username = "testuser",
                secret = "JBSWY3DPEHPK3PXP"
            ),
            Account(
                id = 3,
                service = "Telegram",
                username = "@username",
                secret = "JBSWY3DPEHPK3PXP"
            )
        )

        setContent {
            AuthenticatorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        accounts = testAccounts,
                        onAddAccount = { /* Пока ничего */ },
                        onDeleteAccount = { /* Пока ничего */ }
                    )
                }
            }
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

    val bottomNavItems = listOf(
        BottomNavItem.Accounts,
        BottomNavItem.Add,
        BottomNavItem.Settings
    )

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
                        onAddAccount = { /* Пока ничего */ },
                        onDeleteAccount = onDeleteAccount
                    )
                    1 -> AddScreen(
                        onAddAccount = onAddAccount,
                        onBack = { selectedTab = 0 }
                    )
                    2 -> SettingsScreen()
                }
            }
        }
    }
}