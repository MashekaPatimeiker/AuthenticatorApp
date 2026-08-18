package com.example.authenticatorapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.authenticatorapp.R
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

    var showDeleteDialog by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }

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
                title = { Text(stringResource(R.string.accounts_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onAddAccount) {
                        Icon(Icons.Default.Add, stringResource(R.string.add_account))
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
                    text = stringResource(R.string.no_accounts),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.no_accounts_hint),
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
                                            offsetX < -maxSwipeDistance / 2 -> {
                                                accountToDelete = account
                                                showDeleteDialog = true
                                            }
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
                                    stringResource(R.string.delete),
                                    tint = MaterialTheme.colorScheme.onError
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    stringResource(R.string.delete),
                                    color = MaterialTheme.colorScheme.onError
                                )
                            }
                        }

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
                                    stringResource(R.string.info),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    Icons.Default.Info,
                                    stringResource(R.string.info),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

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

    DeleteAccountDialog(
        showDialog = showDeleteDialog,
        account = accountToDelete,
        onConfirm = { accountToDelete?.let { onDeleteAccount(it) } },
        onDismiss = {
            showDeleteDialog = false
            accountToDelete = null
        }
    )

    AccountInfoDialog(
        showDialog = showInfoDialog,
        account = accountInfo,
        onDismiss = {
            showInfoDialog = false
            accountInfo = null
        }
    )
}

@Composable
fun DeleteAccountDialog(
    showDialog: Boolean,
    account: Account?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog && account != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.delete_account_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.delete_account_confirm))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${account.service} (${account.username})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete_button))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun AccountInfoDialog(
    showDialog: Boolean,
    account: Account?,
    onDismiss: () -> Unit
) {
    if (showDialog && account != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.account_info_title)) },
            text = {
                Column {
                    Text(
                        text = "📱 ${stringResource(R.string.service)}: ${account.service}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "👤 ${stringResource(R.string.username)}: ${account.username}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🔑 ${stringResource(R.string.secret)}: ${account.secret.take(8)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}