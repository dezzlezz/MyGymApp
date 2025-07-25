package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.ExerciseEntry
import com.example.mygymapp.model.Line
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground

@Composable
fun LineArchivePage() {
    // Temporary demo data
    val lines = remember {
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
                        ),
                        id = 0L
                    )
                ),
                supersets = listOf(),
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
                        ),
                        id = 1L
                    )
                ),
                supersets = listOf(1L to 2L),
                note = "Late session with high focus."
            )
        )
    }

    PaperBackground(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(lines) { line ->
                LineCard(
                    line = line,
                    onEdit = {},
                    onAddToToday = {},
                    onArchive = {},
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}
