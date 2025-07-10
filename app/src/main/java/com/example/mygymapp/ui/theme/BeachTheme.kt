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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.navigation.NavTabs

private val BeachColors = lightColorScheme(
    primary = SunsetCoral,
    onPrimary = NightBlack,
    secondary = WaveBlue,
    onSecondary = NightBlack,
    background = BeachSand,
    onBackground = NightBlack,
    surface = BeachSand,
    onSurface = NightBlack
)

@Composable
fun BeachTheme(animationsEnabled: Boolean = true) {
    val navController = rememberNavController()
    val current by navController.currentBackStackEntryAsState()
    val index = NavTabs.indexOfFirst { it.route == current?.destination?.route }.let { if (it >= 0) it else 0 }

    MaterialTheme(colorScheme = BeachColors) {
        Box(Modifier.fillMaxSize().background(BeachSand)) {
            BeachScene(Modifier.fillMaxSize(), animationsEnabled)
            androidx.compose.material3.Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    NavigationBar(containerColor = BeachColors.surface) {
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
                                icon = { androidx.compose.material3.Icon(tab.icon, tab.label) },
                                label = { androidx.compose.material3.Text(tab.label) }
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

@Composable
private fun BeachScene(modifier: Modifier = Modifier, animationsEnabled: Boolean) {
    val transition = rememberInfiniteTransition(label = "wave")
    val anim by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 1f else 0f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing))
    )
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val horizon = h * 0.35f
        drawRect(BeachSand, Offset(0f, horizon), androidx.compose.ui.geometry.Size(w, h - horizon))
        val dy = h * 0.05f
        val dx = w * anim
        val wave1 = Path().apply {
            moveTo(-w + dx, horizon)
            cubicTo(-w * 0.5f + dx, horizon + dy, w * 0.5f + dx, horizon - dy, w + dx, horizon)
            lineTo(w + dx, horizon - dy * 2)
            lineTo(-w + dx, horizon - dy * 2)
            close()
        }
        val wave2 = Path().apply {
            moveTo(-w + dx * 1.3f, horizon - dy * 1.5f)
            cubicTo(-w * 0.4f + dx * 1.3f, horizon - dy * 0.5f, w * 0.6f + dx * 1.3f, horizon - dy * 2.5f, w + dx * 1.3f, horizon - dy * 1.5f)
            lineTo(w + dx * 1.3f, horizon - dy * 3)
            lineTo(-w + dx * 1.3f, horizon - dy * 3)
            close()
        }
        drawPath(wave1, WaveBlue)
        drawPath(wave2, SeaFoam.copy(alpha = 0.7f))
    }
}
