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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
fun DarkForestTheme(content: @Composable () -> Unit) {
    Crossfade(targetState = true) { _ ->
        MaterialTheme(colorScheme = DarkForestColors, shapes = DarkShapes) {
            Box(Modifier.fillMaxSize()) {
                content()
                RainEffect(Modifier.fillMaxSize())
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
private fun RainEffect(modifier: Modifier = Modifier, count: Int = 40, dropColor: Color = Color.White.copy(alpha = 0.15f), stroke: Dp = 2.dp) {
    val drops = remember { List(count) { Drop(Random.nextFloat(), Random.nextFloat() * 4 + 2, Random.nextFloat() * 0.1f + 0.05f) } }
    val transition = rememberInfiniteTransition(label = "rain")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
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

