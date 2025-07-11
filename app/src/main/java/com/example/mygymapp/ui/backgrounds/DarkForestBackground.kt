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
        TreeLayers(Modifier.fillMaxSize(), darkMode, animationsEnabled)
        RainOverlay(Modifier.fillMaxSize(), animationsEnabled)
        FirefliesOverlay(Modifier.fillMaxSize(), animationsEnabled)
        GroundFog(Modifier.fillMaxSize())
    }
}

private data class Tree(val x: Float, val size: Float)

@Composable
private fun TreeLayers(modifier: Modifier, darkMode: Boolean, animationsEnabled: Boolean) {
    val layers = remember {
        List(3) { layer ->
            val rand = Random(layer + 10)
            List(12) { Tree(rand.nextFloat(), rand.nextFloat()) }
        }
    }
    val shift = rememberInfiniteTransition(label = "parallax")
    val anim by shift.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(
            tween(20000, easing = LinearEasing),
            RepeatMode.Restart
        )
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
            val dx = w * anim * (index + 1) * 0.1f
            trees.forEach { tree ->
                val baseX = (tree.x * w + dx) % w
                drawTree(baseX, ground, tree.size, colors[index])
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTree(x: Float, ground: Float, size: Float, color: Color) {
    val h = size * size.height * 0.3f + size.height * 0.15f
    val trunkW = h * 0.12f
    val canopyH = h * 0.7f
    val trunkTop = ground - h
    // trunk
    drawRect(color, Offset(x - trunkW/2, trunkTop), androidx.compose.ui.geometry.Size(trunkW, h))
    // canopy path with curves
    val path = Path().apply {
        moveTo(x, trunkTop - canopyH)
        cubicTo(x - trunkW*3, trunkTop - canopyH*0.5f, x - trunkW*2, trunkTop + canopyH*0.3f, x, trunkTop + canopyH*0.2f)
        cubicTo(x + trunkW*2, trunkTop + canopyH*0.3f, x + trunkW*3, trunkTop - canopyH*0.5f, x, trunkTop - canopyH)
        close()
    }
    drawPath(path, color)
}

private data class Drop(val x: Float, val speed: Float, val length: Float)

@Composable
private fun RainOverlay(modifier: Modifier, animationsEnabled: Boolean, count: Int = 60) {
    val drops = remember { List(count) { Drop(Random.nextFloat(), Random.nextFloat() * 4 + 2, Random.nextFloat() * 0.1f + 0.05f) } }
    val transition = rememberInfiniteTransition(label = "rain")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drops.forEach { drop ->
            val y = (anim * drop.speed + drop.x) % 1f
            drawLine(Color.White.copy(alpha = 0.15f), Offset(w * drop.x, h * y), Offset(w * drop.x, h * y + h * drop.length), strokeWidth = 2f)
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
        val gradient = androidx.compose.ui.graphics.Brush.verticalGradient(
            listOf(Color.Transparent, FogLight.copy(alpha = 0.3f), Color.Transparent),
            startY = size.height * 0.6f,
            endY = size.height
        )
        drawRect(gradient, size = size)
    }
}
