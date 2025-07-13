package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.ui.components.ExerciseCard
import com.example.mygymapp.viewmodel.ExerciseViewModel

@Composable
fun ExercisesScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel(),
    onEditExercise: (Long) -> Unit = {}
) {
    val exercises by viewModel.allExercises.observeAsState(emptyList())

    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<ExerciseCategory?>(null) }
    var searchFocused by remember { mutableStateOf(false) }

    val filteredExercises = exercises.filter { ex ->
        ex.name.contains(query, ignoreCase = true) &&
            (selectedCategory == null || ex.category == selectedCategory)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addExercise") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text(stringResource(id = R.string.search_exercises)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onFocusChanged { searchFocused = it.isFocused }
            )

            if (searchFocused) {
                Row(Modifier.padding(horizontal = 8.dp)) {
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.display) }
                    )
                    Spacer(Modifier.width(8.dp))
                    ExerciseCategory.values().forEachIndexed { index, category ->
                        if (index != 0) Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.display) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(if (searchFocused) 4.dp else 0.dp))

            if (filteredExercises.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Keine Übungen gefunden")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExercises, key = { it.id }) { ex ->
                        ExerciseCard(
                            ex = ex,
                            onClick = { onEditExercise(ex.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(ex) }
                        )
                    }
                }
            }
        }
    }
}
