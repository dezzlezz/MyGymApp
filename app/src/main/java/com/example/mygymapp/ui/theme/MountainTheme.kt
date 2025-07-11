package com.example.mygymapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
    val current by navController.currentBackStackEntryAsState()
    val index = NavTabs.indexOfFirst { it.route == current?.destination?.route }.let { if (it >= 0) it else 0 }

    val scheme = if (darkMode) MountainDarkColors else MountainLightColors
    val bg = if (darkMode) MountainBackgroundDark else SnowFlake

    MaterialTheme(colorScheme = scheme) {
        Box(Modifier.fillMaxSize().background(bg)) {
            MountainBackground(
                modifier = Modifier.fillMaxSize(),
                darkMode = darkMode,
                animationsEnabled = animationsEnabled
            )
            androidx.compose.material3.Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    TabRow(selectedTabIndex = index, containerColor = scheme.surface) {
                        NavTabs.forEachIndexed { idx, tab ->
                            Tab(selected = idx == index, onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }, icon = { androidx.compose.material3.Icon(tab.icon, tab.label) })
                        }
                    }
                }
            ) { padding ->
                AppNavHost(navController, Modifier.padding(padding))
            }
        }
    }
}
