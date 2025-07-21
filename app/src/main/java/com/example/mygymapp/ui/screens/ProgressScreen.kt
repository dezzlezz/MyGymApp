package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.R
import com.example.mygymapp.model.MuscleGroupStat
import com.example.mygymapp.ui.components.MuscleGroupStatsChart
import com.example.mygymapp.ui.components.WorkoutHistoryCalendar
import com.example.mygymapp.viewmodel.ProfileViewModel
import java.time.LocalDate

@Composable
fun ProgressScreen(viewModel: ProfileViewModel = viewModel()) {
    val history by viewModel.history.collectAsState()
    val stats by produceState(initialValue = emptyList<MuscleGroupStat>(), history) {
        value = viewModel.getMuscleGroupStats()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(id = R.string.progress_log), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text(stringResource(id = R.string.progress_history), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        WorkoutHistoryCalendar(
            currentDate = LocalDate.now(),
            workoutHistory = history,
            onDateClick = { date ->
                history[date]?.let { entry ->
                    viewModel.getEntryInfo(entry) { plan, day ->
                        // TODO: show detail dialog or bottom sheet
                    }
                }
            }
        )
        Spacer(Modifier.height(24.dp))
        Text(stringResource(id = R.string.progress_muscle_groups), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        MuscleGroupStatsChart(stats)
    }
}
