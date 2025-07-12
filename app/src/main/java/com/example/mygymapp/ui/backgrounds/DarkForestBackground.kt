package com.example.mygymapp.ui.backgrounds

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
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
import com.example.mygymapp.ui.theme.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun DarkForestBackground(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    animationsEnabled: Boolean = true
) {
    Box(modifier) {
        SkyGradient(Modifier.fillMaxSize(), darkMode)
        TreeLayers(Modifier.fillMaxSize(), darkMode, animationsEnabled)
        RainOverlay(Modifier.fillMaxSize(), animationsEnabled)
        FirefliesOverlay(Modifier.fillMaxSize(), animationsEnabled)
        GroundFog(Modifier.fillMaxSize())
        DimOverlay(Modifier.fillMaxSize(), darkMode)
    }
}

private data class Tree(
    val x: Float,
    val size: Float,
    val phase: Float,
    val width: Float
)

@Composable
private fun TreeLayers(modifier: Modifier, darkMode: Boolean, animationsEnabled: Boolean) {
    val layers = remember {
        List(3) { layer ->
            val rand = Random(layer + 10)
            List(12) {
                Tree(
                    rand.nextFloat(),
                    rand.nextFloat(),
                    rand.nextFloat() * (2f * PI).toFloat(),
                    0.8f + rand.nextFloat() * 0.4f
                )
            }
        }
    }
    val windTrans = rememberInfiniteTransition(label = "wind")
    val windPhase by windTrans.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) (2f * PI).toFloat() else 0f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing))
    )

    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val ground = h * 0.8f
        val colors = if (darkMode) {
            listOf(ForestShadow, ForestShadow.copy(alpha = 0.8f), ForestShadow.copy(alpha = 0.6f))
        } else {
            listOf(ForestPrimaryLight, ForestPrimaryLight.copy(alpha = 0.8f), ForestPrimaryLight.copy(alpha = 0.6f))
        }
        layers.forEachIndexed { index, trees ->
            trees.forEach { tree ->
                val baseX = tree.x * w
                drawTree(
                    x = baseX,
                    ground = ground,
                    scale = tree.size,
                    phase = tree.phase,
                    width = tree.width,
                    wind = windPhase,
                    color = colors[index]
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTree(
    x: Float,
    ground: Float,
    scale: Float,
    phase: Float,
    width: Float,
    wind: Float,
    color: Color
) {
    val h = this.size.height * (0.18f + scale * 0.35f)
    val trunkW = h * 0.1f * width
    val canopyH = h * (0.6f + width * 0.2f)
    val canopyW = trunkW * 6f * width
    val trunkTop = ground - h
    val sway = kotlin.math.sin(wind + phase) * trunkW * 0.4f
    // trunk
    drawRect(color, Offset(x - trunkW / 2 + sway * 0.2f, trunkTop), androidx.compose.ui.geometry.Size(trunkW, h))
    // canopy path sways with wind
    val path = Path().apply {
        moveTo(x + sway, trunkTop - canopyH)
        cubicTo(x - canopyW * 0.5f + sway, trunkTop - canopyH * 0.4f, x - canopyW * 0.3f + sway, trunkTop + canopyH * 0.2f, x + sway, trunkTop + canopyH * 0.3f)
        cubicTo(x + canopyW * 0.3f + sway, trunkTop + canopyH * 0.2f, x + canopyW * 0.5f + sway, trunkTop - canopyH * 0.4f, x + sway, trunkTop - canopyH)
        close()
    }
    drawPath(path, color)
}

private data class Drop(val x: Float, val speed: Float, val length: Float)

@Composable
private fun RainOverlay(modifier: Modifier, animationsEnabled: Boolean, count: Int = 40) {
    val drops = remember { List(count) { Drop(Random.nextFloat(), 0.5f + Random.nextFloat(), Random.nextFloat() * 0.05f + 0.03f) } }
    val transition = rememberInfiniteTransition(label = "rain")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drops.forEach { drop ->
            val y = (anim * drop.speed + drop.x) % 1f
            drawLine(Color.White.copy(alpha = 0.1f), Offset(w * drop.x, h * y), Offset(w * drop.x, h * y + h * drop.length), strokeWidth = 1.5f)
        }
    }
}

private data class Firefly(val x: Float, val y: Float, val phase: Float)

@Composable
private fun FirefliesOverlay(modifier: Modifier, animationsEnabled: Boolean, count: Int = 25) {
    val flies = remember { List(count) { Firefly(Random.nextFloat(), Random.nextFloat() * 0.6f, Random.nextFloat()) } }
    val transition = rememberInfiniteTransition(label = "flies")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart)
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        flies.forEach { f ->
            val alpha = abs(sin((time + f.phase) * 2f * PI)).toFloat()
            drawCircle(FireflyYellow.copy(alpha = alpha), radius = 3f, center = Offset(w * f.x, h * f.y))
        }
    }
}

@Composable
private fun GroundFog(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val base = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(Color.Transparent, FogLight.copy(alpha = 0.35f)),
            startY = h * 0.6f,
            endY = h
        )
        drawRect(base, size = size)
        val plume = androidx.compose.ui.graphics.Brush.radialGradient(
            colors = listOf(FogLight.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(w / 2f, h * 0.85f),
            radius = h * 0.4f
        )
        drawRect(plume, size = size)
    }
}

@Composable
private fun SkyGradient(modifier: Modifier, darkMode: Boolean) {
    Canvas(modifier) {
        val colors = if (darkMode) {
            listOf(NightBlack, ForestShadow)
        } else {
            listOf(ForestBackgroundLight, ForestPrimaryLight)
        }
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(colors),
            size = size
        )
    }
}

@Composable
private fun DimOverlay(modifier: Modifier, darkMode: Boolean) {
    Canvas(modifier) {
        val fade = if (darkMode) Color.Black else Color.White
        drawRect(fade.copy(alpha = 0.3f), size = size)
    }
}
