package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.widgets.DifficultyRating
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.mygymapp.model.musclesByGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseScreen(
    exerciseId: Long,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ExerciseViewModel = viewModel()
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var difficulty by rememberSaveable { mutableIntStateOf(3) }
    var category by rememberSaveable { mutableStateOf(ExerciseCategory.Calisthenics) }
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }
    var group by rememberSaveable { mutableStateOf(MuscleGroup.Legs) }
    var groupExpanded by rememberSaveable { mutableStateOf(false) }
    var muscle by rememberSaveable { mutableStateOf("") }
    var muscleExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(exerciseId) {
        viewModel.getById(exerciseId)?.let { ex ->
            name = ex.name
            description = ex.description
            difficulty = ex.likeability
            category = ex.category
            group = ex.muscleGroup
            muscle = ex.muscle
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Exercise") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val exercise = Exercise(
                            id = exerciseId,
                            name = name,
                            description = description,
                            category = category,
                            likeability = difficulty,
                            muscleGroup = group,
                            muscle = muscle
                        )
                        viewModel.update(exercise)
                        onDone()
                    },
                    enabled = name.isNotBlank() && muscle.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) { Text("Speichern") }
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Abbrechen") }
            }
        }
    }
}