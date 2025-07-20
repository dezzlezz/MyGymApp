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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.drawscope.translate
import com.example.mygymapp.ui.theme.MossGreen
import com.example.mygymapp.ui.theme.PineGreen
import com.example.mygymapp.ui.theme.RiverBlue
import com.example.mygymapp.ui.theme.DeepForest
import com.example.mygymapp.ui.theme.RiverHighlight
import com.example.mygymapp.ui.theme.FogGray
import com.example.mygymapp.ui.theme.SkyDark
import com.example.mygymapp.ui.theme.SkyLight
import com.example.mygymapp.ui.theme.SunriseOrange
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

private fun DrawScope.drawHillPage(page: Int, pageWidth: Float, height: Float) {
    val startX = pageWidth * page
    val hill = Path().apply {
        moveTo(startX, height * 0.8f)
        cubicTo(
            startX + pageWidth * 0.3f,
            height * 0.75f,
            startX + pageWidth * 0.7f,
            height * 0.85f,
            startX + pageWidth,
            height * 0.8f
        )
        lineTo(startX + pageWidth, height)
        lineTo(startX, height)
        close()
    }
    drawPath(hill, MossGreen)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(SunriseOrange, Color.Transparent),
            center = Offset(startX + pageWidth * 0.8f, height * 0.2f),
            radius = pageWidth * 0.5f
        ),
        center = Offset(startX + pageWidth * 0.8f, height * 0.2f),
        radius = pageWidth * 0.5f
    )
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

private data class FogCloud(
    val start: Float,
    val baseY: Float,
    val width: Float,
    val height: Float,
    val wave: Float,
    val color: Color
)

@Composable
fun ForestBackgroundCanvas(
    currentPageOffset: Float,
    modifier: Modifier = Modifier,
    showLightCone: Boolean = false,
    showFog: Boolean = false
) {
    // Use relative positions so we can stretch the landscape over four screens
    val stars = remember { List(50) { Offset(Random.nextFloat() * 4f, Random.nextFloat() * 0.4f) } }
    val fireflies = remember {
        List(10) {
            Triple(
                Random.nextFloat() * 4f,
                0.4f * Random.nextFloat() + 0.1f,
                Random.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    val fireflyAnim = rememberInfiniteTransition(label = "fireflies")
    val phase by fireflyAnim.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 6000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "phase"
    )

    val coneAnim = rememberInfiniteTransition(label = "cone")
    val coneAlpha by coneAnim.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 3000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val fogClouds = remember {
        List(5) {
            FogCloud(
                start = Random.nextFloat(),
                baseY = 0.65f + 0.1f * Random.nextFloat(),
                width = 0.3f + 0.2f * Random.nextFloat(),
                height = 0.08f + 0.04f * Random.nextFloat(),
                wave = Random.nextFloat() * 2f * PI.toFloat(),
                color = FogGray.copy(alpha = 0.1f + 0.05f * Random.nextFloat())
            )
        }
    }

    val fogAnim = rememberInfiniteTransition(label = "fog")
    val fogShift by fogAnim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 30000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "shift"
    )
    val fogPhase by fogAnim.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 12000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {
        val pageWidth = size.width
        val w = pageWidth * 4f
        val h = size.height
        val shift = -currentPageOffset * pageWidth

        withTransform({ translate(left = shift) }) {

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(SkyDark, SkyLight),
                startY = 0f,
                endY = h * 0.6f
            ),
            size = Size(w, h)
        )

        repeat(4) { page ->
            drawSkyPage(page, pageWidth, h)
        }

        stars.forEach { offset ->
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                center = Offset(w * offset.x, h * offset.y),
                radius = 1.5f
            )
        }

        drawForestPage(0, pageWidth, h, dense = true, withFireflies = true)
        drawForestPage(1, pageWidth, h, dense = false, withFireflies = false)
        drawRiverPage(2, pageWidth, h)
        drawHillPage(3, pageWidth, h)

        if (showFog) {
            fogClouds.forEach { cloud ->
                val cloudWidth = w * cloud.width
                val cloudHeight = h * cloud.height
                val progress = (fogShift + cloud.start) % 1f
                val x = -cloudWidth + progress * (w + cloudWidth)
                val baseY = h * cloud.baseY
                val y = baseY + h * 0.02f * sin(fogPhase + cloud.wave).toFloat()
                drawRoundRect(
                    color = cloud.color,
                    topLeft = Offset(x, y),
                    size = Size(cloudWidth, cloudHeight),
                    cornerRadius = CornerRadius(cloudHeight / 2f, cloudHeight / 2f)
                )
            }
        }

        if (showLightCone) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Yellow.copy(alpha = coneAlpha), Color.Transparent),
                    center = Offset(w, 0f),
                    radius = size.minDimension * 0.6f
                ),
                size = Size(w, h)
            )
        }

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




