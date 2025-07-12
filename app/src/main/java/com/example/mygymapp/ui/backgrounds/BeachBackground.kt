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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
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
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) (2f * PI).toFloat() else 0f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing))
    )
    val sandDots = remember { List(40) { Offset(Random.nextFloat(), Random.nextFloat()) } }
    val algaePoints = remember { List(6) { Random.nextFloat() } }

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val horizon = h * 0.35f
        val sandBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = if (darkMode) listOf(BeachSandDark, BeachSandDark.copy(alpha = 0.8f))
            else listOf(BeachSand, BeachSand.copy(alpha = 0.8f)),
            startY = horizon,
            endY = h
        )
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(SunYellow, WaveBlue.copy(alpha = 0.4f)),
                startY = 0f,
                endY = horizon
            ),
            size = androidx.compose.ui.geometry.Size(w, horizon)
        )
        drawRect(sandBrush, Offset(0f, horizon), androidx.compose.ui.geometry.Size(w, h - horizon))
        sandDots.forEach { p ->
            val x = p.x * w
            val y = horizon + p.y * (h - horizon)
            drawCircle(color = Color.Black.copy(alpha = 0.05f), radius = 1.5f, center = Offset(x, y))
        }

        val amplitude1 = h * 0.03f
        val amplitude2 = h * 0.02f
        val amplitude3 = h * 0.015f
        val wavelength = w * 0.6f
        fun wavePath(baseY: Float, amp: Float, shift: Float): Path {
            val step = w / 25f
            return Path().apply {
                moveTo(0f, baseY)
                var x = 0f
                while (x <= w + step) {
                    val y = baseY + amp * sin((x / wavelength + shift) * 2f * PI).toFloat()
                    lineTo(x, y)
                    x += step
                }
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
        }

        val water = if (darkMode) WaveBlue.copy(alpha = 0.7f) else WaveBlue
        val foam = SeaFoam.copy(alpha = if (darkMode) 0.5f else 0.7f)
        val path1 = wavePath(horizon, amplitude1, phase)
        val path2 = wavePath(horizon + amplitude1 * 0.6f, amplitude2, -phase * 1.2f)
        val path3 = wavePath(horizon + amplitude1, amplitude3, phase * 0.8f)
        drawPath(path1, water)
        drawPath(path2, foam)
        drawPath(path3, foam.copy(alpha = 0.5f))

        // algae near the sand for more detail
        val algaeColor = if (darkMode) PineGreen else AlgaeGreen
        algaePoints.forEachIndexed { idx, r ->
            val baseX = w * (0.1f + r * 0.8f)
            val height = h * (0.05f + (idx % 3) * 0.02f)
            val path = Path().apply {
                moveTo(baseX, h - 2f)
                quadraticBezierTo(baseX - 10f, h - height * 0.5f, baseX, h - height)
                quadraticBezierTo(baseX + 10f, h - height * 0.5f, baseX, h - 2f)
                close()
            }
            drawPath(path, algaeColor)
        }
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
