package com.example.mygymapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.ui.backgrounds.MountainBackground

private val MountainLightColors = lightColorScheme(
    primary = GlacierBlue,
    onPrimary = SnowFlake,
    secondary = DuskViolet,
    onSecondary = SnowFlake,
    background = SnowFlake,
    onBackground = MountainDeepBlue,
    surface = SnowFlake,
    onSurface = MountainDeepBlue
)

private val MountainDarkColors = darkColorScheme(
    primary = GlacierBlue,
    onPrimary = SnowFlake,
    secondary = DuskViolet,
    onSecondary = SnowFlake,
    background = MountainBackgroundDark,
    onBackground = SnowFlake,
    surface = MountainSurfaceDark,
    onSurface = SnowFlake
)

@Composable
fun MountainTheme(animationsEnabled: Boolean = true, darkMode: Boolean = isSystemInDarkTheme()) {
    val navController = rememberNavController()

    val scheme = if (darkMode) MountainDarkColors else MountainLightColors
    val bg = if (darkMode) MountainBackgroundDark else SnowFlake

    MaterialTheme(colorScheme = scheme) {
        Box(
            Modifier
                .fillMaxSize()
                .background(bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MountainBackground(
                modifier = Modifier.fillMaxSize(),
                darkMode = darkMode,
                animationsEnabled = animationsEnabled
            )
            AppNavHost(navController)
        }
    }
}

@Composable
private fun StarTabIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2f, 0f)
            lineTo(w * 0.62f, h * 0.35f)
            lineTo(w, h * 0.38f)
            lineTo(w * 0.7f, h * 0.62f)
            lineTo(w * 0.82f, h)
            lineTo(w / 2f, h * 0.78f)
            lineTo(w * 0.18f, h)
            lineTo(w * 0.3f, h * 0.62f)
            lineTo(0f, h * 0.38f)
            lineTo(w * 0.38f, h * 0.35f)
            close()
        }
        drawPath(path, color)
    }
}
