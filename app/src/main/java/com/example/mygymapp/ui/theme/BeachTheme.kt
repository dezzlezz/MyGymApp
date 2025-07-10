package com.example.mygymapp.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val BeachColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = DeepBlack,
    secondary = Turquoise,
    onSecondary = DeepBlack,
    background = Sand,
    onBackground = DeepBlack,
    surface = Sand,
    onSurface = DeepBlack
)

private val BeachShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun BeachTheme(content: @Composable () -> Unit) {
    Crossfade(targetState = BeachColorScheme) { colors ->
        MaterialTheme(colorScheme = colors, shapes = BeachShapes) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Sand, SkyBlue)
                        )
                    )
            ) {
                content()
            }
        }
    }
}
