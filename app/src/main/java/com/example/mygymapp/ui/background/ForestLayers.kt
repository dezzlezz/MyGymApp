package com.example.mygymapp.ui.background

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import com.google.accompanist.pager.PagerState
import com.example.mygymapp.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Animated forest layers shown behind the pager screens.
 */
@Composable
fun ForestBackground(state: PagerState, pageCount: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val width = maxWidth
        val widthPx = with(LocalDensity.current) { width.toPx() }
        val offset = state.currentPage + state.currentPageOffset

        val farX = -offset * widthPx * 0.3f
        val midX = -offset * widthPx * 0.6f
        val nearX = -offset * widthPx

        Canvas(Modifier.fillMaxSize()) {
            val total = size.width * pageCount
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF233329), Color(0xFF0E1C14))
                ),
                size = size
            )

            val far = Path().apply {
                moveTo(0f, size.height * 0.55f)
                lineTo(total, size.height * 0.5f)
                lineTo(total, size.height)
                lineTo(0f, size.height)
                close()
            }
            withTransform({ translate(left = farX) }) { drawPath(far, PineGreen.darken()) }

            val mid = Path().apply {
                moveTo(0f, size.height * 0.7f)
                cubicTo(total * 0.25f, size.height * 0.65f, total * 0.75f, size.height * 0.75f, total, size.height * 0.7f)
                lineTo(total, size.height)
                lineTo(0f, size.height)
                close()
            }
            withTransform({ translate(left = midX) }) { drawPath(mid, MossGreen) }

            val near = Path().apply {
                moveTo(0f, size.height * 0.8f)
                cubicTo(total * 0.3f, size.height * 0.83f, total * 0.7f, size.height * 0.77f, total, size.height * 0.8f)
                lineTo(total, size.height)
                lineTo(0f, size.height)
                close()
            }
            withTransform({ translate(left = nearX) }) { drawPath(near, MossGreen.lighten()) }
        }
    }
}

@Composable
fun AnimatedRiver(state: PagerState, pageCount: Int) {
    val anim = rememberInfiniteTransition(label = "river")
    val alpha by anim.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val total = w * pageCount
        val offsetX = -(state.currentPage + state.currentPageOffset) * w
        val path = Path().apply {
            moveTo(offsetX, h * 0.7f)
            var x = 0f
            while (x <= total) {
                val y = h * (0.7f + 0.05f * sin((x / total) * pageCount * PI).toFloat())
                lineTo(offsetX + x, y)
                x += w / 20f
            }
        }
        drawPath(path, color = SeaFoam.copy(alpha = alpha), style = Stroke(width = h * 0.05f))
    }
}

@Composable
fun FogOverlay() {
    val anim = rememberInfiniteTransition(label = "fog")
    val offsetFraction by anim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "offset"
    )
    Canvas(Modifier.fillMaxSize()) {
        val gradient = Brush.horizontalGradient(colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.2f), Color.Transparent))
        val dx = -size.width + size.width * offsetFraction
        withTransform({ translate(left = dx) }) { drawRect(gradient, size = size) }
        withTransform({ translate(left = dx + size.width) }) { drawRect(gradient, size = size) }
    }
}

@Composable
fun SunRaysOverlay() {
    val anim = rememberInfiniteTransition(label = "sun")
    val radiusFraction by anim.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse),
        label = "radius"
    )
    Canvas(Modifier.fillMaxSize()) {
        val radius = size.height * radiusFraction
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x66FFFACD), Color.Transparent),
                center = Offset(size.width / 2f, 0f),
                radius = radius
            ),
            size = size
        )
    }
}

@Composable
fun FallingLeavesOverlay() {
    val leaves = remember { List(10) { Random.nextFloat() to Random.nextFloat() } }
    val anim = rememberInfiniteTransition(label = "leaves")
    val offsetFraction by anim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "offset"
    )
    Canvas(Modifier.fillMaxSize()) {
        val offset = -50f * offsetFraction
        leaves.forEachIndexed { index, pair ->
            val x = size.width * pair.first
            val baseY = size.height * 0.3f + pair.second * 60f
            drawCircle(
                color = Color(0x5580C080),
                center = Offset(x, baseY + offset + index * 10f),
                radius = 6f
            )
        }
    }
}

private fun Color.darken(amount: Float = 0.1f): Color = copy(
    red = (red * (1 - amount)).coerceIn(0f, 1f),
    green = (green * (1 - amount)).coerceIn(0f, 1f),
    blue = (blue * (1 - amount)).coerceIn(0f, 1f)
)

private fun Color.lighten(amount: Float = 0.1f): Color = copy(
    red = (red + amount).coerceIn(0f, 1f),
    green = (green + amount).coerceIn(0f, 1f),
    blue = (blue + amount).coerceIn(0f, 1f)
)
