package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExerciseCategory
import com.example.mygymapp.data.MuscleGroup
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.widgets.StarRating
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseAddEditScreen(
    viewModel: ExerciseViewModel,
    exerciseId: Long?,
    onSaved: () -> Unit
) {
    val editing = exerciseId != null
    val oldExercise = if (editing) viewModel.getById(exerciseId!!) else null

    var name by rememberSaveable { mutableStateOf(oldExercise?.name ?: "") }
    var description by rememberSaveable { mutableStateOf(oldExercise?.description ?: "") }
    var category by rememberSaveable { mutableStateOf(oldExercise?.category ?: ExerciseCategory.Gym) }
    var likeability by rememberSaveable { mutableStateOf(oldExercise?.likeability ?: 3) }
    var muscleGroup by rememberSaveable { mutableStateOf(oldExercise?.muscleGroup ?: MuscleGroup.Arms) }
    var muscle by rememberSaveable { mutableStateOf(oldExercise?.muscle ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editing) "Edit Exercise" else "Add Exercise") }
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            // Category Dropdown
            CategoryDropdown(category, onCategoryChange = { category = it })
            // Muscle Group Dropdown
            MuscleGroupDropdown(muscleGroup, onMuscleGroupChange = { muscleGroup = it })
            OutlinedTextField(
                value = muscle,
                onValueChange = { muscle = it },
                label = { Text("Muscle") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            // Likeability Stars
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Likeability: ")
                StarRating(rating = likeability)
                Spacer(Modifier.width(8.dp))
                Slider(
                    value = likeability.toFloat(),
                    onValueChange = { likeability = it.toInt() },
                    valueRange = 1f..5f,
                    steps = 3
                )
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    val exercise = Exercise(
                        id = oldExercise?.id ?: 0,
                        name = name,
                        description = description,
                        category = category,
                        likeability = likeability,
                        muscleGroup = muscleGroup,
                        muscle = muscle
                    )
                    if (editing) viewModel.update(exercise) else viewModel.insert(exercise)
                    onSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && muscle.isNotBlank()
            ) {
                Text(if (editing) "Save changes" else "Add Exercise")
            }
        }
    }
}

@Composable
fun CategoryDropdown(selected: ExerciseCategory, onCategoryChange: (ExerciseCategory) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selected.name)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ExerciseCategory.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        onCategoryChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MuscleGroupDropdown(selected: MuscleGroup, onMuscleGroupChange: (MuscleGroup) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selected.name)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MuscleGroup.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.name) },
                    onClick = {
                        onMuscleGroupChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
