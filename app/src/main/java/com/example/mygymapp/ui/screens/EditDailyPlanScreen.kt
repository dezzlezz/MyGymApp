package com.example.mygymapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.R
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.model.ExerciseEntry
import com.example.mygymapp.ui.util.move
import com.example.mygymapp.ui.widgets.DifficultyRating
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.viewmodel.PlansViewModel
import com.example.mygymapp.viewmodel.PlansViewModelFactory
import com.example.mygymapp.model.Equipment
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavController
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditDailyPlanScreen(
    planId: Long,
    navController: NavController
) {
    val context = LocalContext.current
    val repo = remember(context) { PlanRepository(AppDatabase.getDatabase(context).planDao()) }
    val viewModel: PlansViewModel = viewModel(key = "editDaily$planId", factory = PlansViewModelFactory(repo))
    val exerciseViewModel: ExerciseViewModel = viewModel()

    val exercises by exerciseViewModel.allExercises.observeAsState(emptyList())
    val planLive = remember(planId) { viewModel.load(planId) }
    val planWithExercises by planLive.observeAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableIntStateOf(3) }
    var duration by rememberSaveable { mutableIntStateOf(30) }
    val equipment = remember { mutableStateListOf<String>() }
    val selected = remember { mutableStateListOf<ExerciseEntry>() }
    var showChooser by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(planWithExercises, exercises) {
        if (!initialized && planWithExercises != null && exercises.isNotEmpty()) {
            name = planWithExercises!!.plan.name
            desc = planWithExercises!!.plan.description
            difficulty = planWithExercises!!.plan.difficulty
            duration = planWithExercises!!.plan.durationMinutes
            equipment.clear()
            equipment.addAll(planWithExercises!!.plan.requiredEquipment)
            selected.clear()
            selected.addAll(
                planWithExercises!!.exercises.sortedBy { it.orderIndex }.mapNotNull { ref ->
                    val ex = exercises.firstOrNull { it.id == ref.exerciseId }
                    ex?.let { ExerciseEntry(it, ref.sets, ref.reps) }
                }
            )
            initialized = true
        }
    }

    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        selected.move(from.index, to.index)
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.edit_daily_plan)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            val saveEnabled = planWithExercises != null && name.isNotBlank() && selected.isNotEmpty()
            FloatingActionButton(
                onClick = {
                    if (saveEnabled) {
                        planWithExercises?.let { pw ->
                            val plan = pw.plan.copy(
                                name = name,
                                description = desc,
                                difficulty = difficulty,
                                durationMinutes = duration,
                                requiredEquipment = equipment.toList()
                            )
                            val refs = selected.mapIndexed { idx, e ->
                                PlanExerciseCrossRef(
                                    planId = plan.planId,
                                    exerciseId = e.exercise.id,
                                    sets = e.sets,
                                    reps = e.reps,
                                    orderIndex = idx
                                )
                            }
                            viewModel.save(plan, refs)
                            navController.popBackStack()
                        }
                    }
                }
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.save),
                    tint = if (saveEnabled) {
                        LocalContentColor.current
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) // Updated line
                    }
                )
            }

        }
    ) { padding ->
        if (planWithExercises == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.name_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(stringResource(id = R.string.description_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(stringResource(id = R.string.difficulty))
                DifficultyRating(rating = difficulty, onRatingChanged = { difficulty = it })
                Spacer(Modifier.height(8.dp))

                Text(stringResource(id = R.string.duration_label, duration))
                Slider(
                    value = duration.toFloat(),
                    onValueChange = { duration = it.roundToInt() },
                    valueRange = 10f..60f
                )
                Spacer(Modifier.height(8.dp))

                Text(stringResource(id = R.string.equipment_label))
                FlowRow {
                    Equipment.options.forEach { eq ->
                        FilterChip(
                            selected = eq in equipment,
                            onClick = {
                                if (eq in equipment) equipment.remove(eq) else equipment.add(eq)
                            },
                            label = { Text(eq) }
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { showChooser = true }) { Text(stringResource(id = R.string.add_exercise_button)) }
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    state = reorderState.listState,
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .reorderable(reorderState)
                        .detectReorderAfterLongPress(reorderState)
                ) {
                    itemsIndexed(selected, key = { _, item -> item.id }) { _, item ->
                        ReorderableItem(reorderState, key = item.id) { _ ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
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
            }
        }
    }

    if (showChooser) {
        ModalBottomSheet(onDismissRequest = { showChooser = false }, sheetState = rememberModalBottomSheetState()) {
            LazyColumn {
                items(exercises) { ex ->
                    ListItem(

                        headlineContent = { Text(ex.name) },

                        modifier = Modifier.clickable {
                            selected.add(ExerciseEntry(ex))
                            showChooser = false
                        }
                    )
                }
            }
        }
    }
}
