package com.example.mygymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun PaperBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFEDE5D0),
                        Color(0xFFD9CEB2)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val noiseColor = Color.Black.copy(alpha = 0.01f)
            val step = 4
            for (x in 0 until size.width.toInt() step step) {
                for (y in 0 until size.height.toInt() step step) {
                    if ((x * 17 + y * 31) % 101 == 0) {
                        drawRect(
                            color = noiseColor,
                            topLeft = Offset(x.toFloat(), y.toFloat()),
                            size = Size(step.toFloat(), step.toFloat())
                        )
                    }
                }
            }
        }
        content()
    }
}
