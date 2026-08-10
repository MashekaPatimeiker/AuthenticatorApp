package com.example.authenticatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authenticatorapp.model.TotpGenerator
import com.example.authenticatorapp.viewmodel.MainViewModel
import com.example.authenticatorapp.ui.theme.AuthenticatorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthenticatorAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val totpState by viewModel.totpState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🔐 Аутентификатор") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (totpState.secondsRemaining < 5)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Код доступа",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = totpState.code,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 52.sp,
                        color = if (totpState.secondsRemaining < 5)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = totpState.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = if (totpState.secondsRemaining < 5)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${totpState.secondsRemaining} сек.",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (totpState.secondsRemaining < 5)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Секрет: JBSWY3DPEHPK3PXP",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Проверь в Google Authenticator\n(QR-код добавим позже)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}