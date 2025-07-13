package com.example.mygymapp.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mygymapp.ui.screens.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.ExerciseRepository
import com.example.mygymapp.viewmodel.PreferencesViewModel
import com.example.mygymapp.viewmodel.PreferencesViewModelFactory
import com.example.mygymapp.viewmodel.WorkoutViewModel
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.mygymapp.data.Exercise

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
            composable("preferences") {
                PreferenceScreen(navController)
            }
            composable("suggestedPlans") {
                val context = LocalContext.current
                val repo = remember(context) { PlanRepository(AppDatabase.getDatabase(context).planDao()) }
                val exerciseRepo = remember(context) { ExerciseRepository(AppDatabase.getDatabase(context).exerciseDao()) }
                val viewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModelFactory(repo))
                val prefs by viewModel.prefs.collectAsState()
                var plans by remember { mutableStateOf<List<com.example.mygymapp.data.Plan>>(emptyList()) }
                var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
                val scope = rememberCoroutineScope()
                LaunchedEffect(Unit) {
                    plans = repo.getAllPlans()
                    exercises = exerciseRepo.getAllExercises().first()
                }
                SuggestedPlansScreen(
                    preferences = prefs,
                    allPlans = plans,
                    onPlanSelected = { navController.navigate("setupWeek/${it.planId}") },
                    onBack = { navController.popBackStack() },
                    onGenerate = {
                        scope.launch {
                            val newPlan = repo.generatePlanFromPreferences(prefs, exercises)
                            navController.navigate("setupWeek/${newPlan.plan.planId}")
                        }
                    }
                )
            }
            composable("setupWeek/{planId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("planId")?.toLong() ?: return@composable
                val viewModel: WorkoutViewModel = viewModel()
                SetupWeekScreen(
                    planId = id,
                    onStartWeek = { progress ->
                        viewModel.startWeek(progress)
                        navController.navigate("workout") {
                            popUpTo("exercises") { inclusive = false }
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable("workout") { WorkoutScreen() }
            composable("profile") { ProfileScreen(navController) }
            composable("selectTheme") {
                ThemePickerScreen(onBack = { navController.popBackStack() })
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
