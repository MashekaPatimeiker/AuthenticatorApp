package com.example.authenticatorapp.ui.screen

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authenticatorapp.R
import com.example.authenticatorapp.domain.models.Account
import com.example.authenticatorapp.domain.totp.SecretGenerator
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onAddAccount: (Account) -> Unit,
    onBack: () -> Unit,
    onOpenQrScanner: () -> Unit
) {
    var service by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }

    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(3000)
            showSuccess = false
        }
    }
    LaunchedEffect(showError) {
        if (showError) {
            delay(3000)
            showError = false
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_account_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.cancel),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onOpenQrScanner,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.QrCodeScanner, stringResource(R.string.qr_scanner))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.qr_scanner), fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val clip = clipboard.primaryClip
                        if (clip != null && clip.itemCount > 0) {
                            val text = clip.getItemAt(0).text.toString()
                            secret = text.uppercase()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.ContentPaste, stringResource(R.string.paste))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.paste), fontSize = 12.sp)
                }
            }

            Button(
                onClick = {
                    secret = SecretGenerator.generateSecret()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Icon(Icons.Default.Refresh, stringResource(R.string.generate_secret))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.generate_secret))
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            OutlinedTextField(
                value = service,
                onValueChange = { service = it },
                label = { Text(stringResource(R.string.service_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = service.isBlank() && showError
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.username_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = secret,
                onValueChange = { secret = it.uppercase() },
                label = { Text(stringResource(R.string.secret_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text(
                        if (secret.isNotBlank() && !SecretGenerator.isValidSecret(secret)) {
                            stringResource(R.string.invalid_secret)
                        } else {
                            stringResource(R.string.secret_hint)
                        },
                        color = if (secret.isNotBlank() && !SecretGenerator.isValidSecret(secret)) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                isError = secret.isNotBlank() && !SecretGenerator.isValidSecret(secret)
            )

            Button(
                onClick = {
                    when {
                        service.isBlank() -> {
                            showError = true
                            errorMessage = context.getString(R.string.error_service_empty)
                            return@Button
                        }
                        secret.isBlank() -> {
                            showError = true
                            errorMessage = context.getString(R.string.error_secret_empty)
                            return@Button
                        }
                        !SecretGenerator.isValidSecret(secret) -> {
                            showError = true
                            errorMessage = context.getString(R.string.error_invalid_secret)
                            return@Button
                        }
                    }

                    onAddAccount(
                        Account(
                            service = service,
                            username = username.ifBlank { "Без логина" },
                            secret = secret
                        )
                    )

                    showSuccess = true
                    service = ""
                    username = ""
                    secret = ""
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = service.isNotBlank() && secret.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(R.string.add_button))
            }

            if (showSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.account_added_success),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (showError) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "❌ $errorMessage",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.or_scan_qr),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}