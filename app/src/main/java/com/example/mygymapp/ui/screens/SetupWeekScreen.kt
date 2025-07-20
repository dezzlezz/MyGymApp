package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.R
import com.example.mygymapp.data.Plan
import com.example.mygymapp.model.WeekProgress
import com.example.mygymapp.viewmodel.WorkoutViewModel

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
private fun dayName(index: Int): String = stringResource(id = dayStrings[index])

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupWeekScreen(
    planId: Long,
    onStartWeek: (WeekProgress) -> Unit,
    onCancel: () -> Unit,
    viewModel: WorkoutViewModel = viewModel()
) {
    val planLive = remember(planId) { viewModel.loadPlan(planId) }
    val plan by planLive.observeAsState()
    val dailyPlans by viewModel.dailyPlans.observeAsState(emptyList())

    var restDay by remember { mutableIntStateOf(-1) }
    var modularRest by remember { mutableStateOf(false) }
    var modularPlan by remember { mutableStateOf<Plan?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.setup_week)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(plan?.plan?.name ?: stringResource(id = R.string.loading), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(id = R.string.select_rest_day))
            dayStrings.forEachIndexed { index, _ ->
                val d = dayName(index)
                val enabled = index != 5
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = restDay == index, onClick = { if (enabled) restDay = index }, enabled = enabled)
                    Text(if (index == 5) "$d (${stringResource(id = R.string.modular_day)})" else d)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = restDay == -1, onClick = { restDay = -1 })
                Text(stringResource(id = R.string.none))
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(id = R.string.modular_day))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = modularRest, onCheckedChange = { modularRest = it })
                Text(stringResource(id = R.string.rest_day))
            }
            if (!modularRest) {
                LazyColumn(Modifier.weight(1f, false)) {
                    items(dailyPlans, key = { it.planId }) { p ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            RadioButton(selected = modularPlan?.planId == p.planId, onClick = { modularPlan = p })
                            Text(p.name)
                        }
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }
            Button(
                onClick = {
                    val progress = WeekProgress(
                        weeklyPlanId = planId,
                        restDay = restDay,
                        modularPlanId = modularPlan?.planId,
                        modularRest = modularRest,
                        day = 0
                    )
                    onStartWeek(progress)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = modularRest || modularPlan != null
            ) { Text(stringResource(id = R.string.start_week_button)) }
        }
    }
}

