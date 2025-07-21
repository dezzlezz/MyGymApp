package com.example.mygymapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
                SectionHeader(title = stringResource(id = R.string.home_today_header))
                Crossfade(targetState = todayPlan.value != null && progress.value != null) { hasPlan ->
                    if (hasPlan) {
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
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (groups.isNotBlank()) {
                            Text(stringResource(id = R.string.home_goal, groups), style = MaterialTheme.typography.bodySmall)
                        }
                        Button(onClick = { /* TODO start workout */ }, shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                            Text(stringResource(id = R.string.home_start_workout))
                        }
                    } else {
                        Text(stringResource(id = R.string.home_no_plan), style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { /* TODO choose plan */ }, shape = RoundedCornerShape(12.dp)) {
                            Text(stringResource(id = R.string.home_choose_plan))
                        }
                    }
                }
            }

            item {
                SectionHeader(title = stringResource(id = R.string.home_last_workout_header))
                val last = viewModel.lastWorkout
                val prInfo = remember { mutableStateOf<Pair<String, Int>?>(null) }
                LaunchedEffect(last) {
                    prInfo.value = last?.let { viewModel.getPrInfo(it.date) }
                }
                Crossfade(targetState = last != null) { has ->
                    if (has) {
                        val info = entryInfo.value
                        val planName = info?.first ?: ""
                        val dayName = info?.second ?: ""
                        Column {
                            Text(stringResource(id = R.string.home_last_active, dayName, planName))
                            AnimatedVisibility(prInfo.value != null) {
                                Text(
                                    "\uD83C\uDFC6 ${prInfo.value?.first}: ${prInfo.value?.second} Reps – neuer PR!",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    } else {
                        Text(stringResource(id = R.string.home_no_workout), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                SectionHeader(title = stringResource(id = R.string.home_week_header))
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
                            val icon = when {
                                history.value.containsKey(date) -> Icons.Outlined.Check
                                progress.value?.restDay == i -> Icons.Outlined.Bedtime
                                i == 5 && progress.value?.modularRest == true -> Icons.Outlined.Bedtime
                                date.isBefore(ctx) -> Icons.Outlined.Close
                                else -> Icons.Outlined.RadioButtonUnchecked
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { selectedDate.value = date }
                                    .padding(horizontal = 4.dp)
                            ) {
                                Text(date.dayOfWeek.name.substring(0, 2))
                                Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(title = stringResource(id = R.string.home_progress_header))
                Text(stringResource(id = R.string.home_streak, viewModel.workoutStreak), style = MaterialTheme.typography.labelLarge)
                Text(stringResource(id = R.string.home_week_progress, viewModel.workoutsThisWeek, 5))
                Button(onClick = { /* TODO open progress */ }, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Outlined.BarChart, contentDescription = null)
                    Text(stringResource(id = R.string.home_view_progress))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
                ) {
                    Button(onClick = { /* TODO start workout */ }, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Outlined.PlayArrow, contentDescription = null)
                        Text(stringResource(id = R.string.home_start_workout))
                    }
                    OutlinedButton(onClick = { /* TODO open progress */ }, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Outlined.BarChart, contentDescription = null)
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
