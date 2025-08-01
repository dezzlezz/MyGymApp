package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

@Composable
fun ArchiveNavigation(onNavigateToEntry: () -> Unit = {}) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "line_paragraph") {
        composable("line_paragraph") {
            LineParagraphPage()
        }
        composable("exercise_management") {
            ExerciseManagementScreen(navController = navController)
        }
        composable(
            route = "exercise_editor?editId={editId}",
            arguments = listOf(navArgument("editId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val editIdArg = backStackEntry.arguments?.getLong("editId")?.takeIf { it != -1L }
            MovementEntryPage(navController = navController, editId = editIdArg, userCategories = com.example.mygymapp.model.CustomCategories.list)
        }
        composable("movement_editor") {
            MovementEntryPage(navController = navController, userCategories = com.example.mygymapp.model.CustomCategories.list)
        }
        composable("register_editor") {
            RegisterManagementPage()
        }
    }
}
