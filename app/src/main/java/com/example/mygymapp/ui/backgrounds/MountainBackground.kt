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
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.abs
import kotlin.math.sin
import com.example.mygymapp.ui.theme.*
import kotlin.random.Random

@Composable
fun MountainBackground(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    animationsEnabled: Boolean = true
) {
    Box(modifier) {
        MountainLayers(Modifier.fillMaxSize(), darkMode)
        StarrySky(Modifier.fillMaxSize(), darkMode, animationsEnabled)
        SnowOverlay(Modifier.fillMaxSize(), animationsEnabled)
    }
}

private fun randomCurve(random: Random): List<Pair<Float, Float>> =
    List(7) { it / 6f to (0.4f + random.nextFloat() * 0.3f) }

@Composable
private fun MountainLayers(modifier: Modifier, darkMode: Boolean) {
    val shapes = remember {
        listOf(randomCurve(Random(1)), randomCurve(Random(2)), randomCurve(Random(3)))
    }
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val colors = if (darkMode) {
            listOf(MountainSurfaceDark, MountainSurfaceDark.copy(alpha = 0.7f), MountainSurfaceDark.copy(alpha = 0.5f))
        } else {
            listOf(MountainDeepBlue, GlacierBlue, GlacierBlue.copy(alpha = 0.5f))
        }
        shapes.forEachIndexed { idx, points ->
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(points.first().first * w, points.first().second * h)
                points.drop(1).forEach { (x, y) -> quadraticBezierTo(x * w, y * h, x * w, y * h) }
                lineTo(w, h)
                close()
            }
            drawPath(path, colors[idx])
        }
    }
}

private data class Star(val x: Float, val y: Float, val phase: Float)

@Composable
private fun StarrySky(modifier: Modifier, darkMode: Boolean, animationsEnabled: Boolean, count: Int = 40) {
    val stars = remember { List(count) { Star(Random.nextFloat(), Random.nextFloat() * 0.5f, Random.nextFloat()) } }
    val transition = rememberInfiniteTransition(label = "stars")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val color = if (darkMode) Color.White else DuskViolet
        stars.forEach { s ->
            val alpha = 0.5f + 0.5f * abs(sin((time + s.phase) * 2f * Math.PI)).toFloat()
            drawCircle(color.copy(alpha = alpha), radius = 2f, center = Offset(w * s.x, h * s.y))
        }
    }
}

private data class Snow(val x: Float, val speed: Float)

@Composable
private fun SnowOverlay(modifier: Modifier, animationsEnabled: Boolean, count: Int = 50) {
    val flakes = remember { List(count) { Snow(Random.nextFloat(), 0.5f + Random.nextFloat()) } }
    val transition = rememberInfiniteTransition(label = "snow")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        flakes.forEach { f ->
            val y = (anim * f.speed + f.x) % 1f
            drawCircle(Color.White.copy(alpha = 0.8f), 3f, Offset(w * f.x, h * y))
        }
    }
}
