package com.example.mygymapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.ExerciseEntry
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup

@Composable
fun ArchivePage(
    onManageExercises: () -> Unit,
    onOpenLineParagraph: () -> Unit
) {
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
                            customCategory = null,
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
                            customCategory = null,
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

    Column(modifier = Modifier.fillMaxSize()) {
        LineArchivePage(
            lines = demoLines,
            onEdit = {},
            onAdd = {},
            onArchive = {},
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onManageExercises,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "✍️ Edit Exercises",
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onOpenLineParagraph,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "📖 Lines & Paragraphs",
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
