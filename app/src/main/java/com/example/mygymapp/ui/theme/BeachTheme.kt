package com.example.mygymapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
                    NavigationBar(containerColor = scheme.surface) {
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
