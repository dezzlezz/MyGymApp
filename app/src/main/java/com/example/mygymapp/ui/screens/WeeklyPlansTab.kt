package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.model.PlanType
import com.example.mygymapp.ui.components.PlanCard
import com.example.mygymapp.ui.components.PlanDetailSheet
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.example.mygymapp.ui.viewmodel.PlansViewModelFactory
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlansTab() {
    val context = LocalContext.current
    val repo = remember(context) {
        PlanRepository(AppDatabase.getDatabase(context).planDao())
    }
    val viewModel: PlansViewModel = viewModel(factory = PlansViewModelFactory(repo))
    val exerciseViewModel: ExerciseViewModel = viewModel()

    val plans by viewModel.plans.observeAsState(emptyList())
    val exercises by exerciseViewModel.allExercises.observeAsState(emptyList())

    var showAdd by remember { mutableStateOf(false) }
    var detail by remember { mutableStateOf<PlanWithExercises?>(null) }

    LaunchedEffect(Unit) {
        viewModel.switchType(PlanType.WEEKLY)
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            items(plans, key = { it.planId }) { plan ->
                PlanCard(plan = plan, onClick = {
                    viewModel.load(plan.planId).observeForever { pw ->
                        detail = pw
                    }
                })
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

    detail?.let { pw ->
        val map = exercises.associate { it.id to it.name }
        PlanDetailSheet(planWithExercises = pw, exerciseMap = map, onClose = { detail = null })
    }
}