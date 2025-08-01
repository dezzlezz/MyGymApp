package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.components.PaperBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineEditorPage(
    initial: Line? = null,
    onSave: (Line) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var muscleGroup by remember { mutableStateOf(initial?.muscleGroup ?: "") }
    var mood by remember { mutableStateOf(initial?.mood ?: "") }
    var note by remember { mutableStateOf(initial?.note ?: "") }

    val exerciseList = remember {
        mutableStateListOf<Exercise>().apply { addAll(initial?.exercises ?: emptyList()) }
    }
    val supersets = remember {
        mutableStateListOf<Pair<Long, Long>>().apply { addAll(initial?.supersets ?: emptyList()) }
    }
    var showExerciseEditor by remember { mutableStateOf(false) }
    var selectedExerciseIndex by remember { mutableStateOf<Int?>(null) }
    val vm: ExerciseViewModel = viewModel()
    val allExercises by vm.allExercises.observeAsState(emptyList())
    var showExercisePicker by remember { mutableStateOf(false) }

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "✏️ Edit Line",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif
            )

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            OutlinedTextField(value = muscleGroup, onValueChange = { muscleGroup = it }, label = { Text("Muscle Group") })
            OutlinedTextField(value = mood, onValueChange = { mood = it }, label = { Text("Mood") })
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") })

            Text("Exercises", style = MaterialTheme.typography.titleMedium)
            exerciseList.forEachIndexed { index, exercise ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${exercise.name} – ${exercise.sets}×${exercise.repsOrDuration}")
                    Row {
                        TextButton(onClick = {
                            selectedExerciseIndex = index
                            showExerciseEditor = true
                        }) { Text("Edit") }
                        TextButton(onClick = { exerciseList.removeAt(index) }) { Text("Remove") }
                    }
                }
            }
            Button(onClick = {
                showExercisePicker = true
            }) { Text("➕ Add from List") }

            Text("Supersets", style = MaterialTheme.typography.titleMedium)
            supersets.forEachIndexed { index, pair ->
                val nameA = exerciseList.find { it.id == pair.first }?.name ?: "?"
                val nameB = exerciseList.find { it.id == pair.second }?.name ?: "?"
                Row {
                    Text("$nameA + $nameB")
                    TextButton(onClick = { supersets.removeAt(index) }) { Text("Remove") }
                }
            }
            if (exerciseList.size >= 2) {
                Button(onClick = {
                    val a = exerciseList.getOrNull(0)?.id ?: return@Button
                    val b = exerciseList.getOrNull(1)?.id ?: return@Button
                    supersets.add(a to b)
                }) { Text("➕ Add Superset (1+2)") }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val newLine = Line(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        category = category,
                        muscleGroup = muscleGroup,
                        mood = mood,
                        exercises = exerciseList.toList(),
                        supersets = supersets.toList(),
                        note = note,
                        isArchived = false
                    )
                    onSave(newLine)
                }) {
                    Text("Save")
                }
            }
        }
    }

    if (showExerciseEditor) {
        var name by remember { mutableStateOf("") }
        var sets by remember { mutableStateOf("3") }
        var reps by remember { mutableStateOf("12") }

        AlertDialog(
            onDismissRequest = { showExerciseEditor = false },
            confirmButton = {
                TextButton(onClick = {
                    val new = Exercise(
                        id = System.currentTimeMillis(),
                        name = name,
                        sets = sets.toIntOrNull() ?: 3,
                        repsOrDuration = reps
                    )
                    if (selectedExerciseIndex != null) {
                        exerciseList[selectedExerciseIndex!!] = new
                    } else {
                        exerciseList.add(new)
                    }
                    showExerciseEditor = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showExerciseEditor = false }) { Text("Cancel") }
            },
            title = { Text("Exercise") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                    OutlinedTextField(value = sets, onValueChange = { sets = it }, label = { Text("Sets") })
                    OutlinedTextField(value = reps, onValueChange = { reps = it }, label = { Text("Reps or Duration") })
                }
            }
        )
    }

    if (showExercisePicker) {
        ModalBottomSheet(onDismissRequest = { showExercisePicker = false }) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Choose an Exercise",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif
                )
                Spacer(Modifier.height(8.dp))
                allExercises.forEach { ex ->
                    TextButton(onClick = {
                        exerciseList.add(
                            Exercise(
                                id = System.currentTimeMillis(),
                                name = ex.name,
                                sets = 3,
                                repsOrDuration = "12"
                            )
                        )
                        showExercisePicker = false
                    }) {
                        Text(ex.name)
                    }
                }
            }
        }
    }
}

