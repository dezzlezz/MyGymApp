package com.example.mygymapp.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.navigation.NavTabs
import androidx.compose.animation.core.*

private val BeachColorScheme = lightColorScheme(
    primary = Coral,
    onPrimary = DeepBlack,
    secondary = Turquoise,
    onSecondary = DeepBlack,
    background = Sand,
    onBackground = DeepBlack,
    surface = Sand,
    onSurface = DeepBlack
)

private val BeachShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun BeachTheme(animationsEnabled: Boolean = true) {
    val navController = rememberNavController()
    val current by navController.currentBackStackEntryAsState()
    val index = NavTabs.indexOfFirst { it.route == current?.destination?.route }.let { if (it >= 0) it else 0 }

    Crossfade(targetState = BeachColorScheme) { colors ->
        MaterialTheme(colorScheme = colors, shapes = BeachShapes) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(colors = listOf(SkyBlue, Sand))
                    )
            ) {
                WaveBackground(Modifier.matchParentSize(), animationsEnabled)
                Scaffold(
                    containerColor = Color.Transparent,
                    bottomBar = {
                        NavigationBar(containerColor = Sand) {
                            NavTabs.forEachIndexed { idx, tab ->
                                NavigationBarItem(
                                    selected = idx == index,
                                    onClick = {
                                        navController.navigate(tab.route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                                    label = { Text(tab.label) }
                                )
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
private fun WaveBackground(modifier: Modifier = Modifier, animationsEnabled: Boolean) {
    val transition = rememberInfiniteTransition(label = "waves")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val y1 = h * 0.3f
        val y2 = h * 0.32f
        val dx = w * offset
        val path1 = Path().apply {
            moveTo(-w + dx, y1)
            cubicTo(-w * 0.5f + dx, y1 + 20f, w * 0.5f + dx, y1 - 20f, w + dx, y1)
            lineTo(w + dx, 0f)
            lineTo(-w + dx, 0f)
            close()
        }
        val path2 = Path().apply {
            moveTo(-w + dx * 1.2f, y2)
            cubicTo(-w * 0.5f + dx * 1.2f, y2 + 20f, w * 0.5f + dx * 1.2f, y2 - 20f, w + dx * 1.2f, y2)
            lineTo(w + dx * 1.2f, 0f)
            lineTo(-w + dx * 1.2f, 0f)
            close()
        }
        drawRect(Sand, topLeft = Offset(0f, y2), size = androidx.compose.ui.geometry.Size(w, h - y2))
        drawPath(path1, SkyBlue)
        drawPath(path2, Turquoise.copy(alpha = 0.5f))
    }
}
