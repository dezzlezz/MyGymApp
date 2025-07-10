package com.example.mygymapp.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mygymapp.ui.screens.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timeline

/** Simple navigation graph extracted from MainScreen */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val navTabs = NavTabs

    NavHost(
        navController = navController,
        startDestination = navTabs.first().route,
        modifier = modifier
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

}

data class NavTab(
    val route: String,
    val label: String,
    val icon: ImageVector)

val NavTabs = listOf(
    NavTab("exercises", "Exercises", Icons.Outlined.FitnessCenter),
    NavTab("plans", "Plans", Icons.AutoMirrored.Outlined.List),
    NavTab("workout", "Workout", Icons.Outlined.Timeline),
    NavTab("profile", "Profile", Icons.Outlined.Person)
)
