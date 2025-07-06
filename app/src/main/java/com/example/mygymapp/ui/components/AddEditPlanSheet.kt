package com.example.mygymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanType
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.Exercise

/**
 * Bottom Sheet zum Hinzufügen/Bearbeiten eines Plans inklusive Übungen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlanSheet(
    initialPlan: Plan?,
    allExercises: List<Exercise>,
    onSave: (plan: Plan, refs: List<PlanExerciseCrossRef>) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initialPlan?.name.orEmpty()) }
    var description by remember { mutableStateOf(initialPlan?.description.orEmpty()) }
    var type by remember { mutableStateOf(initialPlan?.type ?: PlanType.DAILY) }

    // Liste der ausgewählten Übungen mit Standardwerten
    val selectedRefs = remember { mutableStateListOf<PlanExerciseCrossRef>() }

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxHeight(0.9f)
    ) {
        Text(
            text = if (initialPlan == null) "Neuen Plan erstellen" else "Plan bearbeiten",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Beschreibung") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Text("Typ:", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth()) {
            PlanType.values().forEach { pt ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = type == pt,
                        onClick = { type = pt }
                    )
                    Text(pt.name)
                }
            }
        }
        Spacer(Modifier.height(8.dp))

        Text("Übungen auswählen:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(allExercises) { exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedRefs.none { it.exerciseId == exercise.id }) {
                                selectedRefs += PlanExerciseCrossRef(
                                    planId = initialPlan?.planId ?: 0L,
                                    exerciseId = exercise.id,
                                    sets = 3,
                                    reps = 10,
                                    orderIndex = selectedRefs.size
                                )
                            }
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(exercise.name, modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = selectedRefs.any { it.exerciseId == exercise.id },
                        onCheckedChange = { checked ->
                            if (checked) {
                                selectedRefs += PlanExerciseCrossRef(
                                    planId = initialPlan?.planId ?: 0L,
                                    exerciseId = exercise.id,
                                    sets = 3,
                                    reps = 10,
                                    orderIndex = selectedRefs.size
                                )
                            } else {
                                selectedRefs.removeAll { it.exerciseId == exercise.id }
                            }
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) { Text("Abbrechen") }
            Spacer(Modifier.width(16.dp))
            Button(onClick = {
                val plan = Plan(
                    planId = initialPlan?.planId ?: 0L,
                    name = name,
                    description = description,
                    isFavorite = initialPlan?.isFavorite ?: false,
                    type = type,
                    iconUri = initialPlan?.iconUri
                )
                onSave(plan, selectedRefs.toList())
            }) {
                Text("Speichern")
            }
        }
    }
}