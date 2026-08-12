package com.example.authenticatorapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.authenticatorapp.domain.models.Account

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onAddAccount: (Account) -> Unit,
    onBack: () -> Unit
) {
    var service by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var secret by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("➕ Добавить аккаунт") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
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
            OutlinedTextField(
                value = service,
                onValueChange = { service = it },
                label = { Text("Сервис (например, Google)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Логин или email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = secret,
                onValueChange = { secret = it.uppercase() },
                label = { Text("Секретный ключ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Например: JBSWY3DPEHPK3PXP")
                }
            )

            Button(
                onClick = {
                    if (service.isNotBlank() && secret.isNotBlank()) {
                        onAddAccount(
                            Account(
                                service = service,
                                username = username.ifBlank { "Без логина" },
                                secret = secret
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = service.isNotBlank() && secret.isNotBlank()
            ) {
                Text("Добавить аккаунт")
            }

            Text(
                text = "Скоро добавим сканирование QR-кода 📸",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}