package com.example.authenticatorapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authenticatorapp.domain.models.Account
import com.example.authenticatorapp.domain.models.TotpGenerator

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AccountCard(
    account: Account,
    totpResult: TotpGenerator.TotpResult,
    onCopy: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (totpResult.secondsRemaining < 5)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = account.service,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = account.username,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                AnimatedContent(
                    targetState = totpResult.code,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) +
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(300)
                                ) with
                                fadeOut(animationSpec = tween(200)) +
                                slideOutVertically(
                                    targetOffsetY = { -it },
                                    animationSpec = tween(200)
                                )
                    }
                ) { code ->
                    Text(
                        text = code,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 28.sp,
                        color = if (totpResult.secondsRemaining < 5)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    AnimatedProgressIndicator(
                        progress = totpResult.progress,
                        color = if (totpResult.secondsRemaining < 5)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    AnimatedContent(
                        targetState = totpResult.secondsRemaining,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)) +
                                    slideInVertically(
                                        initialOffsetY = { -it / 2 },
                                        animationSpec = tween(200)
                                    ) with
                                    fadeOut(animationSpec = tween(100)) +
                                    slideOutVertically(
                                        targetOffsetY = { it / 2 },
                                        animationSpec = tween(100)
                                    )
                        }
                    ) { seconds ->
                        Text(
                            text = "${seconds}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedProgressIndicator(
    progress: Float,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "progress_animation"
    )

    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier
            .width(60.dp)
            .height(4.dp),
        color = color,
        trackColor = color.copy(alpha = 0.3f)
    )
}