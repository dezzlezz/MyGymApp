package com.example.mygymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.components.PlanCard
import com.example.mygymapp.viewmodel.PlansViewModel
import com.example.mygymapp.viewmodel.PlansViewModelFactory
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.mygymapp.ui.theme.FogGray
import com.example.mygymapp.ui.theme.PineGreen
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.example.mygymapp.model.Equipment
import com.example.mygymapp.R
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DailyPlansTab(navController: NavController) {
    val context = LocalContext.current
    val repo = remember(context) {
        PlanRepository(AppDatabase.getDatabase(context).planDao())
    }
    val viewModel: PlansViewModel = viewModel(key = "dailyPlans", factory = PlansViewModelFactory(repo))
    val exerciseViewModel: ExerciseViewModel = viewModel()

    val plans by viewModel.plans.observeAsState(emptyList())
    val exercises by exerciseViewModel.allExercises.observeAsState(emptyList())

    var showAdd by remember { mutableStateOf(false) }
    var maxTime by rememberSaveable { mutableIntStateOf(60) }
    val availableEquipment = remember { mutableStateListOf<String>() }

    val filtered = plans.filter { plan ->
        plan.durationMinutes <= maxTime &&
            plan.requiredEquipment.all { it in availableEquipment }
    }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(id = R.string.max_time_filter, maxTime))
            Slider(
                value = maxTime.toFloat(),
                onValueChange = { maxTime = it.roundToInt() },
                valueRange = 10f..60f
            )
            Spacer(Modifier.height(4.dp))
            Text(stringResource(id = R.string.equipment_filter))
            FlowRow {
                Equipment.options.forEach { eq ->
                    FilterChip(
                        selected = eq in availableEquipment,
                        onClick = {
                            if (eq in availableEquipment) availableEquipment.remove(eq) else availableEquipment.add(eq)
                        },
                        label = { Text(eq) }
                    )
                    Spacer(Modifier.width(4.dp))
                }
            }
        }

        Box(Modifier.weight(1f)) {
            LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                items(filtered, key = { it.planId }) { plan ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        when (it) {
                            DismissValue.DismissedToEnd -> {
                                viewModel.delete(plan)
                                true
                            }
                            DismissValue.DismissedToStart -> {
                                navController.navigate("editDailyPlan/${plan.planId}")
                                false
                            }
                            else -> false
                        }
                    }
                )

                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                    background = {
                        val dir = dismissState.dismissDirection ?: return@SwipeToDismiss
                        val color = if (dir == DismissDirection.StartToEnd) Color.Red else FogGray
                        val icon = if (dir == DismissDirection.StartToEnd) Icons.Filled.Delete else Icons.Filled.Edit
                        val align = if (dir == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color)
                                .padding(horizontal = 20.dp),
                            contentAlignment = align
                        ) {
                            Icon(icon, contentDescription = null, tint = PineGreen)
                        }
                    },
                    dismissContent = {
                        PlanCard(plan = plan, onClick = {
                            navController.navigate("planDetail/${plan.planId}")
                        })
                    }
                )
            }
        }
            FloatingActionButton(onClick = { showAdd = true }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Add Plan")
            }
        }

    if (showAdd) {
        AddDailyPlanSheet(
            exercises = exercises,
            onSave = { plan, refs ->
                viewModel.save(plan, refs)
                showAdd = false
            },
            onCancel = { showAdd = false }
        )
    }
}
    }