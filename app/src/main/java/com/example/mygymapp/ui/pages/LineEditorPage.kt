package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.burnoutcrew.reorderable.*
import com.example.mygymapp.model.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LineEditorPage(
    initial: Line? = null,
    onSave: (Line) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var muscleGroup by remember { mutableStateOf(initial?.muscleGroup ?: "") }
    var note by remember { mutableStateOf(initial?.note ?: "") }

    val supersetSelection = remember { mutableStateListOf<Long>() }
    val exerciseList = remember { mutableStateListOf<Exercise>().apply { addAll(initial?.exercises ?: emptyList()) } }
    val supersets = remember { mutableStateListOf<Pair<Long, Long>>().apply { addAll(initial?.supersets ?: emptyList()) } }

    val vm: ExerciseViewModel = viewModel()
    val allExercises by vm.allExercises.observeAsState(emptyList())

    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        exerciseList.add(to.index, exerciseList.removeAt(from.index))
    })

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
                fontFamily = GaeguBold,
                color = Color.Black
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("What would you call this Line?", fontFamily = GaeguRegular, color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 24.sp, color = Color.Black)
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                placeholder = { Text("Category", fontFamily = GaeguRegular, color = Color.Gray) },
                textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp, color = Color.Black)
            )
            OutlinedTextField(
                value = muscleGroup,
                onValueChange = { muscleGroup = it },
                placeholder = { Text("Muscle Group", fontFamily = GaeguRegular, color = Color.Gray) },
                textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp, color = Color.Black)
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Anything else you'd like to remember?", fontFamily = GaeguRegular, color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp, color = Color.Black)
            )

            Text(
                "Exercises",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = GaeguBold,
                color = Color.Black
            )
            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .reorderable(reorderState)
                    .detectReorderAfterLongPress(reorderState)
            ) {
                itemsIndexed(exerciseList, key = { _, ex -> ex.id }) { index, exercise ->
                    ReorderableItem(reorderState, key = exercise.id) { _ ->
                        val bg = if (supersetSelection.contains(exercise.id)) Color(0xFFD9CEB2) else Color.Transparent
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bg, RoundedCornerShape(12.dp))
                                .padding(8.dp)
                                .clickable {
                                    if (supersetSelection.contains(exercise.id)) supersetSelection.remove(exercise.id)
                                    else if (supersetSelection.size < 2) supersetSelection.add(exercise.id)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(exercise.name, modifier = Modifier.weight(1f), fontFamily = GaeguRegular, color = Color.Black)
                            var setsText by remember(exercise.id) { mutableStateOf(exercise.sets.toString()) }
                            var repsText by remember(exercise.id) { mutableStateOf(exercise.repsOrDuration) }
                            BasicTextField(
                                value = setsText,
                                onValueChange = {
                                    setsText = it
                                    exerciseList[index] = exercise.copy(sets = it.toIntOrNull() ?: exercise.sets)
                                },
                                modifier = Modifier.width(40.dp),
                                textStyle = TextStyle(fontFamily = GaeguRegular, color = Color.Black),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Text(" × ", fontFamily = GaeguRegular, color = Color.Black)
                            BasicTextField(
                                value = repsText,
                                onValueChange = {
                                    repsText = it
                                    exerciseList[index] = exercise.copy(repsOrDuration = it)
                                },
                                modifier = Modifier.width(60.dp),
                                textStyle = TextStyle(fontFamily = GaeguRegular, color = Color.Black)
                            )
                            TextButton(onClick = { exerciseList.removeAt(index) }) {
                                Text("🗑", fontFamily = GaeguRegular, color = Color.Black)
                            }
                        }
                    }
                }
            }
            if (supersetSelection.size == 2) {
                Button(onClick = {
                    supersets.add(supersetSelection[0] to supersetSelection[1])
                    supersetSelection.clear()
                }) { Text("🔗 Create Superset", fontFamily = GaeguRegular, color = Color.Black) }
            }
            if (supersets.isNotEmpty()) {
                Text(
                    "Supersets",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = GaeguBold,
                    color = Color.Black
                )
                supersets.forEach { pair ->
                    val exA = exerciseList.find { it.id == pair.first }
                    val exB = exerciseList.find { it.id == pair.second }
                    if (exA != null && exB != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .combinedClickable(onLongClick = { supersets.remove(pair) }) {},
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5DC))
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Text("• ${exA.name}   [${exA.sets}] × [${exA.repsOrDuration}]", fontFamily = GaeguRegular, color = Color.Black)
                                Text("• ${exB.name}   [${exB.sets}] × [${exB.repsOrDuration}]", fontFamily = GaeguRegular, color = Color.Black)
                            }
                        }
                    }
                }
            }

            Text(
                "Add a movement",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = GaeguBold,
                color = Color.Black
            )
            LazyColumn {
                items(allExercises) { ex ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ex.name, modifier = Modifier.weight(1f), fontFamily = GaeguRegular, color = Color.Black)
                        TextButton(onClick = {
                            val defaultReps = if (ex.category == ExerciseCategory.Cardio) "30s" else "12"
                            exerciseList.add(
                                Exercise(
                                    id = System.currentTimeMillis(),
                                    name = ex.name,
                                    sets = 3,
                                    repsOrDuration = defaultReps
                                )
                            )
                        }) { Text("➕", fontFamily = GaeguRegular, color = Color.Black) }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Preview this line",
                style = MaterialTheme.typography.titleMedium,
                fontFamily = GaeguBold,
                color = Color.Black
            )
            LineCard(
                line = Line(
                    id = initial?.id ?: 0L,
                    title = title.ifBlank { "Untitled" },
                    category = category,
                    muscleGroup = muscleGroup,
                    exercises = exerciseList.toList(),
                    supersets = supersets.toList(),
                    note = note,
                    isArchived = false
                ),
                onEdit = {},
                onArchive = {},
                onRestore = {},
                onUse = {}
            )
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                TextButton(onClick = onCancel) { Text("Cancel", fontFamily = GaeguRegular, color = Color.Black) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val newLine = Line(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        category = category,
                        muscleGroup = muscleGroup,
                        exercises = exerciseList.toList(),
                        supersets = supersets.toList(),
                        note = note,
                        isArchived = false
                    )
                    onSave(newLine)
                }) {
                    Text("💾 Save this line", fontFamily = GaeguRegular, color = Color.Black)
                }
            }
        }
    }
}
