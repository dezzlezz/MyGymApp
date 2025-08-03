package com.example.mygymapp.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.model.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.viewmodel.ExerciseViewModel
import kotlinx.coroutines.delay

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

    var showSavedOverlay by remember { mutableStateOf(false) }

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "✒ Compose your daily line",
                    fontFamily = GaeguBold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                InkTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "What would you call this line?",
                    textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 24.sp)
                )
                InkTextField(
                    value = category,
                    onValueChange = { category = it },
                    placeholder = "Category",
                    textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp)
                )
                InkTextField(
                    value = muscleGroup,
                    onValueChange = { muscleGroup = it },
                    placeholder = "Muscle Group",
                    textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp)
                )

                val notePlaceholder = "Anything else you'd like to remember?"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .drawBehind {
                            val lineSpacing = 28.dp.toPx()
                            val lines = (size.height / lineSpacing).toInt()
                            repeat(lines) { i ->
                                val y = (i + 1) * lineSpacing
                                drawLine(
                                    color = Color.Black.copy(alpha = 0.15f),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1f
                                )
                            }
                        }
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    BasicTextField(
                        value = note,
                        onValueChange = { note = it },
                        textStyle = TextStyle(
                            fontFamily = GaeguRegular,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            color = Color.Black
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { inner ->
                            if (note.isBlank()) {
                                Text(
                                    notePlaceholder,
                                    fontFamily = GaeguRegular,
                                    fontSize = 18.sp,
                                    color = Color.Black.copy(alpha = 0.4f)
                                )
                            }
                            inner()
                        }
                    )
                }

                Text(
                    "Exercises",
                    fontFamily = GaeguBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(exerciseList, key = { _, ex -> ex.id }) { index, exercise ->
                        val isSelected = supersetSelection.contains(exercise.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) Color(0xFFD9CEB2) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (isSelected) supersetSelection.remove(exercise.id)
                                    else if (supersetSelection.size < 2) supersetSelection.add(exercise.id)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "• ${exercise.name}",
                                modifier = Modifier.weight(1f),
                                fontFamily = GaeguRegular,
                                color = Color.Black
                            )
                            var setsText by remember(exercise.id) { mutableStateOf(exercise.sets.toString()) }
                            var repsText by remember(exercise.id) { mutableStateOf(exercise.repsOrDuration) }
                            InkSmallField(
                                value = setsText,
                                onValueChange = {
                                    setsText = it
                                    exerciseList[index] = exercise.copy(sets = it.toIntOrNull() ?: exercise.sets)
                                },
                                modifier = Modifier.width(40.dp),
                                keyboardType = KeyboardType.Number
                            )
                            Text("×", fontFamily = GaeguRegular, color = Color.Black)
                            InkSmallField(
                                value = repsText,
                                onValueChange = {
                                    repsText = it
                                    exerciseList[index] = exercise.copy(repsOrDuration = it)
                                },
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                "✖",
                                fontFamily = GaeguRegular,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable { exerciseList.removeAt(index) }
                            )
                        }
                    }
                }

                if (supersetSelection.size == 2) {
                    Text(
                        "🔗 Create Superset",
                        fontFamily = GaeguRegular,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                supersets.add(supersetSelection[0] to supersetSelection[1])
                                supersetSelection.clear()
                            }
                    )
                }

                if (supersets.isNotEmpty()) {
                    Text(
                        "Supersets",
                        fontFamily = GaeguBold,
                        fontSize = 18.sp,
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
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
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
                    fontFamily = GaeguBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(allExercises) { ex ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                ex.name,
                                modifier = Modifier.weight(1f),
                                fontFamily = GaeguRegular,
                                color = Color.Black
                            )
                            Text(
                                "➕",
                                fontFamily = GaeguRegular,
                                color = Color.Black,
                                modifier = Modifier.clickable {
                                    val defaultReps = if (ex.category == ExerciseCategory.Cardio) "30s" else "12"
                                    exerciseList.add(
                                        Exercise(
                                            id = System.currentTimeMillis(),
                                            name = ex.name,
                                            sets = 3,
                                            repsOrDuration = defaultReps
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                Text(
                    "Preview this line",
                    fontFamily = GaeguBold,
                    fontSize = 18.sp,
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
                    onUse = {},
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        "Cancel",
                        fontFamily = GaeguRegular,
                        color = Color.Gray,
                        modifier = Modifier
                            .clickable { onCancel() }
                            .padding(8.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "💾 Save this line",
                        fontFamily = GaeguBold,
                        color = Color.Black,
                        modifier = Modifier
                            .clickable {
                                showSavedOverlay = true
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
                            }
                            .padding(8.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = showSavedOverlay,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A new line has been written...",
                        color = Color.White,
                        style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp)
                    )
                }
            }

            LaunchedEffect(showSavedOverlay) {
                if (showSavedOverlay) {
                    delay(1000)
                    showSavedOverlay = false
                }
            }
        }
    }
}

@Composable
private fun InkTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp)
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle.copy(color = Color.Black),
        cursorBrush = SolidColor(Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .drawBehind {
                val strokeWidth = 2f
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Color(0xFF1B1B1B),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            },
        decorationBox = { inner ->
            if (value.isBlank()) {
                Text(placeholder, style = textStyle.copy(color = Color.Gray))
            }
            inner()
        }
    )
}

@Composable
private fun InkSmallField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.Black),
        cursorBrush = SolidColor(Color.Black),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .padding(horizontal = 2.dp)
            .drawBehind {
                val strokeWidth = 2f
                val y = size.height - strokeWidth / 2
                drawLine(
                    color = Color(0xFF1B1B1B),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = strokeWidth
                )
            }
    )
}

