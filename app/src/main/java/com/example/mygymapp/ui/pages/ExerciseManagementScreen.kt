package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.components.ExerciseCardWithHighlight

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExerciseManagementScreen(navController: NavController) {
    val vm: ExerciseViewModel = viewModel()
    val exercises by vm.allExercises.observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }

    val categories = ExerciseCategory.values().map { it.display }
    val muscles = MuscleGroup.values().map { it.display }

    val normalizedQuery = searchQuery.replace("\\s".toRegex(), "").lowercase()

    val filteredExercises = exercises.filter {
        (selectedMuscleGroup == null || it.muscleGroup.display == selectedMuscleGroup) && (
                it.name.replace("\\s".toRegex(), "").lowercase().contains(normalizedQuery) ||
                        it.category.display.replace("\\s".toRegex(), "").lowercase().contains(normalizedQuery) ||
                        it.muscleGroup.display.replace("\\s".toRegex(), "").lowercase().contains(normalizedQuery)
                )
    }

    val groupedExercises = filteredExercises.groupBy { it.muscleGroup.display }
    val collapsedStates = remember { mutableStateMapOf<String, Boolean>() }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Exercise Library",
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search", fontFamily = FontFamily.Serif) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedMuscleGroup == null,
                        onClick = { selectedMuscleGroup = null },
                        label = { Text("All", fontFamily = FontFamily.Serif) }
                    )
                    muscles.forEach { muscle ->
                        FilterChip(
                            selected = selectedMuscleGroup == muscle,
                            onClick = { selectedMuscleGroup = muscle },
                            label = { Text(muscle, fontFamily = FontFamily.Serif) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isNotBlank() || selectedMuscleGroup != null) {
                        if (filteredExercises.isEmpty()) {
                            Text(
                                text = "No exercises found.",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredExercises) { ex ->
                                    ExerciseCardWithHighlight(
                                        ex,
                                        normalizedQuery,
                                        onEdit = {
                                            navController.navigate("exercise_editor?editId=${ex.id}")
                                        },
                                        onDelete = { vm.delete(ex.id) }
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            groupedExercises.forEach { (muscleGroup, items) ->
                                val isCollapsed = collapsedStates[muscleGroup] ?: true
                                stickyHeader {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                collapsedStates[muscleGroup] = !isCollapsed
                                            },
                                        color = MaterialTheme.colorScheme.surface,
                                        tonalElevation = 4.dp
                                    ) {
                                        Text(
                                            text = if (isCollapsed) "▸ $muscleGroup" else "▾ $muscleGroup",
                                            modifier = Modifier.padding(
                                                vertical = 8.dp,
                                                horizontal = 16.dp
                                            ),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontFamily = FontFamily.Serif
                                            )
                                        )
                                    }
                                }
                                if (!isCollapsed) {
                                    items(items) { ex ->
                                        ExerciseCardWithHighlight(
                                            ex,
                                            "",
                                            onEdit = {
                                                navController.navigate("exercise_editor?editId=${ex.id}")
                                            },
                                            onDelete = { vm.delete(ex.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate("exercise_editor") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("➕ Add", fontFamily = FontFamily.Serif)
            }
        }
    }
}
