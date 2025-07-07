package com.example.mygymapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.MyApp
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.ui.screens.AddEditPlanSheet
import com.example.mygymapp.ui.screens.ExercisesScreen
import com.example.mygymapp.ui.screens.PlansScreen
import com.example.mygymapp.ui.screens.ProfileScreen
import com.example.mygymapp.ui.screens.WorkoutScreen
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.example.mygymapp.ui.viewmodel.PlansViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Falls du ein eigenes Theme hast, tausche hier MaterialTheme aus:
            MaterialTheme {
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

    // Bottom Sheet State & Coroutine Scope
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    // Repository & ViewModel
    val plansRepo = PlanRepository(MyApp.database.planDao())
    val plansViewModel: PlansViewModel = viewModel(
        factory = PlansViewModelFactory(plansRepo)
    )

    // Nav Tabs Definition
    val navTabs = listOf(
        NavTab("exercises", "Exercises", Icons.Filled.FitnessCenter),
        NavTab("plans",     "Plans",     Icons.Filled.List),
        NavTab("workout",   "Workout",   Icons.Filled.Timeline),
        NavTab("profile",   "Profile",   Icons.Filled.Person)
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            AddEditPlanSheet(
                initialPlan = Plan(name = "", description = "", iconUri = null, type = com.example.mygymapp.data.PlanType.DAILY),
                initialExercises = emptyList<PlanExerciseCrossRef>(),
                onSave = { plan, refs ->
                    scope.launch { sheetState.hide() }
                    plansViewModel.save(plan, refs)
                },
                onCancel = {
                    scope.launch { sheetState.hide() }
                }
            )
        },
        scrimColor = MaterialTheme.colors.onSurface.copy(alpha = 0.32f)
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { scope.launch { sheetState.show() } },
                    content = { Icon(Icons.Filled.Add, contentDescription = "Add Plan") }
                )
            },
            bottomBar = {
                BottomNavigation {
                    navTabs.forEach { tab ->
                        val selected = currentDestination?.route == tab.route
                        BottomNavigationItem(
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
                composable("plans")     { PlansScreen() }  // no onAddPlan parameter anymore
                composable("workout")   { WorkoutScreen() }
                composable("profile")   { ProfileScreen() }
            }
        }
    }
}

data class NavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)
