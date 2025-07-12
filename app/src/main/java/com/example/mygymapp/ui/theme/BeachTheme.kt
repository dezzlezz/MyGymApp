package com.example.mygymapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.navigation.NavTabs
import com.example.mygymapp.ui.backgrounds.BeachBackground

private val BeachLightColors = lightColorScheme(
    primary = SunsetCoral,
    onPrimary = NightBlack,
    secondary = WaveBlue,
    onSecondary = NightBlack,
    background = BeachSand,
    onBackground = NightBlack,
    surface = BeachSand,
    onSurface = NightBlack
)

private val BeachDarkColors = darkColorScheme(
    primary = SunsetCoral,
    onPrimary = NightBlack,
    secondary = WaveBlue,
    onSecondary = NightBlack,
    background = BeachBackgroundDark,
    onBackground = SnowFlake,
    surface = BeachSandDark,
    onSurface = SnowFlake
)

@Composable
fun BeachTheme(animationsEnabled: Boolean = true, darkMode: Boolean = isSystemInDarkTheme()) {
    val navController = rememberNavController()
    val current by navController.currentBackStackEntryAsState()
    val index = NavTabs.indexOfFirst { it.route == current?.destination?.route }.let { if (it >= 0) it else 0 }

    val scheme = if (darkMode) BeachDarkColors else BeachLightColors
    val sand = if (darkMode) BeachSandDark else BeachSand

    MaterialTheme(colorScheme = scheme) {
        Box(Modifier.fillMaxSize().background(sand)) {
            BeachBackground(
                modifier = Modifier.fillMaxSize(),
                darkMode = darkMode,
                animationsEnabled = animationsEnabled
            )
            androidx.compose.material3.Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    val icons: List<@Composable () -> Unit> = listOf(
                        { CrabIcon(modifier = Modifier.size(24.dp), color = scheme.onSurface) },
                        { ShellIcon(modifier = Modifier.size(24.dp), color = scheme.onSurface) },
                        { StarfishIcon(modifier = Modifier.size(24.dp), color = scheme.onSurface) },
                        { FishIcon(modifier = Modifier.size(24.dp), color = scheme.onSurface) }
                    )
                    NavigationBar(containerColor = scheme.surface.copy(alpha = 0.8f)) {
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
                                icon = { icons[idx]() },
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
private fun CrabIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val bodyR = w * 0.25f
        drawCircle(color, radius = bodyR, center = Offset(w/2f, h*0.55f))
        drawCircle(color, radius = bodyR*0.6f, center = Offset(w*0.25f, h*0.35f))
        drawCircle(color, radius = bodyR*0.6f, center = Offset(w*0.75f, h*0.35f))
        val legY = h*0.7f
        drawLine(color, Offset(w*0.3f, legY), Offset(w*0.2f, h*0.9f), strokeWidth = 2f)
        drawLine(color, Offset(w*0.7f, legY), Offset(w*0.8f, h*0.9f), strokeWidth = 2f)
    }
}

@Composable
private fun ShellIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w/2f, h*0.2f)
            cubicTo(w*0.1f, h*0.4f, w*0.1f, h*0.8f, w/2f, h*0.9f)
            cubicTo(w*0.9f, h*0.8f, w*0.9f, h*0.4f, w/2f, h*0.2f)
            close()
        }
        drawPath(path, color)
        drawLine(color, Offset(w*0.2f, h*0.6f), Offset(w*0.8f, h*0.6f), strokeWidth = 2f)
    }
}

@Composable
private fun StarfishIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w/2f, h*0.1f)
            lineTo(w*0.65f, h*0.4f)
            lineTo(w*0.95f, h*0.45f)
            lineTo(w*0.72f, h*0.65f)
            lineTo(w*0.8f, h*0.95f)
            lineTo(w/2f, h*0.8f)
            lineTo(w*0.2f, h*0.95f)
            lineTo(w*0.28f, h*0.65f)
            lineTo(w*0.05f, h*0.45f)
            lineTo(w*0.35f, h*0.4f)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
private fun FishIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val body = androidx.compose.ui.graphics.Path().apply {
            moveTo(w*0.2f, h*0.5f)
            quadraticBezierTo(w*0.4f, h*0.2f, w*0.7f, h*0.5f)
            quadraticBezierTo(w*0.4f, h*0.8f, w*0.2f, h*0.5f)
            close()
        }
        val tail = androidx.compose.ui.graphics.Path().apply {
            moveTo(w*0.7f, h*0.5f)
            lineTo(w*0.95f, h*0.35f)
            lineTo(w*0.95f, h*0.65f)
            close()
        }
        drawPath(body, color)
        drawPath(tail, color)
        drawCircle(Color.White, radius = w*0.05f, center = Offset(w*0.4f, h*0.45f))
    }
}
