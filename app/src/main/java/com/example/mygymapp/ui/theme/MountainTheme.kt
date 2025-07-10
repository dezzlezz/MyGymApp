package com.example.mygymapp.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.*
import kotlin.random.Random
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.navigation.NavTabs
import com.example.mygymapp.R

private val MountainColorScheme = lightColorScheme(
    primary = MountainBlue,
    onPrimary = SnowWhite,
    secondary = GlacierAccent,
    onSecondary = SnowWhite,
    background = SnowWhite,
    onBackground = MountainBlue,
    surface = SnowWhite,
    onSurface = MountainBlue
)

private val MountainShapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun MountainTheme() {
    val navController = rememberNavController()
    val current by navController.currentBackStackEntryAsState()
    val index = NavTabs.indexOfFirst { it.route == current?.destination?.route }.let { if (it >= 0) it else 0 }

    Crossfade(targetState = MountainColorScheme) { colors ->
        MaterialTheme(colorScheme = colors, shapes = MountainShapes) {
            Box(Modifier.fillMaxSize()) {
                MountainBackground(Modifier.matchParentSize())
                SnowEffect(Modifier.matchParentSize())
                Scaffold(
                    containerColor = Color.Transparent,
                    topBar = {
                        Column(Modifier.statusBarsPadding()) {
                            ParallaxHeader()
                            TabRow(selectedTabIndex = index, containerColor = Color.Transparent) {
                                NavTabs.forEachIndexed { idx, tab ->
                                    Tab(
                                        selected = idx == index,
                                        onClick = {
                                            navController.navigate(tab.route) {
                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                tab.icon,
                                                contentDescription = tab.label,
                                                modifier = Modifier.graphicsLayer { translationY = if (idx % 2 == 0) 0f else 6f }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->
                    AppNavHost(navController, Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun ParallaxHeader(modifier: Modifier = Modifier) {
    val height = 200.dp
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Image(
            painterResource(R.drawable.ic_mountain),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // simple transparent overlay hinting at snow/wind
        Box(Modifier.matchParentSize().background(SnowWhite.copy(alpha = 0.1f)))
    }
}

@Composable
private fun MountainBackground(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val h = size.height
        val w = size.width
        val path = Path().apply {
            moveTo(0f, h * 0.4f)
            lineTo(w * 0.25f, h * 0.2f)
            lineTo(w * 0.5f, h * 0.35f)
            lineTo(w * 0.75f, h * 0.15f)
            lineTo(w, h * 0.3f)
            lineTo(w, 0f)
            lineTo(0f, 0f)
            close()
        }
        drawPath(path, MountainBlue.copy(alpha = 0.3f))
    }
}

private data class Flake(val x: Float, val speed: Float, val size: Float)

@Composable
private fun SnowEffect(modifier: Modifier = Modifier, count: Int = 30) {
    val flakes = remember { List(count) { Flake(Random.nextFloat(), Random.nextFloat() * 0.5f + 0.5f, Random.nextFloat() * 3f + 2f) } }
    val transition = rememberInfiniteTransition(label = "snow")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing))
    )
    Canvas(modifier) {
        flakes.forEach { flake ->
            val y = (anim * flake.speed + flake.x) % 1f
            drawCircle(Color.White.copy(alpha = 0.8f), flake.size, Offset(size.width * flake.x, size.height * y))
        }
    }
}
