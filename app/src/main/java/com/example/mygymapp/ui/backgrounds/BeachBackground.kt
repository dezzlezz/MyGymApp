package com.example.mygymapp.ui.backgrounds

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import com.example.mygymapp.ui.theme.*

@Composable
fun BeachBackground(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    animationsEnabled: Boolean = true
) {
    Box(modifier) {
        Waves(Modifier.fillMaxSize(), darkMode, animationsEnabled)
        SeagullShadow(Modifier.fillMaxSize())
    }
}

@Composable
private fun Waves(modifier: Modifier, darkMode: Boolean, animationsEnabled: Boolean) {
    val transition = rememberInfiniteTransition(label = "wave")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val horizon = h * 0.35f
        val sand = if (darkMode) BeachSandDark else BeachSand
        drawRect(sand, Offset(0f, horizon), androidx.compose.ui.geometry.Size(w, h - horizon))
        val dy = h * 0.05f
        val dx = w * anim
        val water = if (darkMode) WaveBlue.copy(alpha = 0.7f) else WaveBlue
        val foam = SeaFoam.copy(alpha = if (darkMode) 0.6f else 0.8f)
        val wave1 = Path().apply {
            moveTo(-w + dx, horizon)
            cubicTo(-w * 0.5f + dx, horizon + dy, w * 0.5f + dx, horizon - dy, w + dx, horizon)
            lineTo(w + dx, horizon + dy * 2)
            lineTo(-w + dx, horizon + dy * 2)
            close()
        }
        val wave2 = Path().apply {
            moveTo(-w + dx * 1.3f, horizon + dy)
            cubicTo(-w * 0.4f + dx * 1.3f, horizon + dy * 2, w * 0.6f + dx * 1.3f, horizon, w + dx * 1.3f, horizon + dy)
            lineTo(w + dx * 1.3f, horizon + dy * 3)
            lineTo(-w + dx * 1.3f, horizon + dy * 3)
            close()
        }
        drawPath(wave1, water)
        drawPath(wave2, foam)
    }
}

@Composable
private fun SeagullShadow(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.8f, h * 0.15f)
            quadraticBezierTo(w * 0.82f, h * 0.1f, w * 0.86f, h * 0.15f)
            moveTo(w * 0.86f, h * 0.15f)
            quadraticBezierTo(w * 0.88f, h * 0.1f, w * 0.92f, h * 0.15f)
        }
        drawPath(path, color = Color.Black.copy(alpha = 0.2f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
    }
}
