package com.example.mygymapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mygymapp.ui.screens.ExercisesScreen
import com.example.mygymapp.ui.screens.WorkoutScreen
import com.example.mygymapp.ui.screens.ProgressScreen
import com.example.mygymapp.ui.screens.ProfileScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "workout",
        modifier = modifier
    ) {
        composable("workout") { WorkoutScreen() }
        composable("progress") { ProgressScreen() }
        composable("exercises") { ExercisesScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}
