package com.example.mygymapp.ui.components

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.mygymapp.data.PlanWithExercises

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailSheet(
    planWithExercises: PlanWithExercises,
    onDismiss: () -> Unit
) {
    Column(
        Modifier
            .fillMaxHeight(0.9f)
            .padding(16.dp)
    ) {
        Text(
            text = planWithExercises.plan.name,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = planWithExercises.plan.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { /* expand/collapse */ }) {
            Text("Show Exercises")
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(planWithExercises.exercises) { ex ->
                Text(text = ex.name)
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Close")
        }
    }
}