package com.example.mygymapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mygymapp.ui.theme.MyGymAppTheme
import com.example.mygymapp.ui.theme.AccentGreen
import com.example.mygymapp.ui.theme.InactiveGray
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.ui.screens.ExercisesScreen
import com.example.mygymapp.ui.screens.PlansScreen
import com.example.mygymapp.ui.screens.ProfileScreen
import com.example.mygymapp.ui.screens.WorkoutScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // default to dark theme for this preview activity
            MyGymAppTheme(darkTheme = true) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentDestination = navController
        .currentBackStackEntryAsState().value?.destination

    // Nav Tabs Definition
    val navTabs = listOf(
        NavTab("exercises", "Exercises", Icons.Outlined.FitnessCenter),
        NavTab("plans",     "Plans",     Icons.Outlined.List),
        NavTab("workout",   "Workout",   Icons.Outlined.Timeline),
        NavTab("profile",   "Profile",   Icons.Outlined.Person)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
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
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentGreen,
                            selectedTextColor = AccentGreen,
                            unselectedIconColor = InactiveGray,
                            unselectedTextColor = InactiveGray,
                            indicatorColor = Color.Transparent
                        )
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
            composable("plans")     { PlansScreen() }
            composable("workout")   { WorkoutScreen() }
            composable("profile")   { ProfileScreen() }
        }
    }
}

data class NavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)
