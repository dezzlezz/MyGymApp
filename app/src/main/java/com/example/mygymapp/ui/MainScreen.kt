package com.example.mygymapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
mport androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mygymapp.ui.theme.MyGymAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.ui.screens.ExercisesScreen
import com.example.mygymapp.ui.screens.PlansScreen
import com.example.mygymapp.ui.screens.ProfileScreen
import com.example.mygymapp.ui.screens.WorkoutScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyGymAppTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val currentDestination = navController
        .currentBackStackEntryAsState().value?.destination

    val scope = rememberCoroutineScope()

    // Nav Tabs Definition
    val navTabs = listOf(
        NavTab("exercises", "Exercises", Icons.Filled.FitnessCenter),
        NavTab("plans",     "Plans",     Icons.Filled.List),
        NavTab("workout",   "Workout",   Icons.Filled.Timeline),
        NavTab("profile",   "Profile",   Icons.Filled.Person)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
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
