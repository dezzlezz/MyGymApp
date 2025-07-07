package com.example.mygymapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NatureGreen,
    onPrimary = OnDark,
    secondary = KaizenBeige,
    onSecondary = OnDark,
    background = DeepBlack,
    onBackground = OnDark,
    surface = DarkGreen,
    onSurface = OnDark,
    error = ErrorRed,
    onError = OnDark
)

private val LightColorScheme = lightColorScheme(
    primary = NatureGreen,
    onPrimary = DeepBlack,
    secondary = KaizenBeige,
    onSecondary = DeepBlack,
    background = OnDark,
    onBackground = DeepBlack,
    surface = OnDark,
    onSurface = DeepBlack,
    error = ErrorRed,
    onError = DeepBlack
)

@Composable
fun MyGymAppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
