package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.PlanExerciseCrossRef

@Composable
fun StepWorkoutScreen(
    exercises: List<PlanExerciseCrossRef>,
    getExerciseInfo: (Long) -> Exercise,
    onComplete: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }

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

    val ref = exercises.getOrNull(currentIndex)
    if (ref != null) {
        val ex = getExerciseInfo(ref.exerciseId)

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
            Text("${ref.sets} × ${ref.reps}", style = MaterialTheme.typography.bodyLarge)
            Text("${ex.muscle} • ${ex.category.display}", style = MaterialTheme.typography.bodySmall)

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

