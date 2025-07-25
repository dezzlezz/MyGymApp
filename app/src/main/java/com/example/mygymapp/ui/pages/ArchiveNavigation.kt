package com.example.mygymapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ArchiveNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "lines") {
        composable("lines") {
            ArchivePage(onManageExercises = { navController.navigate("exercise_management") })
        }
        composable("exercise_management") {
            ExerciseManagementScreen(navController = navController)
        }
    }
}
