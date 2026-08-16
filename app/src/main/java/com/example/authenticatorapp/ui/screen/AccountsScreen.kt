package com.example.authenticatorapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.authenticatorapp.domain.models.Account
import com.example.authenticatorapp.domain.totp.TotpGenerator
import kotlinx.coroutines.delay
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    accounts: List<Account>,
    onAddAccount: () -> Unit,
    onDeleteAccount: (Account) -> Unit
) {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Состояние для диалога удаления
    var showDeleteDialog by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }

    // Состояние для диалога информации
    var showInfoDialog by remember { mutableStateOf(false) }
    var accountInfo by remember { mutableStateOf<Account?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("🔐 Аккаунты") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onAddAccount) {
                        Icon(Icons.Default.Add, "Добавить аккаунт")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (accounts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🔐",
                    fontSize = MaterialTheme.typography.displayLarge.fontSize
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Нет аккаунтов",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Нажмите на +, чтобы добавить",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = accounts,
                    key = { it.id }
                ) { account ->
                    var offsetX by remember { mutableStateOf(0f) }
                    val maxSwipeDistance = 200f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        when {
                                            // Свайп влево (удаление)
                                            offsetX < -maxSwipeDistance / 2 -> {
                                                accountToDelete = account
                                                showDeleteDialog = true
                                            }
                                            // Свайп вправо (информация)
                                            offsetX > maxSwipeDistance / 2 -> {
                                                accountInfo = account
                                                showInfoDialog = true
                                            }
                                        }
                                        offsetX = 0f
                                    },
                                    onDragCancel = { offsetX = 0f },
                                    onHorizontalDrag = { _, dragAmount ->
                                        offsetX = (offsetX + dragAmount).coerceIn(-maxSwipeDistance, maxSwipeDistance)
                                    }
                                )
                            }
                    ) {
                        // 👈 Левый фон (удаление) — виден при свайпе влево
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.error)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    "Удалить",
                                    tint = MaterialTheme.colorScheme.onError
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Удалить",
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                        }

                        // 👉 Правый фон (информация) — виден при свайпе вправо
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    "Информация",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.Info,
                                    "Информация",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        // Карточка со смещением
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset { androidx.compose.ui.unit.IntOffset(offsetX.toInt(), 0) }
                        ) {
                            val totpResult = remember(currentTime) {
                                TotpGenerator.generate(account.secret)
                            }

                            AccountCard(
                                account = account,
                                totpResult = totpResult,
                                onCopy = { /* Копирование */ },
                                onDelete = {
                                    accountToDelete = account
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ========== ДИАЛОГ УДАЛЕНИЯ ==========
    if (showDeleteDialog && accountToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                accountToDelete = null
            },
            title = {
                Text("🗑️ Удалить аккаунт?")
            },
            text = {
                Column {
                    Text("Вы уверены, что хотите удалить аккаунт?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${accountToDelete?.service} (${accountToDelete?.username})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        accountToDelete?.let { onDeleteAccount(it) }
                        showDeleteDialog = false
                        accountToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        accountToDelete = null
                    }
                ) {
                    Text("Отмена")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // ========== ДИАЛОГ ИНФОРМАЦИИ ==========
    if (showInfoDialog && accountInfo != null) {
        AlertDialog(
            onDismissRequest = {
                showInfoDialog = false
                accountInfo = null
            },
            title = {
                Text("ℹ️ Информация об аккаунте")
            },
            text = {
                Column {
                    Text(
                        text = "📱 Сервис: ${accountInfo?.service}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "👤 Логин: ${accountInfo?.username}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔑 Секрет: ${accountInfo?.secret?.take(8)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInfoDialog = false
                        accountInfo = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Закрыть")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}