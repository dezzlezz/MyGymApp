package com.example.mygymapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mygymapp.ui.screens.ExercisesScreen
import com.example.mygymapp.ui.screens.MainScreen

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = modifier
    ) {
        composable("main") { MainScreen(navController) }
        composable("exercises") { ExercisesScreen(navController) }
    }
}
