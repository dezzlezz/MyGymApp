package com.example.mygymapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.ui.backgrounds.DarkForestBackground

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

    val scheme = if (darkMode) ForestDarkColors else ForestLightColors
    val bg = if (darkMode) NightBlack else ForestBackgroundLight

    MaterialTheme(colorScheme = scheme) {
        Box(
            Modifier
                .fillMaxSize()
                .background(bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            DarkForestBackground(
                modifier = Modifier.fillMaxSize(),
                darkMode = darkMode,
                animationsEnabled = animationsEnabled
            )
            AppNavHost(navController)
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTrunk(color: Color) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.45f, 0f)
        cubicTo(w * 0.6f, h * 0.25f, w * 0.6f, h * 0.75f, w * 0.45f, h)
        lineTo(w * 0.55f, h)
        cubicTo(w * 0.4f, h * 0.75f, w * 0.4f, h * 0.25f, w * 0.55f, 0f)
        close()
    }
    drawPath(path, color)
}

@Composable
private fun LeafIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            quadraticBezierTo(w * 0.2f, h * 0.3f, w * 0.4f, h * 0.9f)
            quadraticBezierTo(w * 0.8f, h * 0.7f, w * 0.5f, h * 0.1f)
            close()
        }
        drawPath(path, color)
        drawLine(color, Offset(w * 0.5f, h * 0.2f), Offset(w * 0.5f, h * 0.85f), strokeWidth = 2f)
    }
}

@Composable
private fun BranchIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawLine(color, Offset(w * 0.5f, h * 0.1f), Offset(w * 0.5f, h * 0.9f), strokeWidth = 3f)
        drawLine(color, Offset(w * 0.5f, h * 0.4f), Offset(w * 0.75f, h * 0.2f), strokeWidth = 3f)
        drawLine(color, Offset(w * 0.5f, h * 0.6f), Offset(w * 0.75f, h * 0.8f), strokeWidth = 3f)
    }
}

@Composable
private fun PineIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.8f, h * 0.6f)
            lineTo(w * 0.2f, h * 0.6f)
            close()
            moveTo(w * 0.5f, h * 0.4f)
            lineTo(w * 0.75f, h * 0.9f)
            lineTo(w * 0.25f, h * 0.9f)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
private fun FireflyIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawCircle(color, radius = w * 0.2f, center = Offset(w * 0.5f, h * 0.5f))
        drawCircle(color, radius = w * 0.1f, center = Offset(w * 0.35f, h * 0.4f))
        drawCircle(color, radius = w * 0.1f, center = Offset(w * 0.65f, h * 0.4f))
    }
}

