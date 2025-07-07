package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.PlanWithExercises

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailSheet(
    planWithExercises: PlanWithExercises,
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

            // Beschreibung
            Text(
                text = plan.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            // Liste der Übungen
            Text(
                text = "Übungen:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            planWithExercises.exercises.forEach { ref ->
                Text(
                    text = "• Übung ID ${ref.exerciseId}, Sets: ${ref.sets}, Reps: ${ref.reps}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Schließen")
            }
        }
    }
}
