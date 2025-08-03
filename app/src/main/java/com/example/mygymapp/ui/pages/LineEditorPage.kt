package com.example.mygymapp.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Mood
import com.example.mygymapp.ui.components.ExerciseItem
import com.example.mygymapp.ui.components.MoodChip
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticCard
import com.example.mygymapp.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LineEditorPage(
    initial: Line? = null,
    onSave: (Line) -> Unit,
    onCancel: () -> Unit
) {
    val vm: ExerciseViewModel = viewModel()
    val allExercises by vm.allExercises.observeAsState(emptyList())

    var title by remember { mutableStateOf(initial?.title ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var muscleGroup by remember { mutableStateOf(initial?.muscleGroup ?: "") }
    var note by remember { mutableStateOf(initial?.note ?: "") }
    var mood by remember { mutableStateOf(initial?.mood ?: Mood.CALM) }

    val selectedExercises = remember {
        mutableStateListOf<com.example.mygymapp.model.Exercise>().apply {
            initial?.exercises?.let { addAll(it) }
        }
    }
    val supersets = remember { mutableStateListOf<Pair<Long, Long>>().apply { initial?.supersets?.let { addAll(it) } } }

    var search by remember { mutableStateOf("") }

    val categoryOptions = listOf("Push", "Pull", "Core", "Cardio", "Recovery")
    val muscleOptions = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Full Body")

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "✒ Compose your daily line",
                fontFamily = GaeguBold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            PoeticCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        textStyle = TextStyle(fontFamily = GaeguRegular)
                    )
                    DropdownField("Category", category, { category = it }, categoryOptions)
                    DropdownField("Muscle Group", muscleGroup, { muscleGroup = it }, muscleOptions)
                    Text("Mood", fontFamily = GaeguBold)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Mood.values().forEach { m ->
                            MoodChip(mood = m, selected = m == mood, onClick = { mood = m })
                        }
                    }
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note") },
                        modifier = Modifier.heightIn(min = 80.dp),
                        textStyle = TextStyle(fontFamily = GaeguRegular)
                    )
                }
            }

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Filter saved exercises") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = GaeguRegular)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(allExercises.filter { it.name.contains(search, ignoreCase = true) }) { ex ->
                    ExerciseRow(ex) {
                        selectedExercises.add(
                            com.example.mygymapp.model.Exercise(
                                id = ex.id,
                                name = ex.name,
                                sets = 3,
                                repsOrDuration = "10"
                            )
                        )
                    }
                }
            }

            if (selectedExercises.isNotEmpty()) {
                Text("Today's plan", fontFamily = GaeguBold)
                PoeticCard {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val supersetMap = supersets.mapIndexed { index, pair -> pair.first to index }.toMap()
                        selectedExercises.forEach { ex ->
                            val label = supersetMap[ex.id]?.let { idx -> "Superset ${(65 + idx).toChar()}" }
                            label?.let { Text(it, fontFamily = GaeguBold) }
                            ExerciseItem(exercise = ex)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Cancel", fontFamily = GaeguRegular) }
                Spacer(Modifier.width(16.dp))
                Button(onClick = {
                    val newLine = Line(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        category = category,
                        muscleGroup = muscleGroup,
                        mood = mood,
                        exercises = selectedExercises.toList(),
                        supersets = supersets.toList(),
                        note = note,
                        isArchived = false
                    )
                    onSave(newLine)
                }) {
                    Text("Save", fontFamily = GaeguBold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(label: String, value: String, onValueChange: (String) -> Unit, options: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            textStyle = TextStyle(fontFamily = GaeguRegular)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onValueChange(option); expanded = false })
            }
        }
    }
}

@Composable
private fun ExerciseRow(ex: Exercise, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .padding(vertical = 8.dp)
    ) {
        Text(ex.name, fontFamily = GaeguRegular)
        Spacer(modifier = Modifier.weight(1f))
        Text("3 × 10", fontFamily = GaeguRegular)
    }
}
