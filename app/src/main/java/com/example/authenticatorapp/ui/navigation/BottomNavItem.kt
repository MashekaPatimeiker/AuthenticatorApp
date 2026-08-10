package com.example.authenticatorapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Accounts : BottomNavItem(
        route = "accounts",
        title = "Аккаунты",
        icon = Icons.Default.Lock
    )

    object Add : BottomNavItem(
        route = "add",
        title = "Добавить",
        icon = Icons.Default.AddCircle
    )

    object Settings : BottomNavItem(
        route = "settings",
        title = "Настройки",
        icon = Icons.Default.Settings
    )
}