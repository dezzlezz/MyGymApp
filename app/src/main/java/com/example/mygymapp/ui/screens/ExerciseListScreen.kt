package com.example.mygymapp.ui.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberDismissState
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.widgets.StarRating
import androidx.compose.material.ExperimentalMaterialApi


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    viewModel: ExerciseViewModel,
    onAddExercise: () -> Unit,
    onEditExercise: (Long) -> Unit
) {
    val exercises by viewModel.allExercises.observeAsState(emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExercise) {
                Icon(Icons.Filled.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            if (exercises.isEmpty()) {
                // Zeige nur Info-Text ohne Button, FAB ist ja immer da!
                Column(
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text("No exercises yet!", style = MaterialTheme.typography.titleMedium)
                    // Optional: Button kann raus! FAB übernimmt das Hinzufügen.
                }
            } else {
                LazyColumn {
                    items(
                        items = exercises,
                        key = { it.id }
                    ) { exercise ->
                        val dismissState = rememberDismissState()
                        if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                            onEditExercise(exercise.id)
                            LaunchedEffect(Unit) { dismissState.reset() }
                        }
                        if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                            viewModel.delete(exercise.id)
                        }

                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(
                                DismissDirection.StartToEnd, // Delete
                                DismissDirection.EndToStart  // Edit
                            ),
                            background = {
                                val color = when (dismissState.dismissDirection) {
                                    DismissDirection.StartToEnd -> Color.Red
                                    DismissDirection.EndToStart -> Color.Yellow
                                    else -> Color.Transparent
                                }
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(20.dp),
                                    contentAlignment = when (dismissState.dismissDirection) {
                                        DismissDirection.StartToEnd -> Alignment.CenterStart
                                        DismissDirection.EndToStart -> Alignment.CenterEnd
                                        else -> Alignment.Center
                                    }
                                ) {
                                    if (dismissState.dismissDirection == DismissDirection.StartToEnd) {
                                        Text("Delete", color = MaterialTheme.colorScheme.onError)
                                    } else if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                        Text("Edit", color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            },
                            dismissContent = {
                                ExerciseListItem(exercise)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EmptyState(onAddExercise: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text("No exercises yet!", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onAddExercise) {
            Text("Add your first Exercise")
        }
    }
}

@Composable
fun ExerciseListItem(ex: Exercise) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(ex.name, style = MaterialTheme.typography.titleMedium)
                Text("${ex.muscleGroup} • ${ex.category}", style = MaterialTheme.typography.bodySmall)
            }
            StarRating(rating = ex.likeability)
        }
    }
}
