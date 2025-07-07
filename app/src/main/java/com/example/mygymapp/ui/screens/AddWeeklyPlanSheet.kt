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
import com.example.mygymapp.model.ExerciseEntry
import org.burnoutcrew.reorderable.*

private fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to) return
    val item = removeAt(from)
    add(if (to > from) to - 1 else to, item)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWeeklyPlanSheet(
    exercises: List<Exercise>,
    onSave: (Plan, List<PlanExerciseCrossRef>, List<String>) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(3) }

    val dayNames = remember { mutableStateListOf("Tag 1", "Tag 2", "Tag 3", "Tag 4", "Tag 5") }
    val dayEntries = remember { List(5) { mutableStateListOf<ExerciseEntry>() } }

    val dropdownState = remember { mutableStateListOf<Exercise?>(null, null, null, null, null) }
    val expandedList = remember { mutableStateListOf(false, false, false, false, false) }

    ModalBottomSheet(onDismissRequest = onCancel, sheetState = rememberModalBottomSheetState()) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            Spacer(Modifier.height(8.dp))
            Text("Schwierigkeit")
            DifficultyRating(rating = difficulty, onRatingChanged = { difficulty = it })
            Spacer(Modifier.height(16.dp))

            dayNames.forEachIndexed { index, dayName ->
                TextField(
                    value = dayName,
                    onValueChange = { dayNames[index] = it },
                    label = { Text("Tag ${index + 1}") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(4.dp))
                val chosen = dropdownState[index]
                val expanded = expandedList[index]
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expandedList[index] = !expanded }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = chosen?.name ?: "Übung auswählen",
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expandedList[index] = false }) {
                        exercises.forEach { ex ->
                            DropdownMenuItem(text = { Text(ex.name) }, onClick = {
                                dropdownState[index] = ex
                                expandedList[index] = false
                            })
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Button(onClick = {
                    dropdownState[index]?.let { ex ->
                        dayEntries[index].add(ExerciseEntry(ex))
                        dropdownState[index] = null
                    }
                }, enabled = dropdownState[index] != null) {
                    Text("Übung hinzufügen")
                }
                Spacer(Modifier.height(4.dp))

                val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
                    dayEntries[index].move(from.index, to.index)
                })
                LazyColumn(
                    state = reorderState.listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                        .reorderable(reorderState)
                        .detectReorderAfterLongPress(reorderState)
                ) {
                    itemsIndexed(dayEntries[index], key = { _, item -> item.exercise.id }) { idx, item ->
                        ReorderableItem(reorderState, key = item.exercise.id) { _ ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text(item.exercise.name, modifier = Modifier.weight(1f))
                                OutlinedTextField(
                                    value = item.sets.toString(),
                                    onValueChange = { item.sets = it.toIntOrNull() ?: item.sets },
                                    label = { Text("Sets") },
                                    modifier = Modifier.width(72.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                OutlinedTextField(
                                    value = item.reps.toString(),
                                    onValueChange = { item.reps = it.toIntOrNull() ?: item.reps },
                                    label = { Text("Reps") },
                                    modifier = Modifier.width(72.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Abbrechen") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val plan = Plan(name = name, description = desc, difficulty = difficulty, iconUri = null, type = PlanType.WEEKLY)
                    val refs = mutableListOf<PlanExerciseCrossRef>()
                    dayEntries.forEachIndexed { day, list ->
                        list.forEachIndexed { idx, entry ->
                            refs.add(
                                PlanExerciseCrossRef(
                                    planId = 0L,
                                    exerciseId = entry.exercise.id,
                                    sets = entry.sets,
                                    reps = entry.reps,
                                    orderIndex = idx,
                                    dayIndex = day
                                )
                            )
                        }
                    }
                    onSave(plan, refs, dayNames.toList())
                }, enabled = name.isNotBlank()) {
                    Text("Speichern")
                }
            }
        }
    }
}