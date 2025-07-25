package com.example.mygymapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.ExerciseEntry
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup


@Composable
fun ArchivePage() {
    val demoLines = remember {
        listOf(
            Line(
                id = 1,
                title = "Silent Force",
                category = "Push",
                muscleGroup = "Core",
                mood = "balanced",
                exercises = listOf(
                    ExerciseEntry(
                        exercise = Exercise(
                            id = 0,
                            name = "Push-up",
                            description = "",
                            category = ExerciseCategory.Calisthenics,
                            likeability = 5,
                            muscleGroup = MuscleGroup.Chest,
                            muscle = "Pectorals"
                        )
                    )
                ),
                supersets = emptyList(),
                note = "Felt steady and grounded throughout."
            ),
            Line(
                id = 2,
                title = "Night Owl Session",
                category = "Pull",
                muscleGroup = "Back",
                mood = "alert",
                exercises = listOf(
                    ExerciseEntry(
                        exercise = Exercise(
                            id = 1,
                            name = "Pull-up",
                            description = "",
                            category = ExerciseCategory.Calisthenics,
                            likeability = 5,
                            muscleGroup = MuscleGroup.Back,
                            muscle = "Latissimus"
                        )
                    )
                ),
                supersets = listOf(1L to 2L),
                note = "Late session with high focus."
            )
        )
    }

    LineArchivePage(
        lines = demoLines,
        onEdit = {},
        onAdd = {},
        onArchive = {}
    )
}
