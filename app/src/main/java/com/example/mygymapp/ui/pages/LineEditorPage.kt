package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.components.GaeguButton
import com.example.mygymapp.ui.components.LinedTextField
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticBottomSheet
import com.example.mygymapp.ui.components.PoeticMultiSelectChips
import com.example.mygymapp.ui.components.PoeticRadioChips
import com.example.mygymapp.ui.components.ReorderableExerciseItem
import com.example.mygymapp.ui.util.move
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import com.example.mygymapp.viewmodel.ExerciseViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    val supersets = remember {
        mutableStateListOf<MutableList<Long>>().apply {
            initial?.supersets?.let { addAll(it.map { grp -> grp.toMutableList() }) }
        }
    }
    var selectedForSuperset by remember { mutableStateOf<LineExercise?>(null) }

    val categoryOptions = listOf("💪 Strength", "🔥 Cardio", "🌱 Warmup", "🧘 Flexibility", "🌀 Recovery")
    val muscleOptions = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Full Body")

    val selectedCategories = remember {
        mutableStateListOf<String>().apply { initial?.category?.split(",")?.let { addAll(it) } }
    }
    val selectedMuscles = remember {
        mutableStateListOf<String>().apply { initial?.muscleGroup?.split(",")?.let { addAll(it) } }
    }

    var showError by remember { mutableStateOf(false) }

    fun addSuperset(ids: List<Long>) {
        supersets.removeAll { group -> group.any { it in ids } }
        if (ids.size > 1) {
            supersets.add(ids.sorted().toMutableList())
        }
    }

    fun removeSuperset(id: Long) {
        supersets.removeAll { group -> group.contains(id) }
    }

    fun findSupersetPartners(id: Long): List<Long> {
        return supersets.firstOrNull { it.contains(id) }?.filter { it != id } ?: emptyList()
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    PaperBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "✒ Compose your daily line",
                fontFamily = GaeguBold,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text("What would you title this day?", fontFamily = GaeguRegular)
            LinedTextField(
                value = title,
                onValueChange = { title = it },
                hint = "A poetic title...",
                initialLines = 1
            )

            Text("What kind of movement is this?", fontFamily = GaeguRegular)
            PoeticMultiSelectChips(
                options = categoryOptions,
                selectedItems = selectedCategories,
                onSelectionChange = {
                    selectedCategories.clear()
                    selectedCategories.addAll(it)
                }
            )

            Text("Which areas are involved?", fontFamily = GaeguRegular)
            PoeticMultiSelectChips(
                options = muscleOptions,
                selectedItems = selectedMuscles,
                onSelectionChange = {
                    selectedMuscles.clear()
                    selectedMuscles.addAll(it)
                }
            )

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
            val filterMuscles = selectedMuscles.ifEmpty {
                allExercises.map { it.muscleGroup.display }.distinct()
            }
            val selectedFilter = remember { mutableStateOf<String?>(null) }

            val filteredExercises = allExercises.filter {
                val matchesFilter = selectedFilter.value == null || it.muscleGroup.display == selectedFilter.value
                val matchesSearch = exerciseSearch.value.isBlank() ||
                    it.name.contains(exerciseSearch.value, ignoreCase = true)
                matchesFilter && matchesSearch
            }

            GaeguButton(
                text = "➕ Add Exercise",
                onClick = { showExerciseSheet.value = true }
            )

            PoeticBottomSheet(
                visible = showExerciseSheet.value,
                onDismiss = { showExerciseSheet.value = false }
            ) {
                LinedTextField(
                    value = exerciseSearch.value,
                    onValueChange = { exerciseSearch.value = it },
                    hint = "Search exercises",
                    modifier = Modifier.fillMaxWidth(),
                    initialLines = 1
                )
                Spacer(Modifier.height(12.dp))
                PoeticRadioChips(
                    options = listOf("All") + filterMuscles,
                    selected = selectedFilter.value ?: "All",
                    onSelected = { selectedFilter.value = if (it == "All") null else it }
                )
                Spacer(Modifier.height(12.dp))
                if (filteredExercises.isEmpty()) {
                    Text(
                        "No matching exercises found.",
                        fontFamily = GaeguLight,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 320.dp)
                            .fillMaxWidth()
                    ) {
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
                                    },
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

            if (selectedExercises.isNotEmpty()) {
                Text("Today's selected movements:", fontFamily = GaeguBold)
                val reorderState = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        selectedExercises.move(from.index, to.index)
                    }
                )
                LazyColumn(
                    state = reorderState.listState,
                    modifier = Modifier
                        .heightIn(max = screenHeight)
                        .reorderable(reorderState)
                        .detectReorderAfterLongPress(reorderState)
                        .fillMaxWidth(),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(selectedExercises, key = { _, item -> item.id }) { index, item ->
                        ReorderableItem(reorderState, key = item.id) { isDragging ->
                            val elevation = if (isDragging) 8.dp else 2.dp
                            val partnerIndices = findSupersetPartners(item.id).mapNotNull { pid ->
                                selectedExercises.indexOfFirst { it.id == pid }.takeIf { it >= 0 }
                            }
                            ReorderableExerciseItem(
                                index = index,
                                exercise = item,
                                onRemove = {
                                    selectedExercises.remove(item)
                                    removeSuperset(item.id)
                                },
                                onSupersetClick = { selectedForSuperset = item },
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .animateItemPlacement()
                                    .shadow(elevation),
                                dragHandle = {
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Drag",
                                        tint = Color.Gray,
                                        modifier = Modifier.detectReorderAfterLongPress(reorderState)
                                    )
                                },
                                supersetPartnerIndices = partnerIndices
                            )
                        }
                    }
                }
            }

            PoeticBottomSheet(
                visible = selectedForSuperset != null,
                onDismiss = { selectedForSuperset = null }
            ) {
                val current = selectedForSuperset
                if (current != null) {
                    Text("Choose superset partners", fontFamily = GaeguBold)
                    Spacer(Modifier.height(8.dp))
                    val options = selectedExercises.filter { it.id != current.id }
                    val selections = remember(current) {
                        mutableStateListOf<Long>().apply { addAll(findSupersetPartners(current.id)) }
                    }
                    if (options.isEmpty()) {
                        Text(
                            "No other exercises available.",
                            fontFamily = GaeguLight,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(12.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 320.dp)
                                .fillMaxWidth()
                        ) {
                            items(options) { ex ->
                                val checked = selections.contains(ex.id)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp, horizontal = 8.dp)
                                        .clickable {
                                            if (checked) selections.remove(ex.id) else selections.add(ex.id)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = checked,
                                        onCheckedChange = {
                                            if (it) selections.add(ex.id) else selections.remove(ex.id)
                                        }
                                    )
                                    Text(ex.name, fontFamily = GaeguRegular, fontSize = 16.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                addSuperset(listOf(current.id))
                                selectedForSuperset = null
                            }) {
                                Text("Clear", fontFamily = GaeguRegular)
                            }
                            Spacer(Modifier.width(8.dp))
                            GaeguButton(
                                text = "Save",
                                onClick = {
                                    addSuperset(listOf(current.id) + selections)
                                    selectedForSuperset = null
                                }
                            )
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) {
                    Text("Cancel", fontFamily = GaeguRegular)
                }
                Spacer(Modifier.width(16.dp))
                GaeguButton(
                    text = "Create",
                    onClick = {
                        if (title.isBlank() || selectedExercises.isEmpty()) {
                            showError = true
                            return@GaeguButton
                        }
                        val newLine = Line(
                            id = initial?.id ?: System.currentTimeMillis(),
                            title = title,
                            category = selectedCategories.joinToString(),
                            muscleGroup = selectedMuscles.joinToString(),
                            mood = null,
                            exercises = selectedExercises.toList(),
                            supersets = supersets.map { it.toList() },
                            note = note,
                            isArchived = false
                        )
                        onSave(newLine)
                    }
                )
            }

            if (showError) {
                Text("Please fill out title and at least one exercise", color = Color.Red, fontFamily = GaeguRegular)
            }
        }
    }
}
