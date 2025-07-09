package com.example.mygymapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mygymapp.ui.screens.*
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timeline
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.ui.theme.AccentGreen
import com.example.mygymapp.ui.theme.InactiveGray

/** Simple navigation graph extracted from MainScreen */
@Composable
fun AppNavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val navTabs = listOf(
        NavTab("exercises", "Exercises", Icons.Outlined.FitnessCenter),
        NavTab("plans", "Plans", Icons.Outlined.List),
        NavTab("workout", "Workout", Icons.Outlined.Timeline),
        NavTab("profile", "Profile", Icons.Outlined.Person)
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
            modifier = modifier.padding(innerPadding)
        ) {
            composable("exercises") {
                ExercisesScreen(
                    navController = navController,
                    onEditExercise = { navController.navigate("editExercise/$it") }
                )
            }
            composable("addExercise") {
                AddExerciseScreen(
                    onDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable("editExercise/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toLong() ?: return@composable
                EditExerciseScreen(
                    exerciseId = id,
                    onDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable("plans") { PlansScreen(navController) }
            composable("editDailyPlan/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toLong() ?: return@composable
                EditDailyPlanScreen(planId = id, navController = navController)
            }
            composable("editWeeklyPlan/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toLong() ?: return@composable
                EditWeeklyPlanScreen(planId = id, navController = navController)
            }
            composable("planDetail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toLong() ?: return@composable
                PlanDetailScreen(planId = id, navController = navController)
            }
            composable("workout") { WorkoutScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

data class NavTab(
    val route: String,
    val label: String,
    val icon: ImageVector)