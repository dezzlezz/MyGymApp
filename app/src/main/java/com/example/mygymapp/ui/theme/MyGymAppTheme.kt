package com.example.mygymapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NatureGreen,
    onPrimary = OnDark,
    secondary = AccentGreen,
    onSecondary = DeepBlack,
    background = DeepBlack,
    onBackground = OnDark,
    surface = DarkGreen,
    onSurface = OnDark,
    error = ErrorRed,
    onError = OnDark
)

@Composable
fun MyGymAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
