package com.example.mygymapp.ui.background

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import com.google.accompanist.pager.PagerState
import com.example.mygymapp.ui.theme.MossGreen
import com.example.mygymapp.ui.theme.PineGreen
import com.example.mygymapp.ui.theme.SeaFoam
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

// Himmel-Farben definiert im File
private val SkyBlue = Color(0xFF87CEEB)
private val DuskPurple = Color(0xFF4B2E83)

/**
 * Parallax forest layers with animated river background.
 */
@Composable
fun ForestBackground(state: PagerState, pageCount: Int) {
    // River transparency animation
    val riverAnim = rememberInfiniteTransition(label = "riverBackground")
    val riverAlpha by riverAnim.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(1500, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "riverAlpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val offsetX = -(state.currentPage + state.currentPageOffset) * w

        // Sky gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(SkyBlue, DuskPurple)
            ),
            size = size
        )

        // Draw forest layers with parallax
        drawLayer(offsetX, 0.55f, 0.02f, w, h, pageCount, PineGreen.darken(), parallax = 0.3f)
        drawLayer(offsetX, 0.70f, 0.04f, w, h, pageCount, MossGreen, parallax = 0.6f)
        drawLayer(offsetX, 0.80f, 0.06f, w, h, pageCount, MossGreen.lighten(), parallax = 1.0f)

        // Animated river
        val riverPath = Path().apply {
            moveTo(offsetX, h * 0.85f)
            var x = 0f
            while (x <= w * pageCount) {
                val y = h * (0.85f + 0.02f * sin((x / w + state.currentPageOffset) * PI).toFloat())
                lineTo(offsetX + x, y)
                x += w / 30f
            }
        }
        drawPath(
            riverPath,
            color = SeaFoam.copy(alpha = riverAlpha),
            style = Stroke(width = h * 0.04f)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLayer(
    offsetX: Float,
    yOffset: Float,
    amplitudeFactor: Float,
    w: Float,
    h: Float,
    pageCount: Int,
    color: Color,
    parallax: Float
) {
    val totalWidth = w * pageCount
    val path = Path().apply {
        moveTo(0f, h * yOffset)
        var xPos = 0f
        while (xPos <= totalWidth) {
            val progressRatio = xPos.toDouble() / w.toDouble() + (offsetX / w).toDouble()
            val y = h * yOffset + h * amplitudeFactor * sin(progressRatio * PI).toFloat()
            lineTo(offsetX * parallax + xPos, y)
            xPos += w / 20f
        }
        lineTo(offsetX * parallax + totalWidth, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path, color = color)
}

/**
 * Subtle fog overlay scrolling across the screen.
 */
@Composable
fun FogOverlay() {
    val fogAnim = rememberInfiniteTransition(label = "fog")
    val offset by fogAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(20000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val gradient = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color.White.copy(alpha = 0.2f),
                Color.Transparent
            )
        )
        val px = offset * w
        withTransform({ translate(left = px - w) }) {
            drawRect(gradient, size = size)
        }
        withTransform({ translate(left = px) }) {
            drawRect(gradient, size = size)
        }
    }
}

/**
 * Pulsating sun rays from top center.
 */
@Composable
fun SunRaysOverlay() {
    val sunAnim = rememberInfiniteTransition(label = "sun")
    val radius by sunAnim.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "radius"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val h = size.height
        val w = size.width
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x66FFFACD), Color.Transparent),
                center = Offset(w / 2f, 0f),
                radius = h * radius
            ),
            size = size
        )
    }
}

/**
 * Gentle falling leaves overlay.
 */
@Composable
fun FallingLeavesOverlay() {
    val leaves = remember { List(10) { Random.nextFloat() to Random.nextFloat() } }
    val leavesAnim = rememberInfiniteTransition(label = "leaves")
    val offset by leavesAnim.animateFloat(
        initialValue = 0f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(
            tween(8000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val h = size.height
        val w = size.width
        leaves.forEachIndexed { index, (fx, fy) ->
            val x = w * fx
            val baseY = h * 0.3f + fy * 60f
            drawCircle(
                color = Color(0x5580C080),
                center = Offset(x, baseY + offset + index * 10f),
                radius = 6f
            )
        }
    }
}

// Farb-Manipulationserweiterungen
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
