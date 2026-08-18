package com.example.authenticatorapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.authenticatorapp.R

sealed class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    object Accounts : BottomNavItem(
        route = "accounts",
        titleResId = R.string.nav_accounts,
        icon = Icons.Default.Lock
    )

    object Add : BottomNavItem(
        route = "add",
        titleResId = R.string.nav_add,
        icon = Icons.Default.AddCircle
    )

    object Settings : BottomNavItem(
        route = "settings",
        titleResId = R.string.nav_settings,
        icon = Icons.Default.Settings
    )
}