package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.theme.Handwriting

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
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

    var search by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf<ExerciseCategory?>(null) }
    var muscleFilter by remember { mutableStateOf<MuscleGroup?>(null) }
    var favoritesOnly by remember { mutableStateOf(false) }

    var supersetMode by remember { mutableStateOf(false) }
    val supersetSelection = remember { mutableStateListOf<Long>() }

    var configExercise by remember { mutableStateOf<com.example.mygymapp.data.Exercise?>(null) }
    var showConfigSheet by remember { mutableStateOf(false) }

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
    var filtersVisible by remember { mutableStateOf(false) }

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

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", fontFamily = Handwriting) },
                textStyle = TextStyle(fontFamily = Handwriting, fontSize = 20.sp)
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category", fontFamily = Handwriting) },
                textStyle = TextStyle(fontFamily = Handwriting, fontSize = 20.sp)
            )
            OutlinedTextField(
                value = muscleGroup,
                onValueChange = { muscleGroup = it },
                label = { Text("Muscle Group", fontFamily = Handwriting) },
                textStyle = TextStyle(fontFamily = Handwriting, fontSize = 20.sp)
            )
            OutlinedTextField(
                value = mood,
                onValueChange = { mood = it },
                label = { Text("Mood", fontFamily = Handwriting) },
                textStyle = TextStyle(fontFamily = Handwriting, fontSize = 20.sp)
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note", fontFamily = Handwriting) },
                textStyle = TextStyle(fontFamily = Handwriting, fontSize = 20.sp)
            )

            Text("Exercises", style = MaterialTheme.typography.titleMedium)
            exerciseList.forEachIndexed { index, exercise ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = supersetMode) {
                            if (supersetSelection.contains(exercise.id)) supersetSelection.remove(exercise.id)
                            else supersetSelection.add(exercise.id)
                        }
                        .background(
                            if (supersetSelection.contains(exercise.id) && supersetMode) Color(0xFFD9CEB2) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${exercise.name} – ${exercise.sets}×${exercise.repsOrDuration}",
                        fontFamily = Handwriting
                    )
                    Row {
                        TextButton(onClick = {
                            selectedExerciseIndex = index
                            showExerciseEditor = true
                        }) { Text("Edit", fontFamily = Handwriting) }
                        TextButton(onClick = { exerciseList.removeAt(index) }) { Text("Remove", fontFamily = Handwriting) }
                    }
                }
            }
            Button(onClick = {
                showExercisePicker = true
            }) { Text("➕ Add movement", fontFamily = Handwriting) }

            Text("Supersets", style = MaterialTheme.typography.titleMedium, fontFamily = Handwriting)
            supersets.forEachIndexed { index, pair ->
                val nameA = exerciseList.find { it.id == pair.first }?.name ?: "?"
                val nameB = exerciseList.find { it.id == pair.second }?.name ?: "?"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$nameA + $nameB", fontFamily = Handwriting)
                    TextButton(onClick = { supersets.removeAt(index) }) { Text("Remove", fontFamily = Handwriting) }
                }
            }
            if (exerciseList.size >= 2) {
                if (supersetMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            if (supersetSelection.size == 2) {
                                supersets.add(supersetSelection[0] to supersetSelection[1])
                                supersetSelection.clear()
                                supersetMode = false
                            }
                        }) { Text("Group selected", fontFamily = Handwriting) }
                        TextButton(onClick = { supersetMode = false; supersetSelection.clear() }) { Text("Cancel", fontFamily = Handwriting) }
                    }
                } else {
                    TextButton(onClick = { supersetMode = true }) { Text("Add a superset", fontFamily = Handwriting) }
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                TextButton(onClick = onCancel) { Text("Cancel", fontFamily = Handwriting) }
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
                    Text("Save", fontFamily = Handwriting)
                }
            }
        }
    }

    if (showExerciseEditor) {
        var name by remember { mutableStateOf("") }
        var sets by remember { mutableStateOf("3") }
        var reps by remember { mutableStateOf("12") }
        var prGoal by remember { mutableStateOf("") }
        var exNote by remember { mutableStateOf("") }

        LaunchedEffect(showExerciseEditor) {
            if (showExerciseEditor) {
                selectedExerciseIndex?.let { idx ->
                    val ex = exerciseList[idx]
                    name = ex.name
                    sets = ex.sets.toString()
                    reps = ex.repsOrDuration
                    prGoal = ex.prGoal?.toString() ?: ""
                    exNote = ex.note
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showExerciseEditor = false },
            confirmButton = {
                TextButton(onClick = {
                    val new = Exercise(
                        id = System.currentTimeMillis(),
                        name = name,
                        sets = sets.toIntOrNull() ?: 3,
                        repsOrDuration = reps,
                        prGoal = prGoal.toIntOrNull(),
                        note = exNote
                    )
                    if (selectedExerciseIndex != null) {
                        exerciseList[selectedExerciseIndex!!] = new
                    } else {
                        exerciseList.add(new)
                    }
                    showExerciseEditor = false
                }) { Text("Save", fontFamily = Handwriting) }
            },
            dismissButton = {
                TextButton(onClick = { showExerciseEditor = false }) { Text("Cancel", fontFamily = Handwriting) }
            },
            title = { Text("Exercise", fontFamily = Handwriting) },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                    OutlinedTextField(value = sets, onValueChange = { sets = it }, label = { Text("How many sets?", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                    OutlinedTextField(value = reps, onValueChange = { reps = it }, label = { Text("How many times will you move?", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                    OutlinedTextField(value = prGoal, onValueChange = { prGoal = it }, label = { Text("Do you feel a personal challenge?", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                    OutlinedTextField(value = exNote, onValueChange = { exNote = it }, label = { Text("Notes", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                }
            }
        )
    }

    if (showExercisePicker) {
        val filtered = allExercises.filter { ex ->
            (search.isBlank() || ex.name.contains(search, ignoreCase = true)) &&
                    (categoryFilter == null || ex.category == categoryFilter) &&
                    (muscleFilter == null || ex.muscleGroup == muscleFilter) &&
                    (!favoritesOnly || ex.isFavorite)
        }
        ModalBottomSheet(onDismissRequest = { showExercisePicker = false }) {
            Column(Modifier
                .fillMaxHeight(0.9f)
                .padding(16.dp)) {
                Text(
                    "Choose a movement that resonates with today.",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = Handwriting
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search gently…", fontFamily = Handwriting) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = Handwriting)
                )
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { filtersVisible = !filtersVisible }) {
                    Text(if (filtersVisible) "Hide filters" else "Show filters", fontFamily = Handwriting)
                }
                if (filtersVisible) {
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = { categoryFilter = null }, label = { Text("All") })
                        ExerciseCategory.values().forEach { cat ->
                            AssistChip(
                                onClick = { categoryFilter = cat },
                                label = { Text(cat.display) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = { muscleFilter = null }, label = { Text("All") })
                        MuscleGroup.values().forEach { m ->
                            AssistChip(
                                onClick = { muscleFilter = m },
                                label = { Text(m.display) }
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { favoritesOnly = !favoritesOnly }) {
                            Icon(
                                imageVector = if (favoritesOnly) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                contentDescription = null
                            )
                        }
                        Text("Favorites", fontFamily = Handwriting)
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filtered) { ex ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    configExercise = ex
                                    showExercisePicker = false
                                    showConfigSheet = true
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ex.name, modifier = Modifier.weight(1f), fontFamily = Handwriting)
                                if (ex.isFavorite) Icon(Icons.Filled.Star, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfigSheet && configExercise != null) {
        val base = configExercise!!
        var setsText by remember { mutableStateOf("3") }
        var repsText by remember { mutableStateOf("12") }
        var prText by remember { mutableStateOf("") }
        var noteText by remember { mutableStateOf("") }

        ModalBottomSheet(onDismissRequest = { showConfigSheet = false }) {
            Column(Modifier.padding(16.dp)) {
                Text(base.name, style = MaterialTheme.typography.titleMedium, fontFamily = Handwriting)
                Spacer(Modifier.height(8.dp))
                if (base.description.isNotBlank()) {
                    Text(base.description, style = MaterialTheme.typography.bodySmall, fontFamily = Handwriting)
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(value = setsText, onValueChange = { setsText = it }, label = { Text("How many sets?", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                OutlinedTextField(value = repsText, onValueChange = { repsText = it }, label = { Text("How many times will you move?", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                OutlinedTextField(value = prText, onValueChange = { prText = it }, label = { Text("Do you feel a personal challenge?", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                OutlinedTextField(value = noteText, onValueChange = { noteText = it }, label = { Text("Notes", fontFamily = Handwriting) }, textStyle = TextStyle(fontFamily = Handwriting))
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showConfigSheet = false }) { Text("Cancel", fontFamily = Handwriting) }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        exerciseList.add(
                            Exercise(
                                id = System.currentTimeMillis(),
                                name = base.name,
                                sets = setsText.toIntOrNull() ?: 3,
                                repsOrDuration = repsText,
                                prGoal = prText.toIntOrNull(),
                                note = noteText
                            )
                        )
                        showConfigSheet = false
                    }) { Text("Add to Line", fontFamily = Handwriting) }
                }
            }
        }
    }
}

