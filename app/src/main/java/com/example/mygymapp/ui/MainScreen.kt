package com.example.mygymapp.ui

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.mygymapp.ui.screens.ExercisesScreen
import com.example.mygymapp.ui.screens.PlansScreen
import com.example.mygymapp.ui.screens.WorkoutScreen
import com.example.mygymapp.ui.screens.ProfileScreen

data class NavTab(val route: String, val label: String, val icon: ImageVector)

val navTabs = listOf(
    NavTab("exercises", "Exercises", Icons.Default.FitnessCenter),
    NavTab("plans", "Plans", Icons.Default.List),
    NavTab("workout", "Workout", Icons.Default.Timeline),
    NavTab("profile", "Profile", Icons.Default.Person)
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                navTabs.forEach { tab ->
                    val selected = currentDestination?.route == tab.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = navTabs.first().route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("exercises") { ExercisesScreen() }
            composable("plans") { PlansScreen() }
            composable("workout") { WorkoutScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}
