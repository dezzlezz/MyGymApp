package com.example.mygymapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
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
import com.example.mygymapp.model.AppTheme

/** Simple navigation graph extracted from MainScreen */
@Composable
fun AppNavGraph(modifier: Modifier = Modifier, theme: AppTheme = AppTheme.Mountains) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val navTabs = listOf(
        NavTab("exercises", "Exercises", Icons.Outlined.FitnessCenter),
        NavTab("plans", "Plans", Icons.Outlined.List),
        NavTab("workout", "Workout", Icons.Outlined.Timeline),
        NavTab("profile", "Profile", Icons.Outlined.Person)
    )

    val navHost: @Composable (Modifier) -> Unit = { extraModifier ->
        NavHost(
            navController = navController,
            startDestination = navTabs.first().route,
            modifier = modifier.then(extraModifier)
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
            composable("profile") { ProfileScreen(navController) }
            composable("selectTheme") {
                ThemePickerScreen(onBack = { navController.popBackStack() })
            }
        }
    }

    when (theme) {
        AppTheme.DarkForest -> {
            Row {
                NavigationRail {
                    navTabs.forEach { tab ->
                        val selected = currentDestination?.route == tab.route
                        NavigationRailItem(
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
                            alwaysShowLabel = true,
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = AccentGreen,
                                selectedTextColor = AccentGreen,
                                unselectedIconColor = InactiveGray,
                                unselectedTextColor = InactiveGray
                            )
                        )
                    }
                }
                navHost(Modifier.weight(1f))
            }
        }
        AppTheme.Mountains -> {
            val index = navTabs.indexOfFirst { it.route == currentDestination?.route }.let { if (it >= 0) it else 0 }
            Scaffold(
                topBar = {
                    TabRow(
                        selectedTabIndex = index,
                        containerColor = MaterialTheme.colorScheme.background,
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        navTabs.forEachIndexed { idx, tab ->
                            Tab(
                                selected = idx == index,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(tab.icon, contentDescription = tab.label) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                navHost(Modifier.padding(innerPadding))
            }
        }
        AppTheme.Beach -> {
            val index = navTabs.indexOfFirst { it.route == currentDestination?.route }.let { if (it >= 0) it else 0 }
            Scaffold(
                topBar = {
                    TabRow(
                        selectedTabIndex = index,
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        navTabs.forEachIndexed { idx, tab ->
                            Tab(
                                selected = idx == index,
                                onClick = {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                text = { Text(tab.label) },
                                icon = { Icon(tab.icon, contentDescription = tab.label) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                navHost(Modifier.padding(innerPadding))
            }
        }
    }
}

data class NavTab(
    val route: String,
    val label: String,
    val icon: ImageVector)