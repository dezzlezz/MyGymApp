package com.example.mygymapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.ui.components.SectionHeader
import com.example.mygymapp.viewmodel.HomeViewModel
import com.example.mygymapp.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val todayPlan = viewModel.todayPlan.collectAsState()
    val progress = viewModel.progress.collectAsState()
    val history = viewModel.history.collectAsState()
    val ctx = LocalDate.now(ZoneId.systemDefault())
    val startOfWeek = ctx.with(DayOfWeek.MONDAY)

    val selectedDate: MutableState<LocalDate?> = remember { mutableStateOf(null) }
    val entryInfo = remember { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(selectedDate.value) {
        val date = selectedDate.value ?: return@LaunchedEffect
        val entry = history.value[date]
        entryInfo.value = entry?.let { viewModel.getEntryInfo(it) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader(title = "Was steht heute an?")
                if (todayPlan.value != null && progress.value != null) {
                    val idx = calculatePlanIndex(progress.value!!)
                    val exercises = todayPlan.value!!.exercises.filter { it.dayIndex == idx }
                    val groups = exercises.map { viewModel.getExerciseGroup(it.exerciseId) }
                        .distinct()
                        .joinToString()
                    Text(
                        text = stringResource(
                            id = R.string.home_today,
                            todayPlan.value!!.plan.name,
                            exercises.size,
                            todayPlan.value!!.plan.durationMinutes
                        )
                    )
                    if (groups.isNotBlank()) {
                        Text(stringResource(id = R.string.home_goal, groups))
                    }
                    Button(onClick = { /* TODO start workout */ }) {
                        Text(stringResource(id = R.string.home_start_workout))
                    }
                } else {
                    Text(stringResource(id = R.string.home_no_plan))
                    Button(onClick = { /* TODO choose plan */ }) {
                        Text(stringResource(id = R.string.home_choose_plan))
                    }
                }
            }
            item { Divider() }
            item {
                SectionHeader(title = "Letztes Workout")
                val last = viewModel.lastWorkout
                if (last != null) {
                    val info = entryInfo.value
                    val planName = info?.first ?: ""
                    val dayName = info?.second ?: ""
                    Text(stringResource(id = R.string.home_last_active, dayName, planName))
                } else {
                    Text(stringResource(id = R.string.home_no_workout))
                }
            }
            item { Divider() }
            item {
                SectionHeader(title = "Woche")
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..6) {
                            val date = startOfWeek.plusDays(i.toLong())
                            val symbol = when {
                                history.value.containsKey(date) -> "✅"
                                progress.value?.restDay == i -> "🌙"
                                i == 5 && progress.value?.modularRest == true -> "🌙"
                                date.isBefore(ctx) -> "❌"
                                else -> "⬜"
                            }
                            Text(
                                text = symbol,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { selectedDate.value = date },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            item { Divider() }
            item {
                SectionHeader(title = "Fortschritt")
                Text(stringResource(id = R.string.home_streak, viewModel.workoutStreak))
                Text(stringResource(id = R.string.home_week_progress, viewModel.workoutsThisWeek, 5))
                Button(onClick = { /* TODO open progress */ }) {
                    Text(stringResource(id = R.string.home_view_progress))
                }
            }
            item { Divider() }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    Button(onClick = { /* TODO start workout */ }) {
                        Text(stringResource(id = R.string.home_start_workout))
                    }
                    Button(onClick = { /* TODO open progress */ }) {
                        Text(stringResource(id = R.string.home_view_progress))
                    }
                }
            }
        }
        if (selectedDate.value != null) {
            AlertDialog(
                onDismissRequest = { selectedDate.value = null },
                confirmButton = {
                    Button(onClick = { selectedDate.value = null }) {
                        Text(stringResource(id = R.string.close))
                    }
                },
                title = { Text(selectedDate.value.toString()) },
                text = {
                    val info = entryInfo.value
                    if (info != null) {
                        Text("${info.second} – ${info.first}")
                    } else {
                        Text(stringResource(id = R.string.home_no_workout))
                    }
                }
            )
        }
    }
}

private fun calculatePlanIndex(state: WeekProgress): Int {
    var idx = state.day
    if (state.restDay in 0..6 && state.day > state.restDay) idx--
    if (state.day > 5) idx--
    return idx
}
