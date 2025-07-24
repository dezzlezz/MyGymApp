package com.example.mygymapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate

@Composable
fun BookmarkRibbon(modifier: Modifier = Modifier) {
    val swing by rememberInfiniteTransition(label = "swing").animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "swingAnim"
    )

    Canvas(
        modifier = modifier
            .size(width = 14.dp, height = 50.dp)
            .rotate(swing)
    ) {
        val centerX = size.width / 2f

        // 🧵 Kordel (leicht geschwungen)
        val cord = Path().apply {
            moveTo(centerX, 0f)
            quadraticBezierTo(centerX + 3f, size.height * 0.3f, centerX, size.height * 0.75f)
        }
        drawPath(cord, color = Color(0xFF3F4E3A), style = Stroke(width = 2f))

        // 🎀 Quaste (kleine Bommel)
        drawCircle(
            color = Color(0xFF3F4E3A),
            radius = 5f,
            center = androidx.compose.ui.geometry.Offset(centerX, size.height * 0.92f)
        )
    }
}
