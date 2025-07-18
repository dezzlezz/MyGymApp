package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.mygymapp.components.WorkoutDayCard
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.model.WeekProgress
import com.example.mygymapp.viewmodel.WorkoutViewModel
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

private val dayStrings = listOf(
    R.string.monday,
    R.string.tuesday,
    R.string.wednesday,
    R.string.thursday,
    R.string.friday,
    R.string.saturday,
    R.string.sunday
)

@Composable
private fun dayName(index: Int): String {
    val safeIdx = index.coerceIn(0, dayStrings.lastIndex)
    return stringResource(id = dayStrings[safeIdx])
}

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel = viewModel()) {
    val progress by viewModel.progress.observeAsState()
    val weeklyPlans by viewModel.weeklyPlans.observeAsState(emptyList())
    val dailyPlans by viewModel.dailyPlans.observeAsState(emptyList())
    val plan by viewModel.todayPlan.observeAsState()
    val _exercises by viewModel.exercises.observeAsState(emptyList())
    val nav = rememberNavController()
    var newWeeklyPlan by remember { mutableStateOf<Plan?>(null) }
    var restDay by remember { mutableIntStateOf(-1) }
    var modularPlan by remember { mutableStateOf<Plan?>(null) }
    var modularRest by remember { mutableStateOf(false) }
    val start = if (progress == null) "StartWorkout" else "WorkoutDay"

    NavHost(navController = nav, startDestination = start) {
        composable("StartWorkout") {
            StartWorkoutScreen { nav.navigate("SelectPlan") }
        }
        composable("SelectPlan") {
            SelectPlanScreen(plans = weeklyPlans) { p ->
                newWeeklyPlan = p
                nav.navigate("PreviewPlan")
            }
        }
        composable("PreviewPlan") {
            val previewData: LiveData<PlanWithExercises>? = newWeeklyPlan?.let { viewModel.loadPlan(it.planId) }
            // Observe the LiveData. If previewData is null, pwAsState will also be null.
            val pwAsState: State<PlanWithExercises?>? = previewData?.observeAsState()
            // Get the actual value from the State, or null if pwAsState is null
            val pw: PlanWithExercises? = pwAsState?.value

            PreviewPlanScreen(plan = pw, viewModel = viewModel) {
                nav.navigate("SelectRestDay")
            }
        }
        composable("SelectRestDay") {
            SelectRestDayScreen(restDay = restDay) { day ->
                restDay = day
                nav.navigate("SelectModularDay")
            }
        }
        composable("SelectModularDay") {
            SelectModularDayScreen(plans = dailyPlans, selected = modularPlan, rest = modularRest) { r, p ->
                modularRest = r
                modularPlan = p
                val prog = WeekProgress(
                    weeklyPlanId = newWeeklyPlan!!.planId,
                    restDay = restDay,
                    modularPlanId = modularPlan?.planId,
                    modularRest = modularRest,
                    day = 0
                )
                viewModel.startWeek(prog)
                nav.navigate("WorkoutDay") {
                    popUpTo("StartWorkout") { inclusive = true }
                }
            }
        }
        composable("WorkoutDay") {
            val state = progress
            if (state == null) {
                nav.navigate("StartWorkout") { popUpTo(0) }
            } else {
                val isRest = state.day == state.restDay || (state.day == 5 && state.modularRest)
                if (isRest) {
                    RestDayScreen(dayIndex = state.day) {
                        viewModel.finishDay()
                        nav.navigate("FinishDay")
                    }
                } else {
                    StepWorkoutScreen(
                        exercises = plan?.exercises
                            ?.filter { it.dayIndex == calculatePlanIndex(state) }
                            ?.sortedBy { it.orderIndex } ?: emptyList(),
                        getExerciseInfo = { id -> viewModel.getExerciseById(id) }
                    ) {
                        viewModel.finishDay()
                        nav.navigate("FinishDay")
                    }
                }
            }
        }
        composable("FinishDay") {
            val state = progress
            FinishDayScreen(state) {
                if (state == null) {
                    nav.navigate("StartWorkout") { popUpTo(0) }
                } else {
                    nav.navigate("WorkoutDay") { popUpTo("WorkoutDay") { inclusive = true } }
                }
            }
        }
    }
}

@Composable
private fun StartWorkoutScreen(onStart: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = onStart) { Text(stringResource(id = R.string.start_week)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectPlanScreen(plans: List<Plan>, onSelect: (Plan) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.select_plan)) }) }) { padding ->
        if (plans.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_weekly_plans))
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(plans, key = { it.planId }) { p ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(p.name, style = MaterialTheme.typography.titleMedium)
                                Text(p.description, style = MaterialTheme.typography.bodySmall)
                            }
                            Button(onClick = { onSelect(p) }) { Text(stringResource(R.string.select)) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewPlanScreen(plan: PlanWithExercises?, viewModel: WorkoutViewModel, onNext: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.preview_plan)) }) }) { padding ->
        if (plan == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text(stringResource(R.string.loading)) }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text(plan.plan.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                val daysSorted = if (plan.days.isNotEmpty()) plan.days.sortedBy { it.dayIndex } else List(7) { null }
                daysSorted.forEachIndexed { idx, day ->
                    val title = day?.name ?: dayName(idx)
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    plan.exercises.filter { it.dayIndex == idx }.forEach { ref ->
                        val name = viewModel.getExerciseName(ref.exerciseId)
                        Text("• $name ${ref.sets}x${ref.reps}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.next)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectRestDayScreen(restDay: Int, onNext: (Int) -> Unit) {
    var selected by remember { mutableIntStateOf(restDay) }
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.select_rest_day)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            dayStrings.forEachIndexed { index, _ ->
                val d = dayName(index)
                val enabled = index != 5
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selected == index,
                        onClick = { if (enabled) selected = index },
                        enabled = enabled
                    )
                    Text(if (index == 5) "$d (${stringResource(R.string.modular_day)})" else d)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = selected == -1, onClick = { selected = -1 })
                Text(stringResource(R.string.none))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onNext(selected) }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.next)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectModularDayScreen(
    plans: List<Plan>,
    selected: Plan?,
    rest: Boolean,
    onNext: (Boolean, Plan?) -> Unit
) {
    var modRest by remember { mutableStateOf(rest) }
    var planSel by remember { mutableStateOf(selected) }
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.modular_day)) }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = modRest, onCheckedChange = { modRest = it })
                Text(stringResource(R.string.rest_day))
            }
            if (!modRest) {
                LazyColumn(Modifier.weight(1f, false)) {
                    items(plans, key = { it.planId }) { p ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            RadioButton(selected = planSel?.planId == p.planId, onClick = { planSel = p })
                            Text(p.name)
                        }
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
            Button(
                onClick = { onNext(modRest, planSel) },
                modifier = Modifier.fillMaxWidth(),
                enabled = modRest || planSel != null
            ) { Text(stringResource(R.string.start_week_button)) }
        }
    }
}

@Composable
private fun RestDayScreen(dayIndex: Int, onFinish: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("${dayName(dayIndex)} - ${stringResource(R.string.rest_day)}", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onFinish) { Text(stringResource(R.string.next_day)) }
    }
}

@Composable
private fun WorkoutDayScreen(
    plan: PlanWithExercises?,
    state: WeekProgress,
    viewModel: WorkoutViewModel,
    onFinish: () -> Unit
) {
    val exercises = remember(plan, state) {
        val idx = calculatePlanIndex(state)
        plan?.exercises?.filter { it.dayIndex == idx } ?: emptyList()
    }
    val doneMap = remember { mutableStateMapOf<Long, Boolean>() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(dayName(state.day), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(exercises) { ref ->
                WorkoutDayCard(
                    ref = ref,
                    exerciseName = viewModel.getExerciseName(ref.exerciseId),
                    muscleGroup = viewModel.getExerciseGroup(ref.exerciseId),
                    done = doneMap[ref.exerciseId] == true,
                    onDone = { doneMap[ref.exerciseId] = it }
                )
            }
        }
        Button(
            onClick = onFinish,
            enabled = exercises.all { doneMap[it.exerciseId] == true },
            modifier = Modifier.fillMaxWidth()
        ) { Text(stringResource(R.string.finish_day)) }
    }
}




@Composable
private fun FinishDayScreen(progress: WeekProgress?, onContinue: () -> Unit) {
    val finishedDay = ((progress?.day ?: 1) - 1).coerceAtLeast(0)
    val text = if (progress == null) {
        stringResource(R.string.week_complete)
    } else {
        stringResource(R.string.day_finished, dayName(finishedDay))
    }
    val button = if (progress == null) stringResource(R.string.back_to_start) else stringResource(R.string.next_day)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onContinue) { Text(button) }
        }
    }
}

private fun calculatePlanIndex(state: WeekProgress): Int {
    var idx = state.day
    if (state.restDay in 0..6 && state.day > state.restDay) idx--
    if (state.day > 5) idx--
    return idx}