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

    NavHost(navController = navController, startDestination = "line_paragraph?tab=0") {
        composable(
            route = "line_paragraph?tab={tab}",
            arguments = listOf(navArgument("tab") {
                type = NavType.IntType
                defaultValue = 0
            })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0
            LineParagraphPage(navController = navController, startTab = tab)
        }
        composable(
            route = "paragraph_editor?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val editIdArg = backStackEntry.arguments?.getLong("id")?.takeIf { it != -1L }
            ParagraphEditorScreen(navController = navController, editId = editIdArg)
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
