package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.mygymapp.data.GroupType
import org.burnoutcrew.reorderable.*
import com.example.mygymapp.model.ExerciseEntry
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R
import com.example.mygymapp.ui.util.move
import androidx.compose.runtime.saveable.rememberSaveable
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.mygymapp.model.Equipment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.viewmodel.AddDailyPlanViewModel
import com.example.mygymapp.components.DurationSlider
import com.example.mygymapp.components.EquipmentChipsRow
import com.example.mygymapp.components.PlanInputField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddDailyPlanSheet(
    exercises: List<Exercise>,
    onSave: (Plan, List<PlanExerciseCrossRef>) -> Unit,
    onCancel: () -> Unit,
    viewModel: AddDailyPlanViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()

    val selected = remember { mutableStateListOf<ExerciseEntry>() }
    val selectedForGroup = remember { mutableStateListOf<Long>() }

    var expanded by rememberSaveable { mutableStateOf(false) }
    var chosen by rememberSaveable { mutableStateOf<Exercise?>(null) }

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
                .padding(dimensionResource(id = R.dimen.spacing_medium))
        ) {
            PlanInputField(
                value = form.name,
                onValueChange = viewModel::updateName,
                labelRes = R.string.name_label,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            PlanInputField(
                value = form.description,
                onValueChange = viewModel::updateDesc,
                labelRes = R.string.description_label,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(stringResource(id = R.string.difficulty))
            DifficultyRating(rating = form.difficulty, onRatingChanged = viewModel::updateDifficulty)
            Spacer(Modifier.height(8.dp))
            DurationSlider(duration = form.duration, onDurationChange = viewModel::updateDuration)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(id = R.string.equipment_label))
            EquipmentChipsRow(selected = form.equipment, onToggle = viewModel::toggleEquipment)
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    readOnly = true,
                    value = chosen?.name ?: stringResource(id = R.string.exercise_placeholder),
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
                Text(stringResource(id = R.string.add_exercise_button))
            }

            if (selectedForGroup.size >= 2) {
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    val gid = System.currentTimeMillis()
                    selected.filter { it.id in selectedForGroup }.forEach { entry ->
                        entry.groupId = gid
                        entry.groupType = GroupType.SUPERSET
                    }
                    selectedForGroup.clear()
                }) {
                    Text(stringResource(R.string.create_superset))
                }
            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .reorderable(reorderState)
                    .detectReorderAfterLongPress(reorderState)
            ) {
                itemsIndexed(selected, key = { _, item -> item.id }) { index, item ->
                    ReorderableItem(reorderState, key = item.id) { _ ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Checkbox(
                                checked = item.id in selectedForGroup,
                                onCheckedChange = { checked ->
                                    if (checked) selectedForGroup.add(item.id) else selectedForGroup.remove(item.id)
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

            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text(stringResource(id = R.string.cancel)) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val plan = Plan(
                        name = form.name,
                        description = form.description,
                        difficulty = form.difficulty,
                        iconUri = null,
                        type = PlanType.DAILY,
                        durationMinutes = form.duration,
                        requiredEquipment = form.equipment.toList()
                    )
                    val refs = selected.mapIndexed { idx, e ->
                        PlanExerciseCrossRef(
                            planId = 0L,
                            exerciseId = e.exercise.id,
                            sets = e.sets,
                            reps = e.reps,
                            orderIndex = idx,
                            groupId = e.groupId,
                            groupType = e.groupType
                        )
                    }
                    onSave(plan, refs)
                }, enabled = form.name.isNotBlank() && selected.isNotEmpty()) {
                    Text(stringResource(id = R.string.save))
                }
            }
        }
    }
}