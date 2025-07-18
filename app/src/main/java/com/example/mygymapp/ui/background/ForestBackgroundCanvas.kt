package com.example.mygymapp.ui.background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import com.example.mygymapp.ui.theme.MossGreen
import com.example.mygymapp.ui.theme.PineGreen
import com.example.mygymapp.ui.theme.RiverBlue
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun ForestBackgroundCanvas(
    modifier: Modifier = Modifier,
    offsetX: Float = 0f
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { translationX = offsetX }
    ) {
        val w = size.width
        val h = size.height

        val fireflies = remember {
            List(10) {
                Triple(
                    Random.nextFloat(),
                    0.4f * Random.nextFloat() + 0.1f,
                    Random.nextFloat() * 2f * PI.toFloat()
                )
            }
        }
        val anim = rememberInfiniteTransition(label = "fireflies")
        val phase by anim.animateFloat(
            initialValue = 0f,
            targetValue = (2f * PI).toFloat(),
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 6000, easing = LinearEasing),
                RepeatMode.Restart
            ),
            label = "phase"
        )

        val far = Path().apply {
            moveTo(0f, h * 0.55f)
            cubicTo(w * 0.25f, h * 0.5f, w * 0.75f, h * 0.6f, w, h * 0.55f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(far, PineGreen.darken(0.2f))

        val mid = Path().apply {
            moveTo(0f, h * 0.7f)
            cubicTo(w * 0.2f, h * 0.65f, w * 0.8f, h * 0.75f, w, h * 0.7f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(mid, MossGreen)

        val near = Path().apply {
            moveTo(0f, h * 0.85f)
            cubicTo(w * 0.3f, h * 0.83f, w * 0.7f, h * 0.87f, w, h * 0.85f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(near, MossGreen.lighten(0.15f))

        val river = Path().apply {
            moveTo(0f, h * 0.75f)
            cubicTo(w * 0.25f, h * 0.72f, w * 0.75f, h * 0.78f, w, h * 0.75f)
            lineTo(w, h * 0.8f)
            cubicTo(w * 0.75f, h * 0.82f, w * 0.25f, h * 0.78f, 0f, h * 0.8f)
            close()
        }
        drawPath(river, RiverBlue)

        val amplitude = 0.02f
        val radius = 3f
        fireflies.forEach { (xFrac, baseY, seed) ->
            val y = baseY + amplitude * sin(phase + seed).toFloat()
            val alpha = 0.3f + 0.7f * (0.5f + 0.5f * sin(phase + seed).toFloat())
            drawCircle(
                color = Color.Yellow.copy(alpha = alpha),
                center = Offset(w * xFrac, h * y),
                radius = radius
            )
        }
    }
}

private fun Color.darken(amount: Float = 0.1f): Color = copy(
    red = (red * (1 - amount)).coerceIn(0f, 1f),
    green = (green * (1 - amount)).coerceIn(0f, 1f),
    blue = (blue * (1 - amount)).coerceIn(0f, 1f),
)

private fun Color.lighten(amount: Float = 0.1f): Color = copy(
    red = (red + amount).coerceIn(0f, 1f),
    green = (green + amount).coerceIn(0f, 1f),
    blue = (blue + amount).coerceIn(0f, 1f),
)
