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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.model.PlanType
import com.example.mygymapp.components.PlanCard
import com.example.mygymapp.viewmodel.PlansViewModel
import com.example.mygymapp.viewmodel.PlansViewModelFactory
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.mygymapp.ui.theme.FogGray
import com.example.mygymapp.ui.theme.PineGreen

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlansTab(navController: NavController) {
    val context = LocalContext.current
    val repo = remember(context) {
        PlanRepository(AppDatabase.getDatabase(context).planDao())
    }
    val viewModel: PlansViewModel = viewModel(key = "weeklyPlans", factory = PlansViewModelFactory(repo))
    val exerciseViewModel: ExerciseViewModel = viewModel()

    val plans by viewModel.plans.observeAsState(emptyList())
    val exercises by exerciseViewModel.allExercises.observeAsState(emptyList())

    var showAdd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.switchType(PlanType.WEEKLY)
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            items(plans, key = { it.planId }) { plan ->
                val dismissState = rememberDismissState(
                    confirmStateChange = {
                        when (it) {
                            DismissValue.DismissedToEnd -> {
                                viewModel.delete(plan)
                                true
                            }
                            DismissValue.DismissedToStart -> {
                                navController.navigate("editWeeklyPlan/${plan.planId}")
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
        AddWeeklyPlanSheet(exercises = exercises,
            onSave = { plan, refs, days ->
                viewModel.save(plan, refs, days)
                showAdd = false
            },
            onCancel = { showAdd = false }
        )
    }
}