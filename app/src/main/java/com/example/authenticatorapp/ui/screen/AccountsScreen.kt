package com.example.authenticatorapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
                                        if (abs(offsetX) > maxSwipeDistance / 2) {
                                            onDeleteAccount(account)
                                        }
                                        offsetX = 0f
                                    },
                                    onDragCancel = { offsetX = 0f },
                                    onHorizontalDrag = { _, dragAmount ->
                                        offsetX = (offsetX + dragAmount).coerceIn(-maxSwipeDistance, 0f)
                                    }
                                )
                            }
                    ) {
                        // Фон для свайпа (кнопка удаления)
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
                                horizontalArrangement = Arrangement.End
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
                                onDelete = { onDeleteAccount(account) }
                            )
                        }
                    }
                }
            }
        }
    }
}