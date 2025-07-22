package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Plan
import com.example.mygymapp.ui.widgets.DifficultyRating
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanType as DataPlanType
import com.example.mygymapp.model.PlanType as UiPlanType
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

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
    var setsText by remember { mutableStateOf(initialExercises.firstOrNull()?.sets?.toString() ?: "") }
    var repsText by remember { mutableStateOf(initialExercises.firstOrNull()?.reps?.toString() ?: "") }
    var orderIndex by remember { mutableStateOf(initialExercises.firstOrNull()?.orderIndex ?: 0) }
    val dayIndex = 0

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = planName,
                onValueChange = { planName = it },
                label = { Text(stringResource(id = R.string.plan_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.description_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("Schwierigkeit")
            DifficultyRating(rating = difficulty, onRatingChanged = { difficulty = it })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = iconUri,
                onValueChange = { iconUri = it },
                label = { Text(stringResource(id = R.string.icon_uri)) },
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
                    label = { Text(stringResource(id = R.string.type)) },
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
                    value = setsText,
                    onValueChange = { setsText = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(id = R.string.sets)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { repsText = it.filter { ch -> ch.isDigit() } },
                    label = { Text(stringResource(id = R.string.reps)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = orderIndex.toString(),
                    onValueChange = { orderIndex = it.toIntOrNull() ?: orderIndex },
                    label = { Text(stringResource(id = R.string.order)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Buttons
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text(stringResource(id = R.string.cancel)) }
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
                            sets = setsText.toIntOrNull() ?: 0,
                            reps = repsText.toIntOrNull() ?: 0,
                            orderIndex = orderIndex,
                            dayIndex = dayIndex
                        )
                    )
                    onSave(plan, crossRefs)
                }) {
                    Text(stringResource(id = R.string.save))
                }
            }
        }
    }
}