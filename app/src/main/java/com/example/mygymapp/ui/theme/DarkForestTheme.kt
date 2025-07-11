package com.example.mygymapp.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

private val ForestDarkColors = darkColorScheme(
    primary = PineGreen,
    onPrimary = LightText,
    secondary = ForestShadow,
    onSecondary = LightText,
    background = NightBlack,
    onBackground = LightText,
    surface = ForestShadow,
    onSurface = LightText
)

private val ForestLightColors = lightColorScheme(
    primary = ForestPrimaryLight,
    onPrimary = NightBlack,
    secondary = ForestSurfaceLight,
    onSecondary = NightBlack,
    background = ForestBackgroundLight,
    onBackground = NightBlack,
    surface = ForestSurfaceLight,
    onSurface = NightBlack
)

@Composable
fun DarkForestTheme(animationsEnabled: Boolean = true, darkMode: Boolean = isSystemInDarkTheme()) {
    val navController = rememberNavController()
    val current by navController.currentBackStackEntryAsState()
    val currentRoute = current?.destination?.route ?: NavTabs.first().route

    val scheme = if (darkMode) ForestDarkColors else ForestLightColors
    val bg = if (darkMode) NightBlack else ForestBackgroundLight

    MaterialTheme(colorScheme = scheme) {
        Box(Modifier.fillMaxSize().background(bg)) {
            ForestScene(Modifier.fillMaxSize(), darkMode)
            Rain(modifier = Modifier.fillMaxSize(), animationsEnabled)
            Fireflies(modifier = Modifier.fillMaxSize(), animationsEnabled)
            Row {
                NavigationRail(containerColor = scheme.surface) {
                    NavTabs.forEach { tab ->
                        NavigationRailItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { androidx.compose.material3.Icon(tab.icon, tab.label) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = PineGreen,
                                unselectedIconColor = FogGray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
                Box(Modifier.weight(1f)) {
                    AppNavHost(navController)
                }
            }
        }
    }
}

private data class Tree(val x: Float, val size: Float)

@Composable
private fun ForestScene(modifier: Modifier = Modifier, darkMode: Boolean) {
    val layers = remember {
        List(3) { layer ->
            val r = Random(layer)
            List(8) { Tree(r.nextFloat(), r.nextFloat()) }
        }
    }
    Canvas(modifier) {
        val width = size.width
        val height = size.height
        val colors = if (darkMode) {
            listOf(ForestShadow, ForestShadow.copy(alpha = 0.8f), ForestShadow.copy(alpha = 0.6f))
        } else {
            listOf(ForestPrimaryLight, ForestPrimaryLight.copy(alpha = 0.8f), ForestPrimaryLight.copy(alpha = 0.6f))
        }
        layers.forEachIndexed { index, trees ->
            val base = height * (0.65f + index * 0.1f)
            trees.forEach { tree ->
                val h = height * (0.25f + tree.size * 0.15f)
                val w = h * 0.4f
                val center = width * tree.x
                val path = Path().apply {
                    moveTo(center, base - h)
                    quadraticBezierTo(center - w * 0.5f, base - h * 0.6f, center, base - h * 0.3f)
                    quadraticBezierTo(center + w * 0.5f, base - h * 0.6f, center, base - h)
                }
                drawPath(path, colors[index])
                drawRect(colors[index], Offset(center - w * 0.1f, base - h * 0.3f), androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.3f))
            }
        }
    }
}

private data class Drop(val x: Float, val speed: Float, val length: Float)

@Composable
private fun Rain(modifier: Modifier = Modifier, animationsEnabled: Boolean, count: Int = 40) {
    val drops = remember { List(count) { Drop(Random.nextFloat(), Random.nextFloat() * 4 + 2, Random.nextFloat() * 0.1f + 0.05f) } }
    val transition = rememberInfiniteTransition(label = "rain")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing))
    )
    Canvas(modifier) {
        drops.forEach { drop ->
            val y = (anim * drop.speed + drop.x) % 1f
            val startY = size.height * y
            val endY = startY + size.height * drop.length
            drawLine(Color.White.copy(alpha = 0.1f), Offset(size.width * drop.x, startY), Offset(size.width * drop.x, endY), strokeWidth = 2f)
        }
    }
}

private data class Firefly(val x: Float, val y: Float, val phase: Float)

@Composable
private fun Fireflies(modifier: Modifier = Modifier, animationsEnabled: Boolean, count: Int = 20) {
    val flies = remember { List(count) { Firefly(Random.nextFloat(), Random.nextFloat() * 0.6f, Random.nextFloat()) } }
    val transition = rememberInfiniteTransition(label = "flies")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart)
    )
    Canvas(modifier) {
        flies.forEach { f ->
            val alpha = abs(sin((time + f.phase) * 2f * PI)).toFloat()
            drawCircle(FireflyYellow.copy(alpha = alpha), radius = 3f, center = Offset(size.width * f.x, size.height * f.y))
        }
    }
}
