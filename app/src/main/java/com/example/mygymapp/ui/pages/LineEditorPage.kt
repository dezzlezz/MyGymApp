// ✒️ Poetic Line Editor – Updated with Superset, Validation, Gaegu Fonts and Improved UI
package com.example.mygymapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.components.ExerciseItem
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticCard
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.verticalScroll
import com.example.mygymapp.ui.components.LinedTextField



@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LineEditorPage(
    initial: Line? = null,
    onSave: (Line) -> Unit,
    onCancel: () -> Unit
) {
    val vm: ExerciseViewModel = viewModel()
    val allExercises by vm.allExercises.observeAsState(emptyList())

    var title by remember { mutableStateOf(initial?.title ?: "") }
    var note by remember { mutableStateOf(initial?.note ?: "") }
    val selectedExercises = remember {
        mutableStateListOf<LineExercise>().apply { initial?.exercises?.let { addAll(it) } }
    }
    val supersets = remember { mutableStateListOf<Pair<Long, Long>>() }
    val selectedForSuperset = remember { mutableStateListOf<Long>() }

    val categoryOptions = listOf("💪 Strength", "🔥 Cardio", "🌱 Warmup", "🧘 Flexibility", "🌀 Recovery")
    val muscleOptions = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Full Body")

    val selectedCategories = remember { mutableStateListOf<String>().apply { initial?.category?.split(",")?.let { addAll(it) } } }
    val selectedMuscles = remember { mutableStateListOf<String>().apply { initial?.muscleGroup?.split(",")?.let { addAll(it) } } }

    var search by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    PaperBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        )
        {
            Text("✒ Compose your daily line", fontFamily = GaeguBold, fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally))

            Text("What would you title this day?", fontFamily = GaeguRegular)
            LinedTextField(
                value = title,
                onValueChange = { title = it },
                hint = "A poetic title...",
                initialLines = 1
            )


            Text("What kind of movement is this?", fontFamily = GaeguRegular)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categoryOptions.forEach { option ->
                    val selected = option in selectedCategories
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (selected) selectedCategories.remove(option) else selectedCategories.add(option)
                            }
                            .background(if (selected) Color(0xFFD7CCC8) else Color.Transparent)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.Transparent
                    ) {
                        Text(option, fontFamily = GaeguRegular, fontSize = 16.sp)
                    }
                }
            }

            Text("Which areas are involved?", fontFamily = GaeguRegular)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                muscleOptions.forEach { option ->
                    val selected = option in selectedMuscles
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (selected) selectedMuscles.remove(option) else selectedMuscles.add(option)
                            }
                            .background(if (selected) Color(0xFFD7CCC8) else Color.Transparent)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.Transparent
                    ) {
                        Text(option, fontFamily = GaeguRegular, fontSize = 16.sp)
                    }
                }
            }

            Text("Your notes on this movement", fontFamily = GaeguRegular)
            LinedTextField(
                value = note,
                onValueChange = { note = it },
                hint = "Write your thoughts here...",
                initialLines = 3
            )


            Text("Which movements do you want to add?", fontFamily = GaeguRegular)
            val showExerciseSheet = remember { mutableStateOf(false) }
            val exerciseSearch = remember { mutableStateOf("") }
            val filterMuscles = selectedMuscles.toList()
            val selectedFilter = remember { mutableStateOf<String?>(null) }

            val filteredExercises = allExercises.filter {
                val matchesFilter = selectedFilter.value == null || it.muscleGroup.display == selectedFilter.value
                val matchesSearch = exerciseSearch.value.isBlank() ||
                        it.name.contains(exerciseSearch.value, ignoreCase = true)
                matchesFilter && matchesSearch
            }

            Button(
                onClick = { showExerciseSheet.value = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F4E3A))
            ) {
                Text("➕ Add Exercise", fontFamily = GaeguBold, color = Color.White)
            }

            if (showExerciseSheet.value) {
                val sheetState = rememberModalBottomSheetState()
                ModalBottomSheet(
                    onDismissRequest = { showExerciseSheet.value = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFFF5F5DC)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = exerciseSearch.value,
                            onValueChange = { exerciseSearch.value = it },
                            label = { Text("Search", fontFamily = GaeguRegular) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "All",
                                fontFamily = GaeguRegular,
                                color = if (selectedFilter.value == null) Color.White else Color.Black,
                                modifier = Modifier
                                    .background(
                                        if (selectedFilter.value == null) Color(0xFF3F4E3A) else Color.LightGray,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedFilter.value = null }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                            filterMuscles.forEach { muscle ->
                                Text(
                                    muscle,
                                    fontFamily = GaeguRegular,
                                    color = if (selectedFilter.value == muscle) Color.White else Color.Black,
                                    modifier = Modifier
                                        .background(
                                            if (selectedFilter.value == muscle) Color(0xFF3F4E3A) else Color.LightGray,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedFilter.value = muscle }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                            items(filteredExercises) { ex ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (selectedExercises.none { it.id == ex.id }) {
                                                selectedExercises.add(
                                                    LineExercise(
                                                        id = ex.id,
                                                        name = ex.name,
                                                        sets = 3,
                                                        repsOrDuration = "10"
                                                    )
                                                )
                                            }
                                            showExerciseSheet.value = false
                                            exerciseSearch.value = ""
                                            selectedFilter.value = null
                                        }
                                    ,
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(ex.name, fontFamily = GaeguRegular, fontSize = 16.sp)
                                        Text(
                                            "${ex.muscleGroup.display} · ${ex.category.display}",
                                            fontFamily = GaeguLight,
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (selectedExercises.isNotEmpty()) {
                Text("Today's selected movements:", fontFamily = GaeguBold)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedExercises.forEach { ex ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFEEE8D5),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ex.name, fontFamily = GaeguRegular)
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
                                    tint = Color.Red,
                                    modifier = Modifier.clickable {
                                        selectedExercises.remove(ex)
                                    }
                                )
                            }
                        }
                    }
                }
            }



            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) {
                    Text("Cancel", fontFamily = GaeguRegular)
                }
                Spacer(Modifier.width(16.dp))
                Button(onClick = {
                    if (title.isBlank() || selectedExercises.isEmpty()) {
                        showError = true
                        return@Button
                    }
                    val newLine = Line(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        category = selectedCategories.joinToString(),
                        muscleGroup = selectedMuscles.joinToString(),
                        mood = null,
                        exercises = selectedExercises.toList(),
                        supersets = supersets.toList(),
                        note = note,
                        isArchived = false
                    )
                    onSave(newLine)
                }) {
                    Text("Create", fontFamily = GaeguBold)
                }
            }

            if (showError) {
                Text("Please fill out title and at least one exercise", color = Color.Red, fontFamily = GaeguRegular)
            }
        }
    }
}
