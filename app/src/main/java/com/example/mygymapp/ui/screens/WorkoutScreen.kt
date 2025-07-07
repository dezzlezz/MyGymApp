package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.model.WeekProgress
import com.example.mygymapp.ui.viewmodel.WorkoutViewModel


private val days = listOf("Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag","Sonntag")

@Composable
fun WorkoutScreen(viewModel: WorkoutViewModel = viewModel()) {
    val progress by viewModel.progress.observeAsState()
    val weeklyPlans by viewModel.weeklyPlans.observeAsState(emptyList())
    val dailyPlans by viewModel.dailyPlans.observeAsState(emptyList())
    val plan by viewModel.todayPlan.observeAsState()

    if (progress == null) {
        StartWeekScreen(weeklyPlans, dailyPlans) { wp ->
            viewModel.startWeek(wp)
        }
    } else {
        val state = progress!!
        val dayIndex = state.day
        when {
            (dayIndex == state.restDay) -> RestDayScreen(dayIndex) { viewModel.finishDay() }
            (dayIndex == 5 && state.modularRest) -> RestDayScreen(dayIndex) { viewModel.finishDay() }
            else -> WorkoutDayScreen(plan, state, viewModel)
        }
    }
}

@Composable
private fun StartWeekScreen(
    weeklyPlans: List<com.example.mygymapp.data.Plan>,
    dailyPlans: List<com.example.mygymapp.data.Plan>,
    onStart: (WeekProgress) -> Unit
) {
    var selectedPlan by remember { mutableStateOf<com.example.mygymapp.data.Plan?>(null) }
    var restDay by remember { mutableIntStateOf(-1) }
    var modularOption by remember { mutableStateOf<com.example.mygymapp.data.Plan?>(null) }
    var modularRest by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Woche starten", style = MaterialTheme.typography.titleLarge)
        PlanDropdown(label = "Weekly Plan", plans = weeklyPlans, selected = selectedPlan) { selectedPlan = it }
        DayDropdown(restDay) { restDay = it }
        Text("Modular Day (Samstag)")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = modularRest, onCheckedChange = { modularRest = it })
            Text("Ruhetag")
        }
        if (!modularRest) {
            PlanDropdown(label = "Daily Plan", plans = dailyPlans, selected = modularOption) { modularOption = it }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val prog = WeekProgress(
                    weeklyPlanId = selectedPlan!!.planId,
                    restDay = restDay,
                    modularPlanId = modularOption?.planId,
                    modularRest = modularRest,
                    day = 0
                )
                onStart(prog)
            },
            enabled = selectedPlan != null
        ) { Text("Woche starten") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanDropdown(
    label: String,
    plans: List<com.example.mygymapp.data.Plan>,
    selected: com.example.mygymapp.data.Plan?,
    onSelect: (com.example.mygymapp.data.Plan) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selected?.name ?: label,
            onValueChange = {},
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            plans.forEach { plan ->
                DropdownMenuItem(text = { Text(plan.name) }, onClick = {
                    onSelect(plan)
                    expanded = false
                })
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDropdown(selected: Int, onSelect: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = if (selected == -1) "Rest Day wählen" else days[selected],
            onValueChange = {},
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            label = { Text("Rest Day") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Keiner") }, onClick = { onSelect(-1); expanded = false })
            days.forEachIndexed { index, d ->
                DropdownMenuItem(text = { Text(d) }, onClick = {
                    onSelect(index)
                    expanded = false
                })
            }
        }
    }
}
@Composable
private fun RestDayScreen(dayIndex: Int, onFinish: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("${days[dayIndex]} - Ruhetag", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onFinish) { Text("Nächster Tag") }
    }
}

@Composable
private fun WorkoutDayScreen(plan: com.example.mygymapp.data.PlanWithExercises?, state: WeekProgress, viewModel: WorkoutViewModel) {
    val exercises = remember(plan, state) {
        val idx = calculatePlanIndex(state)
        plan?.exercises?.filter { it.dayIndex == idx } ?: emptyList()
    }
    val doneMap = remember { mutableStateMapOf<Long, Boolean>() }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(days[state.day], style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(exercises) { ref ->
                ExerciseRow(ref, doneMap[ref.exerciseId] == true, { doneMap[ref.exerciseId] = it }, viewModel)
            }
        }
        Button(onClick = { viewModel.finishDay() }, enabled = exercises.all { doneMap[it.exerciseId] == true }, modifier = Modifier.fillMaxWidth()) {
            Text("Finish Day")
        }
    }
}

@Composable
private fun ExerciseRow(ref: PlanExerciseCrossRef, done: Boolean, onDone: (Boolean) -> Unit, viewModel: WorkoutViewModel) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = done, onCheckedChange = onDone)
        Column(Modifier.weight(1f)) {
            Text(viewModel.getExerciseName(ref.exerciseId), style = MaterialTheme.typography.titleMedium)
            Text("${ref.sets} x ${ref.reps}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun calculatePlanIndex(state: WeekProgress): Int {
    var idx = state.day
    if (state.restDay in 0..6 && state.day > state.restDay) idx--
    if (state.day > 5) idx--
    return idx
}