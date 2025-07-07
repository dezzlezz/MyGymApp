package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Plan
import com.example.mygymapp.ui.widgets.DifficultyRating
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanType as DataPlanType
import com.example.mygymapp.model.PlanType as UiPlanType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPlanSheet(
    initialPlan: Plan = Plan(name = "", description = "", iconUri = null, type = DataPlanType.DAILY),
    initialExercises: List<PlanExerciseCrossRef> = emptyList(),
    onSave: (Plan, List<PlanExerciseCrossRef>) -> Unit,
    onCancel: () -> Unit
) {
    // Textfelder
    var planName by remember { mutableStateOf(initialPlan.name) }
    var description by remember { mutableStateOf(initialPlan.description) }
    var difficulty by remember { mutableStateOf(initialPlan.difficulty) }
    var iconUri by remember { mutableStateOf(initialPlan.iconUri ?: "") }

    // UI-PlanType benutzen
    var uiType by remember { mutableStateOf(UiPlanType.valueOf(initialPlan.type.name)) }

    // CrossRef-States
    var sets by remember { mutableStateOf(initialExercises.firstOrNull()?.sets ?: 3) }
    var reps by remember { mutableStateOf(initialExercises.firstOrNull()?.reps ?: 10) }
    var orderIndex by remember { mutableStateOf(initialExercises.firstOrNull()?.orderIndex ?: 0) }
    val dayIndex = 0

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = planName,
                onValueChange = { planName = it },
                label = { Text("Plan-Name") },
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
            Text("Schwierigkeit")
            DifficultyRating(rating = difficulty, onRatingChanged = { difficulty = it })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = iconUri,
                onValueChange = { iconUri = it },
                label = { Text("Icon URI") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Dropdown für UiPlanType
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = uiType.name,
                    onValueChange = {},
                    label = { Text("Typ") },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    UiPlanType.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                uiType = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // CrossRef-Einstellungen
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sets.toString(),
                    onValueChange = { sets = it.toIntOrNull() ?: sets },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = reps.toString(),
                    onValueChange = { reps = it.toIntOrNull() ?: reps },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = orderIndex.toString(),
                    onValueChange = { orderIndex = it.toIntOrNull() ?: orderIndex },
                    label = { Text("Order") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Abbrechen") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    // Data-PlanType mappen
                    val dataType = DataPlanType.valueOf(uiType.name)
                    // Neues Plan-Objekt
                    val plan = initialPlan.copy(
                        name = planName,
                        description = description,
                        difficulty = difficulty,
                        iconUri = iconUri.ifBlank { null },
                        type = dataType
                    )
                    // CrossRef-Liste erzeugen
                    val crossRefs = listOf(
                        PlanExerciseCrossRef(
                            planId = plan.planId,
                            exerciseId = initialExercises.firstOrNull()?.exerciseId ?: 0L,
                            sets = sets,
                            reps = reps,
                            orderIndex = orderIndex,
                            dayIndex = dayIndex
                        )
                    )
                    onSave(plan, crossRefs)
                }) {
                    Text("Speichern")
                }
            }
        }
    }
}