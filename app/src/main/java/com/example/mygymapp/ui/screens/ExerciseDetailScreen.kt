package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExercisePRStore
import com.example.mygymapp.data.ExerciseLogStore
import com.example.mygymapp.model.ExerciseLogEntry
import com.example.mygymapp.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: ExerciseViewModel = viewModel()
) {
    val context = LocalContext.current
    val prStore = remember(context) { ExercisePRStore.getInstance(context) }
    val logStore = remember(context) { ExerciseLogStore.getInstance(context) }
    var exercise by remember { mutableStateOf<Exercise?>(null) }
    var logs by remember { mutableStateOf<List<ExerciseLogEntry>>(emptyList()) }

    LaunchedEffect(exerciseId) {
        exercise = viewModel.getById(exerciseId)
        logs = logStore.load(exerciseId).sortedByDescending { it.date }
    }

    val ex = exercise
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ex?.name ?: stringResource(id = R.string.loading)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(exerciseId) }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_exercise))
                    }
                }
            )
        }
    ) { innerPadding ->
        if (ex != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text("${ex.muscleGroup.display} • ${ex.category.display}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text(ex.description)
                val pr = prStore.getPR(exerciseId)
                if (pr > 0) {
                    Spacer(Modifier.height(16.dp))
                    Text(stringResource(id = R.string.pr_label, pr), style = MaterialTheme.typography.bodyMedium)
                }

                if (logs.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    Text(stringResource(R.string.progress_log), style = MaterialTheme.typography.titleSmall)
                    logs.take(5).forEach {
                        Text("${it.date}: ${it.reps} Reps")
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
