package com.example.mygymapp.ui.pages

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.viewmodel.ExerciseViewModel

@Composable
fun ArchiveNavigation(onNavigateToEntry: () -> Unit = {}) {
    val navController = rememberNavController()
    val exerciseViewModel: ExerciseViewModel = viewModel()

    NavHost(navController = navController, startDestination = "lines") {
        composable("lines") {
            ArchivePage(onManageExercises = { navController.navigate("exercise_management") })
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
            val editId = backStackEntry.arguments?.getLong("editId") ?: -1L
            val exercisesState = exerciseViewModel.allExercises.observeAsState()
            val editingExercise = exercisesState.value?.find { it.id == editId }

            if (editId != -1L && editingExercise == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                MovementEntryPage(
                    onSave = { name: String, category: String, muscleGroup: String, rating: Int, uri: Uri?, note: String ->
                        val categoryEnum = ExerciseCategory.values().find { it.display == category } ?: ExerciseCategory.Calisthenics
                        val muscleGroupEnum = MuscleGroup.values().find { it.display == muscleGroup } ?: MuscleGroup.Core

                        val updatedExercise = Exercise(
                            id = editingExercise?.id ?: 0,
                            name = name,
                            description = note,
                            category = categoryEnum,
                            likeability = rating,
                            muscleGroup = muscleGroupEnum,
                            muscle = muscleGroupEnum.display,
                            imageUri = uri?.toString(),
                            isFavorite = editingExercise?.isFavorite ?: false
                        )

                        if (editingExercise != null) {
                            exerciseViewModel.update(updatedExercise)
                        } else {
                            exerciseViewModel.insert(updatedExercise)
                        }

                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable("movement_editor") {
            MovementEntryPage(
                onSave = { name: String, category: String, muscleGroup: String, rating: Int, uri: Uri?, note: String ->
                    val categoryEnum = ExerciseCategory.values().find { it.display == category } ?: ExerciseCategory.Calisthenics
                    val muscleGroupEnum = MuscleGroup.values().find { it.display == muscleGroup } ?: MuscleGroup.Core

                    val newExercise = Exercise(
                        id = 0,
                        name = name,
                        description = note,
                        category = categoryEnum,
                        likeability = rating,
                        muscleGroup = muscleGroupEnum,
                        muscle = muscleGroupEnum.display,
                        imageUri = uri?.toString(),
                        isFavorite = false
                    )

                    exerciseViewModel.insert(newExercise)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}
