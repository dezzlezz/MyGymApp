package com.example.mygymapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyGymAppTheme(content: @Composable () -> Unit) {
    val lightColors = lightColorScheme(
        primary = PrimaryGreen,
        secondary = SecondaryGreen,
        background = LightBackground,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        error = ErrorRed
    )
    val darkColors = darkColorScheme(
        primary = PrimaryGreen,
        secondary = SecondaryGreen,
        background = DarkBackground,
        surface = DarkBackground,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        error = ErrorRed
    )
    val colors = if (isSystemInDarkTheme()) darkColors else lightColors
    MaterialTheme(colorScheme = colors, content = content)
}
