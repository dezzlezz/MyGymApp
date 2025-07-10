package com.example.mygymapp.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.navigation.NavTabs
import kotlin.random.Random

private val DarkForestColors = darkColorScheme(
    primary = NatureGreen,
    onPrimary = OnDark,
    secondary = DarkGreen,
    onSecondary = OnDark,
    background = DeepBlack,
    onBackground = OnDark,
    surface = DarkGreen,
    onSurface = OnDark
)

private val DarkShapes = androidx.compose.material3.Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun DarkForestTheme(animationsEnabled: Boolean = true) {
    val navController = rememberNavController()
    val current = navController.currentBackStackEntryAsState().value?.destination?.route
    val items = remember { NavTabs.map { DarkNavItem(it.route, it.label, it.icon) } }

    Crossfade(targetState = true) { _ ->
        MaterialTheme(colorScheme = DarkForestColors, shapes = DarkShapes) {
            Box(Modifier.fillMaxSize()) {
                ForestBackground(Modifier.matchParentSize())
                RainEffect(Modifier.matchParentSize(), animationsEnabled = animationsEnabled)
                androidx.compose.foundation.layout.Row {
                    DarkForestSidebar(
                        items = items,
                        current = current ?: items.first().route,
                        onSelect = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    Box(Modifier.weight(1f)) {
                        AppNavHost(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun DarkForestSidebar(
    items: List<DarkNavItem>,
    current: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier.background(DarkForestColors.surface)) {
        items.forEach { item ->
            NavigationRailItem(
                selected = current == item.route,
                onClick = { onSelect(item.route) },
                icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = NatureGreen,
                    unselectedIconColor = InactiveGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class DarkNavItem(val route: String, val label: String, val icon: ImageVector)

private data class Drop(val x: Float, val speed: Float, val length: Float)

@Composable
private fun ForestBackground(modifier: Modifier = Modifier) {
    val layers = remember {
        List(3) { layer ->
            val rand = Random(layer)
            List(10) { Pair(rand.nextFloat(), rand.nextFloat() * 0.3f + 0.2f) }
        }
    }
    Canvas(modifier) {
        val h = size.height
        val w = size.width
        val colors = listOf(DarkGreen, DarkGreen.copy(alpha = 0.7f), DarkGreen.copy(alpha = 0.5f))
        layers.forEachIndexed { index, points ->
            val baseY = h * (0.7f + index * 0.1f)
            val path = Path().apply {
                moveTo(0f, h)
                lineTo(0f, baseY)
                points.forEach { (x, peak) ->
                    lineTo(w * x, baseY)
                    lineTo(w * x + w * 0.05f, baseY - h * peak)
                    lineTo(w * x + w * 0.1f, baseY)
                }
                lineTo(w, baseY)
                lineTo(w, h)
                close()
            }
            drawPath(path, colors[index])
        }
    }
}

@Composable
private fun RainEffect(modifier: Modifier = Modifier, count: Int = 40, dropColor: Color = Color.White.copy(alpha = 0.15f), stroke: Dp = 2.dp, animationsEnabled: Boolean) {
    val drops = remember { List(count) { Drop(Random.nextFloat(), Random.nextFloat() * 4 + 2, Random.nextFloat() * 0.1f + 0.05f) } }
    val transition = rememberInfiniteTransition(label = "rain")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1500, easing = LinearEasing))
    )
    Canvas(modifier = modifier) {
        drops.forEach { drop ->
            val y = (anim * drop.speed + drop.x) % 1f
            val startY = size.height * y
            val endY = startY + size.height * drop.length
            drawLine(
                dropColor,
                start = Offset(size.width * drop.x, startY),
                end = Offset(size.width * drop.x, endY),
                strokeWidth = stroke.toPx()
            )
        }
    }
}

