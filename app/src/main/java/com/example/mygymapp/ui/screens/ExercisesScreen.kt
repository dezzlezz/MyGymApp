package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.widgets.DifficultyRating

private val musclesByGroup = mapOf(
    MuscleGroup.Arms to listOf("Biceps", "Triceps", "Forearm"),
    MuscleGroup.Legs to listOf("Quadriceps", "Hamstrings", "Calves"),
    MuscleGroup.Core to listOf("Abs", "Obliques"),
    MuscleGroup.Chest to listOf("Upper Chest", "Lower Chest"),
    MuscleGroup.Shoulders to listOf("Front", "Lateral", "Rear"),
    MuscleGroup.Back to listOf("Upper Back", "Lower Back", "Lats")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(viewModel: ExerciseViewModel = viewModel()) {
    val exercises by viewModel.allExercises.observeAsState(emptyList())

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf(3) }
    var category by remember { mutableStateOf(ExerciseCategory.Calisthenics) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var group by remember { mutableStateOf(MuscleGroup.Legs) }
    var groupExpanded by remember { mutableStateOf(false) }
    var muscle by remember { mutableStateOf(musclesByGroup[group]?.first() ?: "") }
    var muscleExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Neue Übung", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Beschreibung") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(Modifier.height(8.dp))
        Text("Schwierigkeit")
        DifficultyRating(rating = difficulty, onRatingChanged = { difficulty = it })
        Spacer(Modifier.height(8.dp))
        // Category dropdown
        ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = category.display,
                onValueChange = {},
                label = { Text("Art") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                ExerciseCategory.values().forEach {
                    DropdownMenuItem(text = { Text(it.display) }, onClick = {
                        category = it
                        categoryExpanded = false
                    })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // Muscle group dropdown
        ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = !groupExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = group.display,
                onValueChange = {},
                label = { Text("Muskelgruppe") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                MuscleGroup.values().forEach {
                    DropdownMenuItem(text = { Text(it.display) }, onClick = {
                        group = it
                        muscle = musclesByGroup[it]?.first() ?: ""
                        groupExpanded = false
                    })
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // Specific muscle dropdown
        val muscles = musclesByGroup[group] ?: emptyList()
        ExposedDropdownMenuBox(expanded = muscleExpanded, onExpandedChange = { muscleExpanded = !muscleExpanded }) {
            OutlinedTextField(
                readOnly = true,
                value = muscle,
                onValueChange = {},
                label = { Text("Muskel") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = muscleExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = muscleExpanded, onDismissRequest = { muscleExpanded = false }) {
                muscles.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = {
                        muscle = option
                        muscleExpanded = false
                    })
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val exercise = Exercise(
                    name = name,
                    description = description,
                    category = category,
                    likeability = difficulty,
                    muscleGroup = group,
                    muscle = muscle
                )
                viewModel.insert(exercise)
                name = ""
                description = ""
            },
            enabled = name.isNotBlank() && muscle.isNotBlank()
        ) {
            Text("Speichern")
        }
        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(8.dp))
        Text("Gespeicherte Übungen", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.fillMaxSize()) {
            items(exercises) { ex ->
                ExerciseRow(ex)
            }
        }
    }
}
@Composable
private fun ExerciseRow(exercise: Exercise) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(exercise.name, style = MaterialTheme.typography.titleMedium)
            Text("${exercise.muscle} • ${exercise.category.display}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
