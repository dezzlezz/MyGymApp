package com.example.mygymapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyGymAppTheme(content: @Composable () -> Unit) {
    val colors = lightColorScheme(
        primary = PineGreen,
        secondary = FogGray,
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    )
    MaterialTheme(colorScheme = colors, content = content)
}
