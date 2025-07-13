package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.PlanExerciseCrossRef

@Composable
data class WorkoutSet(
    var reps: Int = 0,
    var done: Boolean = false
)

data class WorkoutExerciseState(
    val ref: PlanExerciseCrossRef,
    val sets: MutableList<WorkoutSet> = mutableListOf(),
    var notes: String = ""
)

@Composable
fun StepWorkoutScreen(
    exercises: List<PlanExerciseCrossRef>,
    getExerciseInfo: (Long) -> Exercise,
    onComplete: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val workoutExercises = remember(exercises) {
        exercises.map { ref ->
            WorkoutExerciseState(
                ref = ref,
                sets = MutableList(ref.sets) { WorkoutSet(reps = ref.reps) }
            )
        }
    }

    if (exercises.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    stringResource(R.string.no_exercises_today),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onComplete) { Text(stringResource(R.string.finish_day)) }
            }
        }
        return
    }

    val state = workoutExercises.getOrNull(currentIndex)
    if (state != null) {
        val ex = getExerciseInfo(state.ref.exerciseId)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Übung ${currentIndex + 1} / ${exercises.size}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            Text(ex.name, style = MaterialTheme.typography.headlineMedium)
            Text("${state.ref.sets} × ${state.ref.reps}", style = MaterialTheme.typography.bodyLarge)
            Text("${ex.muscleGroup.display} • ${ex.category.display}", style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(16.dp))
            Column {
                state.sets.forEachIndexed { index, set ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("${index + 1}.")
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = if (set.reps == 0) "" else set.reps.toString(),
                            onValueChange = { text -> set.reps = text.toIntOrNull() ?: 0 },
                            label = { Text(stringResource(R.string.reps)) },
                            modifier = Modifier.width(80.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Checkbox(
                            checked = set.done,
                            onCheckedChange = { set.done = it }
                        )
                    }
                }

                Button(onClick = { state.sets.add(WorkoutSet()) }) {
                    Text(stringResource(R.string.add_set))
                }

                Spacer(Modifier.height(8.dp))
                val total = state.sets.sumOf { it.reps }
                Text(stringResource(R.string.total_reps, total))

                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { state.notes = it },
                    label = { Text(stringResource(R.string.notes)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentIndex > 0) {
                    Button(onClick = { currentIndex-- }) { Text(stringResource(R.string.back)) }
                } else {
                    Spacer(Modifier.width(1.dp))
                }

                Button(onClick = {
                    if (currentIndex == exercises.lastIndex) {
                        onComplete()
                    } else {
                        currentIndex++
                    }
                }) {
                    val label = if (currentIndex == exercises.lastIndex) {
                        stringResource(R.string.finish_day)
                    } else {
                        stringResource(R.string.next)
                    }
                    Text(label)
                }
            }
        }
    }
}

