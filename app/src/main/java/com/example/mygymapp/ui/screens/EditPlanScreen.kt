package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.example.mygymapp.ui.viewmodel.PlansViewModelFactory

@Composable
fun EditPlanScreen(
    planId: Long,
    onDone: () -> Unit,
    onCancel: () -> Unit = onDone,
) {
    val context = LocalContext.current
    val repo = remember(context) { PlanRepository(AppDatabase.getDatabase(context).planDao()) }
    val viewModel: PlansViewModel = viewModel(factory = PlansViewModelFactory(repo))

    val planLive = remember(planId) { viewModel.load(planId) }
    val planWithExercises by planLive.observeAsState()

    planWithExercises?.let { pw ->
        AddEditPlanSheet(
            initialPlan = pw.plan,
            initialExercises = pw.exercises,
            onSave = { plan, refs ->
                viewModel.save(plan, refs)
                onDone()
            },
            onCancel = onCancel
        )
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}