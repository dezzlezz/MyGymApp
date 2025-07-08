package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanType
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.ui.widgets.DifficultyRating
import org.burnoutcrew.reorderable.*
import com.example.mygymapp.model.ExerciseEntry
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

private fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to) return
    val item = removeAt(from)
    add(if (to > from) to - 1 else to, item)
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDailyPlanSheet(
    exercises: List<Exercise>,
    onSave: (Plan, List<PlanExerciseCrossRef>) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(3) }

    val selected = remember { mutableStateListOf<ExerciseEntry>() }

    var expanded by remember { mutableStateOf(false) }
    var chosen by remember { mutableStateOf<Exercise?>(null) }

    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        selected.move(from.index, to.index)
    })

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = rememberModalBottomSheetState(),
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Beschreibung") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("Schwierigkeit")
            DifficultyRating(rating = difficulty, onRatingChanged = { difficulty = it })
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    readOnly = true,
                    value = chosen?.name ?: "Übung auswählen",
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    exercises.forEach { ex ->
                        DropdownMenuItem(text = { Text(ex.name) }, onClick = {
                            chosen = ex
                            expanded = false
                        })
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Button(onClick = {
                chosen?.let { ex ->
                    selected.add(ExerciseEntry(ex))
                    chosen = null
                }
            }, enabled = chosen != null) {
                Text("Übung hinzufügen")
            }

            Spacer(Modifier.height(16.dp))
            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .reorderable(reorderState)
                    .detectReorderAfterLongPress(reorderState)
            ) {
                itemsIndexed(selected, key = { _, item -> item.exercise.id }) { index, item ->
                    ReorderableItem(reorderState, key = item.exercise.id) { _ ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(item.exercise.name, modifier = Modifier.weight(1f))
                            var setsText by remember(item.exercise.id) { mutableStateOf(item.sets.toString()) }
                            OutlinedTextField(
                                value = setsText,
                                onValueChange = {
                                    setsText = it.filter { ch -> ch.isDigit() }
                                    item.sets = setsText.toIntOrNull() ?: 0
                                },
                                label = { Text("Sets") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.width(72.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            var repsText by remember(item.exercise.id) { mutableStateOf(item.reps.toString()) }
                            OutlinedTextField(
                                value = repsText,
                                onValueChange = {
                                    repsText = it.filter { ch -> ch.isDigit() }
                                    item.reps = repsText.toIntOrNull() ?: 0
                                },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.width(72.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Abbrechen") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val plan = Plan(name = name, description = desc, difficulty = difficulty, iconUri = null, type = PlanType.DAILY)
                    val refs = selected.mapIndexed { idx, e ->
                        PlanExerciseCrossRef(
                            planId = 0L,
                            exerciseId = e.exercise.id,
                            sets = e.sets,
                            reps = e.reps,
                            orderIndex = idx
                        )
                    }
                    onSave(plan, refs)
                }, enabled = name.isNotBlank() && selected.isNotEmpty()) {
                    Text("Speichern")
                }
            }
        }
    }
}