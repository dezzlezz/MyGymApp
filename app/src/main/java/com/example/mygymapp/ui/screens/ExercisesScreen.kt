package com.example.mygymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.widgets.StarRating
import com.example.mygymapp.ui.theme.EditGray
import com.example.mygymapp.ui.theme.PineGreen

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ExercisesScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel(),
    onEditExercise: (Long) -> Unit = {}
) {
    val exercises by viewModel.allExercises.observeAsState(emptyList())
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addExercise") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        if (exercises.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No exercises yet!", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(exercises, key = { it.id }) { ex ->
                    ExerciseListItem(
                        ex = ex,
                        onEdit = { onEditExercise(it) },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ExerciseListItem(ex: Exercise, onEdit: (Long) -> Unit, viewModel: ExerciseViewModel) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            when (it) {
                DismissValue.DismissedToEnd -> {
                    viewModel.delete(ex.id)
                    true
                }
                DismissValue.DismissedToStart -> {
                    onEdit(ex.id)
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
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = if (direction == DismissDirection.StartToEnd) Color.Red else EditGray
            val icon = if (direction == DismissDirection.StartToEnd) Icons.Default.Delete else Icons.Default.Edit
            val alignment = if (direction == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = null, tint = PineGreen)
            }
        },
        dismissContent = {
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
                        Text("${ex.muscle} • ${ex.category.display}", style = MaterialTheme.typography.bodySmall)
                    }
                    StarRating(rating = ex.likeability)
                }

            }
        }
    )
}



