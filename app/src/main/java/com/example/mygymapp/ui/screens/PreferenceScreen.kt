package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.R
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.model.Equipment
import com.example.mygymapp.model.GoalType
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.model.UserPreferences
import com.example.mygymapp.viewmodel.PreferencesViewModel
import com.example.mygymapp.viewmodel.PreferencesViewModelFactory
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreferenceScreen(navController: NavController) {
    val context = LocalContext.current
    val repo = remember(context) { PlanRepository(AppDatabase.getDatabase(context).planDao()) }
    val viewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModelFactory(repo))

    var days by rememberSaveable { mutableIntStateOf(3) }
    var duration by rememberSaveable { mutableIntStateOf(30) }
    val equipment = remember { mutableStateListOf<String>() }
    var goal by rememberSaveable { mutableStateOf(GoalType.FIT) }
    val groups = remember { mutableStateListOf<MuscleGroup>() }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.preferences_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            Text(stringResource(id = R.string.days_per_week, days))
            Slider(value = days.toFloat(), onValueChange = { days = it.roundToInt() }, valueRange = 1f..7f)
            Spacer(Modifier.height(8.dp))

            Text(stringResource(id = R.string.max_duration_label, duration))
            Slider(value = duration.toFloat(), onValueChange = { duration = it.roundToInt() }, valueRange = 10f..60f)
            Spacer(Modifier.height(8.dp))

            Text(stringResource(id = R.string.goal_label))
            FlowRow {
                GoalType.values().forEach { g ->
                    FilterChip(
                        selected = goal == g,
                        onClick = { goal = g },
                        label = { Text(stringResource(id = goalToString(g))) }
                    )
                    Spacer(Modifier.width(4.dp))
                }
            }
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

            Text(stringResource(id = R.string.focus_groups_label))
            FlowRow {
                MuscleGroup.values().forEach { mg ->
                    FilterChip(
                        selected = mg in groups,
                        onClick = {
                            if (mg in groups) groups.remove(mg) else groups.add(mg)
                        },
                        label = { Text(mg.display) }
                    )
                    Spacer(Modifier.width(4.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                val prefs = UserPreferences(days, duration, equipment.toSet(), goal, groups.toSet())
                viewModel.update(prefs)
                navController.navigate("suggestedPlans")
            }) {
                Text(stringResource(id = R.string.show_suggestions))
            }
        }
    }
}

@Composable
private fun goalToString(goal: GoalType): Int = when (goal) {
    GoalType.HYPERTROPHY -> R.string.goal_hypertrophy
    GoalType.STRENGTH -> R.string.goal_strength
    GoalType.ENDURANCE -> R.string.goal_endurance
    GoalType.FIT -> R.string.goal_fit
}

