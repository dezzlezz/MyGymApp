package com.example.mygymapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.PlanExerciseCrossRef

@Composable
fun WorkoutDayCard(
    ref: PlanExerciseCrossRef,
    exerciseName: String,
    muscleGroup: String,
    done: Boolean,
    onDone: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(checked = done, onCheckedChange = onDone)
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(exerciseName, style = MaterialTheme.typography.titleMedium)
            Text("${ref.sets} x ${ref.reps} • $muscleGroup", style = MaterialTheme.typography.bodySmall)
        }
    }
}