package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.ui.widgets.DifficultyRating
import com.example.mygymapp.data.PlanDay
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailSheet(
    planWithExercises: PlanWithExercises,
    exerciseMap: Map<Long, String>,
    onClose: () -> Unit
) {
    val plan = planWithExercises.plan

    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Überschrift mit Plan-Name
            Text(
                text = plan.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            DifficultyRating(rating = plan.difficulty)
            Spacer(Modifier.height(8.dp))

            // Beschreibung
            Text(
                text = plan.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            val days = if (planWithExercises.days.isNotEmpty()) {
                planWithExercises.days.sortedBy { it.dayIndex }
            } else listOf(PlanDay(plan.planId, 0, "Tag 1"))

            days.forEach { day ->
                Text(day.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                planWithExercises.exercises
                    .filter { it.dayIndex == day.dayIndex }
                    .forEach { ref ->
                        val name = exerciseMap[ref.exerciseId] ?: "Übung ID ${ref.exerciseId}"
                        Text(
                            text = "• $name, Sets: ${ref.sets}, Reps: ${ref.reps}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                        )
                    }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.close))
            }
        }
    }
}
