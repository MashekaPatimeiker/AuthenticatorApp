package com.example.authenticatorapp.ui.screen

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Состояния для уведомлений
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    // Скрываем уведомления через 3 секунды
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
                title = { Text("➕ Добавить аккаунт") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Назад",
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
            // Кнопки быстрого добавления
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
                    Icon(Icons.Default.QrCodeScanner, "QR-сканер")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("QR-сканер", fontSize = 12.sp)
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
                    Icon(Icons.Default.ContentPaste, "Вставить")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Вставить", fontSize = 12.sp)
                }
            }

            // Кнопка "Сгенерировать секрет"
            Button(
                onClick = {
                    secret = SecretGenerator.generateSecret()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Icon(Icons.Default.Refresh, "Обновить")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Сгенерировать секрет")
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Поля ввода
            OutlinedTextField(
                value = service,
                onValueChange = { service = it },
                label = { Text("Сервис (например, Google)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                isError = service.isBlank() && showError
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин или email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            OutlinedTextField(
                value = secret,
                onValueChange = { secret = it.uppercase() },
                label = { Text("Секретный ключ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text(
                        if (secret.isNotBlank() && !SecretGenerator.isValidSecret(secret)) {
                            "❌ Некорректный секрет (только A-Z и 2-7)"
                        } else {
                            "Например: JBSWY3DPEHPK3PXP"
                        },
                        color = if (secret.isNotBlank() && !SecretGenerator.isValidSecret(secret)) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                isError = secret.isNotBlank() && !SecretGenerator.isValidSecret(secret),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error
                )
            )

            // Кнопка "Добавить"
            Button(
                onClick = {
                    // Валидация
                    when {
                        service.isBlank() -> {
                            showError = true
                            errorMessage = "Введите название сервиса"
                            return@Button
                        }
                        secret.isBlank() -> {
                            showError = true
                            errorMessage = "Введите секретный ключ"
                            return@Button
                        }
                        !SecretGenerator.isValidSecret(secret) -> {
                            showError = true
                            errorMessage = "Некорректный секрет (только A-Z и 2-7)"
                            return@Button
                        }
                    }

                    // Всё ок — добавляем
                    onAddAccount(
                        Account(
                            service = service,
                            username = username.ifBlank { "Без логина" },
                            secret = secret
                        )
                    )

                    // Показываем сообщение об успехе
                    showSuccess = true

                    // Очищаем поля
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
                Text("Добавить аккаунт")
            }

            // Уведомления
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
                            text = "✅ Аккаунт успешно добавлен!",
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
                text = "Или отсканируй QR-код с сайта 📸",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}