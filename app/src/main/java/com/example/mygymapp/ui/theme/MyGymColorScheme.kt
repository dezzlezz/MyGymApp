package com.example.mygymapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

object MyGymColorScheme {
    val LightColors = lightColorScheme(
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

    val DarkColors = darkColorScheme(
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
}
