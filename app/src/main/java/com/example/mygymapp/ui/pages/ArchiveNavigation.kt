package com.example.mygymapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.data.Exercise
import android.net.Uri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize

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
        composable(
            route = "exercise_editor?editId={editId}",
            arguments = listOf(navArgument("editId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val vm: ExerciseViewModel = viewModel()
            val editId = backStackEntry.arguments?.getLong("editId") ?: -1L
            val exercisesState = vm.allExercises.observeAsState()
            val editingExercise = exercisesState.value?.find { it.id == editId }

            if (editId != -1L && editingExercise == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                ExerciseEditorScreen(
                    initialName = editingExercise?.name ?: "",
                    initialCategory = editingExercise?.category?.display ?: "",
                    initialMuscleGroup = editingExercise?.muscleGroup?.display ?: "",
                    initialRating = editingExercise?.likeability ?: 3,
                    initialImageUri = editingExercise?.imageUri?.let { Uri.parse(it) },
                    onSave = { name, category, muscleGroup, rating, uri, note ->
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

                        if (editingExercise != null) vm.update(updatedExercise)
                        else vm.insert(updatedExercise)

                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
