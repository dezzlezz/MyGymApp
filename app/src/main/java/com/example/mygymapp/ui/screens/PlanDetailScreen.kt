package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.R
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.viewmodel.PlansViewModel
import com.example.mygymapp.viewmodel.PlansViewModelFactory
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(planId: Long, navController: NavController) {
    val context = LocalContext.current
    val repo = remember(context) { PlanRepository(AppDatabase.getDatabase(context).planDao()) }
    val viewModel: PlansViewModel = viewModel(key = "detail$planId", factory = PlansViewModelFactory(repo))
    val exerciseViewModel: ExerciseViewModel = viewModel()

    val planLive = remember(planId) { viewModel.load(planId) }
    val planWithExercises = planLive.observeAsState().value
    val exercises = exerciseViewModel.allExercises.observeAsState(emptyList()).value.associateBy { it.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.plan_detail)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (planWithExercises == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text(planWithExercises.plan.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(planWithExercises.plan.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                LazyColumn(Modifier.fillMaxSize()) {
                    items(planWithExercises.exercises.sortedBy { it.orderIndex }) { ref ->
                        val name = exercises[ref.exerciseId]?.name ?: "${ref.exerciseId}"
                        Text("• $name ${ref.sets}x${ref.reps}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
