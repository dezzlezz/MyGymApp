package com.example.mygymapp.ui.theme

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.navigation.AppNavHost
import com.example.mygymapp.navigation.NavTabs
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
    val current by navController.currentBackStackEntryAsState()
    val currentRoute = current?.destination?.route ?: NavTabs.first().route

    val scheme = if (darkMode) ForestDarkColors else ForestLightColors
    val bg = if (darkMode) NightBlack else ForestBackgroundLight

    MaterialTheme(colorScheme = scheme) {
        Box(Modifier.fillMaxSize().background(bg)) {
            DarkForestBackground(
                modifier = Modifier.fillMaxSize(),
                darkMode = darkMode,
                animationsEnabled = animationsEnabled
            )
            Row {
                NavigationRail(containerColor = scheme.surface.copy(alpha = 0.6f)) {
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
                            icon = {
                                val selected = currentRoute == tab.route
                                androidx.compose.material3.Icon(
                                    tab.icon,
                                    tab.label,
                                    tint = if (selected) scheme.primary else scheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedTextColor = scheme.primary,
                                unselectedTextColor = scheme.onSurface.copy(alpha = 0.6f)
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

