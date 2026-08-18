package com.example.authenticatorapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleLighter,
    onPrimaryContainer = PurpleDark,
    secondary = PurpleLight,
    onSecondary = Color.White,
    secondaryContainer = PurpleCard,
    onSecondaryContainer = PurpleDark,
    tertiary = PurplePrimary,
    onTertiary = Color.White,
    tertiaryContainer = PurpleCard,
    onTertiaryContainer = PurpleDark,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF3E5F5),
    onSurfaceVariant = PurpleDark,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = PurpleLight,
    outlineVariant = PurpleLighter,
    scrim = Color(0xFF000000),
    surfaceTint = PurplePrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleDark,
    onPrimaryContainer = PurpleLighter,
    secondary = PurpleLight,
    onSecondary = Color.White,
    secondaryContainer = PurpleDark,
    onSecondaryContainer = PurpleLighter,
    tertiary = PurplePrimary,
    onTertiary = Color.White,
    tertiaryContainer = PurpleDark,
    onTertiaryContainer = PurpleLighter,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E0E9),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF2D1A3A),
    onSurfaceVariant = PurpleLighter,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = PurpleLight,
    outlineVariant = PurpleLighter,
    scrim = Color(0xFF000000),
    surfaceTint = PurplePrimary
)

@Composable
fun AuthenticatorAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as androidx.activity.ComponentActivity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}