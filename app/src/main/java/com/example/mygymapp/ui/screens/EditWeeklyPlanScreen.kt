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
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.Plan
import com.example.mygymapp.model.ExerciseEntry
import com.example.mygymapp.ui.util.move
import com.example.mygymapp.ui.widgets.DifficultyRating
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.viewmodel.PlansViewModel
import com.example.mygymapp.viewmodel.PlansViewModelFactory
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWeeklyPlanScreen(
    planId: Long,
    navController: NavController
) {
    val context = LocalContext.current
    val repo = remember(context) { PlanRepository(AppDatabase.getDatabase(context).planDao()) }
    val viewModel: PlansViewModel = viewModel(key = "editWeekly$planId", factory = PlansViewModelFactory(repo))
    val exerciseViewModel: ExerciseViewModel = viewModel()

    val exercises by exerciseViewModel.allExercises.observeAsState(emptyList())
    val planLive = remember(planId) { viewModel.load(planId) }
    val planWithExercises by planLive.observeAsState()

    var name by rememberSaveable { mutableStateOf("") }
    var desc by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableIntStateOf(3) }

    val dayNames = remember { mutableStateListOf<String>() }
    val dayEntries = remember { mutableStateListOf<MutableList<ExerciseEntry>>() }

    var showChooserForDay by remember { mutableIntStateOf(-1) }
    var initialized by remember { mutableStateOf(false) }

    LaunchedEffect(planWithExercises, exercises) {
        if (!initialized && planWithExercises != null && exercises.isNotEmpty()) {
            val pw = planWithExercises!!
            name = pw.plan.name
            desc = pw.plan.description
            difficulty = pw.plan.difficulty
            dayNames.clear()
            dayEntries.clear()
            val daysSorted = if (pw.days.isNotEmpty()) pw.days.sortedBy { it.dayIndex } else List(5) { null }
            daysSorted.forEachIndexed { idx, d ->
                dayNames.add(d?.name ?: stringResource(id = R.string.day_label, idx + 1))
                val list = pw.exercises.filter { it.dayIndex == idx }.sortedBy { it.orderIndex }.mapNotNull { ref ->
                    val ex = exercises.firstOrNull { it.id == ref.exerciseId }
                    ex?.let { ExerciseEntry(it, ref.sets, ref.reps) }
                }.toMutableList()
                dayEntries.add(list)
            }
            initialized = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.edit_weekly_plan)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            val saveEnabled = planWithExercises != null && name.isNotBlank()
            FloatingActionButton(
                onClick = {
                    if (saveEnabled) {
                        planWithExercises?.let { pw ->
                            val plan = pw.plan.copy(name = name, description = desc, difficulty = difficulty)
                            val refs = mutableListOf<PlanExerciseCrossRef>()
                            dayEntries.forEachIndexed { day, list ->
                                list.forEachIndexed { idx, entry ->
                                    refs.add(
                                    PlanExerciseCrossRef(
                                        planId = plan.planId,
                                        exerciseId = entry.exercise.id,
                                        sets = entry.sets,
                                        reps = entry.reps,
                                        orderIndex = idx,
                                        dayIndex = day
                                    )
                                )
                            }
                        }
                        viewModel.save(plan, refs, dayNames.toList())
                        navController.popBackStack()
                    }
                }
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(id = R.string.save),
                    tint = if (saveEnabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = ContentAlpha.disabled)
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
                Spacer(Modifier.height(16.dp))

                dayNames.forEachIndexed { index, dayName ->
                    OutlinedTextField(
                        value = dayName,
                        onValueChange = { dayNames[index] = it },
                        label = { Text(stringResource(id = R.string.day_label, index + 1)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(onClick = { showChooserForDay = index }) { Text(stringResource(id = R.string.add_exercise_button)) }
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
                        itemsIndexed(dayEntries[index], key = { _, item -> item.id }) { _, item ->
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
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    if (showChooserForDay >= 0) {
        ModalBottomSheet(onDismissRequest = { showChooserForDay = -1 }, sheetState = rememberModalBottomSheetState()) {
            LazyColumn {
                items(exercises) { ex ->
                    ListItem(
                        headlineContent = { Text(ex.name) },
                        modifier = Modifier.clickable {
                            if (showChooserForDay in dayEntries.indices) {
                                dayEntries[showChooserForDay].add(ExerciseEntry(ex))
                            }
                            showChooserForDay = -1
                        }
                    )
                }
            }
        }
    }
}
