package com.example.mygymapp.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.navigation.NavTabs
import kotlin.random.Random

private val MountainColors = lightColorScheme(
    primary = GlacierBlue,
    onPrimary = SnowFlake,
    secondary = DuskViolet,
    onSecondary = SnowFlake,
    background = SnowFlake,
    onBackground = MountainDeepBlue,
    surface = SnowFlake,
    onSurface = MountainDeepBlue
)

@Composable
fun MountainTheme(animationsEnabled: Boolean = true) {
    val navController = rememberNavController()
    val current by navController.currentBackStackEntryAsState()
    val index = NavTabs.indexOfFirst { it.route == current?.destination?.route }.let { if (it >= 0) it else 0 }

    MaterialTheme(colorScheme = MountainColors) {
        Box(Modifier.fillMaxSize().background(MountainDeepBlue)) {
            MountainScene(Modifier.fillMaxSize())
            FogOverlay(Modifier.fillMaxSize(), animationsEnabled)
            androidx.compose.material3.Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    TabRow(selectedTabIndex = index, containerColor = MountainColors.surface) {
                        NavTabs.forEachIndexed { idx, tab ->
                            Tab(selected = idx == index, onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }, icon = { androidx.compose.material3.Icon(tab.icon, tab.label) })
                        }
                    }
                }
            ) { padding ->
                AppNavHost(navController, Modifier.padding(padding))
            }
        }
    }
}

private fun curvedRange(random: Random): List<Pair<Float, Float>> =
    List(6) { it / 5f to (0.4f + random.nextFloat() * 0.2f) }

@Composable
private fun MountainScene(modifier: Modifier = Modifier) {
    val layers = remember {
        listOf(curvedRange(Random(1)), curvedRange(Random(2)))
    }
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val colors = listOf(MountainDeepBlue.copy(alpha = 0.6f), MountainDeepBlue.copy(alpha = 0.4f))
        layers.forEachIndexed { idx, points ->
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(points.first().first * w, points.first().second * h)
                points.drop(1).forEach { (x, y) -> quadraticBezierTo(x * w, y * h - h * 0.05f, x * w, y * h) }
                lineTo(w, h)
                close()
            }
            drawPath(path, colors[idx])
        }
    }
}

@Composable
private fun FogOverlay(modifier: Modifier = Modifier, animationsEnabled: Boolean) {
    val transition = rememberInfiniteTransition(label = "fog")
    val anim by transition.animateFloat(
        initialValue = -1f,
        targetValue = if (animationsEnabled) 1f else -1f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val y = h * 0.5f
        drawRect(
            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                listOf(Color.Transparent, FogLight, Color.Transparent),
                startX = -w + w * anim,
                endX = w + w * anim
            ),
            topLeft = Offset(-w + w * anim, y),
            size = androidx.compose.ui.geometry.Size(w * 2, h * 0.3f)
        )
    }
}
