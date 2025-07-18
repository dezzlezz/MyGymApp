package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.dimensionResource
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanType
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.ui.widgets.DifficultyRating
import com.example.mygymapp.model.ExerciseEntry
import org.burnoutcrew.reorderable.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.example.mygymapp.ui.util.move
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.mygymapp.model.Equipment
import com.example.mygymapp.components.PlanInputField
import com.example.mygymapp.components.DurationSlider
import com.example.mygymapp.components.EquipmentChipsRow
import com.example.mygymapp.data.GroupType
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddWeeklyPlanSheet(
    exercises: List<Exercise>,
    onSave: (Plan, List<PlanExerciseCrossRef>, List<String>) -> Unit,
    onCancel: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableIntStateOf(3) }

    var duration by rememberSaveable { mutableIntStateOf(30) }
    val equipment = remember { mutableStateListOf<String>() }

    val context = LocalContext.current
    val dayNames = remember {
        mutableStateListOf(*(1..5).map { context.getString(R.string.day_label, it) }.toTypedArray())
    }
    val dayEntries = remember { List(5) { mutableStateListOf<ExerciseEntry>() } }
    val selectedForGroup = remember { List(5) { mutableStateListOf<Long>() } }

    val dropdownState = remember { mutableStateListOf<Exercise?>(null, null, null, null, null) }
    val expandedList = remember { mutableStateListOf(false, false, false, false, false) }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = rememberModalBottomSheetState(),
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(dimensionResource(id = R.dimen.spacing_medium))
        ) {
            PlanInputField(
                value = name,
                onValueChange = { name = it },
                labelRes = R.string.name_label,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
            PlanInputField(
                value = desc,
                onValueChange = { desc = it },
                labelRes = R.string.description_label,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_small)))
            Text(stringResource(id = R.string.difficulty))
            DifficultyRating(rating = difficulty, onRatingChanged = { difficulty = it })
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            DurationSlider(duration = duration, onDurationChange = { duration = it })
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

            Text(stringResource(id = R.string.equipment_label))
            EquipmentChipsRow(selected = equipment, onToggle = { eq ->
                if (eq in equipment) equipment.remove(eq) else equipment.add(eq)
            })
            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            dayNames.forEachIndexed { index, dayName ->
                PlanInputField(
                    value = dayName,
                    onValueChange = { dayNames[index] = it },
                    labelRes = R.string.day_label,
                    modifier = Modifier.fillMaxWidth(),
                    formatArgs = *arrayOf(index + 1)
                )
                Spacer(Modifier.height(4.dp))
                val chosen = dropdownState[index]
                val expanded = expandedList[index]
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expandedList[index] = !expanded }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = chosen?.name ?: stringResource(id = R.string.exercise_placeholder),
                        onValueChange = {},
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expandedList[index] = false }) {
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
                    Text(stringResource(id = R.string.add_exercise_button))
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
                    itemsIndexed(
                        dayEntries[index],
                        key = { _, item -> item.id }) { idx, item ->
                        ReorderableItem(reorderState, key = item.id) { _ ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(dimensionResource(id = R.dimen.spacing_small))
                            ) {
                                Checkbox(
                                    checked = item.id in selectedForGroup[index],
                                    onCheckedChange = { checked ->
                                        if (checked) selectedForGroup[index].add(item.id) else selectedForGroup[index].remove(item.id)
                                    }
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(item.exercise.name, modifier = Modifier.weight(1f))
                                var setsText by remember(item.id) { mutableStateOf(item.sets.toString()) }
                                OutlinedTextField(
                                    value = setsText,
                                    onValueChange = {
                                        setsText = it.filter { ch -> ch.isDigit() }
                                        item.sets = setsText.toIntOrNull() ?: 0
                                    },
                                    label = { Text(stringResource(id = R.string.sets)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.width(72.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                var repsText by remember(item.id) { mutableStateOf(item.reps.toString()) }
                                OutlinedTextField(
                                    value = repsText,
                                    onValueChange = {
                                        repsText = it.filter { ch -> ch.isDigit() }
                                        item.reps = repsText.toIntOrNull() ?: 0
                                    },
                                    label = { Text(stringResource(id = R.string.reps)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier.width(72.dp)
                                )
                            }
                        }
                    }
                }
                if (selectedForGroup[index].size >= 2) {
                    Spacer(Modifier.height(4.dp))
                    Button(onClick = {
                        val gid = System.currentTimeMillis()
                        dayEntries[index].filter { it.id in selectedForGroup[index] }.forEach { entry ->
                            entry.groupId = gid
                            entry.groupType = GroupType.SUPERSET
                        }
                        selectedForGroup[index].clear()
                    }) { Text(stringResource(R.string.create_superset)) }
                }
                Spacer(Modifier.height(12.dp))
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text(stringResource(id = R.string.cancel)) }
                Spacer(Modifier.width(dimensionResource(id = R.dimen.spacing_small)))
                Button(onClick = {
                    val plan = Plan(
                        name = name,
                        description = desc,
                        difficulty = difficulty,
                        iconUri = null,
                        type = PlanType.WEEKLY,
                        durationMinutes = duration,
                        requiredEquipment = equipment.toList()
                    )
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
                                    dayIndex = day,
                                    groupId = entry.groupId,
                                    groupType = entry.groupType
                                )
                            )
                        }
                    }
                    onSave(plan, refs, dayNames.toList())
                }, enabled = name.isNotBlank()) {
                    Text(stringResource(id = R.string.save))
                }
            }
        }
    }
}