package com.example.authenticatorapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun AuthenticatorAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // ← отключаем dynamicColor для контроля
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = PurplePrimary,
            onPrimary = OnPrimary,
            primaryContainer = PurpleDeep,
            onPrimaryContainer = PurpleLighter,
            secondary = PurpleLight,
            onSecondary = OnSecondary,
            secondaryContainer = PurpleDeep,
            onSecondaryContainer = PurpleLighter,
            tertiary = PurplePrimary,
            onTertiary = OnPrimary,
            tertiaryContainer = PurpleDeep,
            onTertiaryContainer = PurpleLighter,
            background = PurpleDeep,
            onBackground = PurpleLighter,
            surface = Color(0xFF2D0A3A),
            onSurface = PurpleLighter,
            surfaceVariant = Color(0xFF3D1A4A),
            onSurfaceVariant = PurpleLighter,
            error = Error,
            onError = OnError,
            errorContainer = ErrorContainer,
            onErrorContainer = OnErrorContainer,
            outline = PurpleLighter,
            outlineVariant = PurpleLight,
            scrim = Scrim,
            surfaceTint = PurplePrimary
        )
    } else {
        lightColorScheme(
            primary = PurplePrimary,
            onPrimary = OnPrimary,
            primaryContainer = PrimaryContainer,
            onPrimaryContainer = OnPrimaryContainer,
            secondary = PurpleLight,
            onSecondary = OnSecondary,
            secondaryContainer = SecondaryContainer,
            onSecondaryContainer = OnSecondaryContainer,
            tertiary = PurplePrimary,
            onTertiary = OnPrimary,
            tertiaryContainer = PurpleCard,
            onTertiaryContainer = PurpleDeep,
            background = PurpleBg,
            onBackground = PurpleDeep,
            surface = Color(0xFFFFFFFF),
            onSurface = PurpleDeep,
            surfaceVariant = SurfaceVariant,
            onSurfaceVariant = OnSurfaceVariant,
            error = Error,
            onError = OnError,
            errorContainer = ErrorContainer,
            onErrorContainer = OnErrorContainer,
            outline = Outline,
            outlineVariant = OutlineVariant,
            scrim = Scrim,
            surfaceTint = SurfaceTint
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}