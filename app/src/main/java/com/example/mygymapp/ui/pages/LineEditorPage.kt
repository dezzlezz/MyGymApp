package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.snapshots.SnapshotStateList
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

data class ExerciseSection(
    var name: String,
    val exercises: SnapshotStateList<LineExercise> = mutableStateListOf()
)

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
    val sections = remember {
        mutableStateListOf<ExerciseSection>().apply {
            val initExercises = initial?.exercises ?: emptyList()
            add(
                ExerciseSection(
                    name = "Workout",
                    exercises = mutableStateListOf<LineExercise>().apply { addAll(initExercises) }
                )
            )
        }
    }
    val supersets = remember {
        mutableStateListOf<MutableList<Long>>().apply {
            initial?.supersets?.let { addAll(it.map { grp -> grp.toMutableList() }) }
        }
    }
    val supersetSelection = remember { mutableStateListOf<Long>() }
    val allSelectedExercises by remember { derivedStateOf { sections.flatMap { it.exercises } } }

    val snackbarHostState = remember { SnackbarHostState() }

    val categoryOptions =
        listOf("💪 Strength", "🔥 Cardio", "🌱 Warmup", "🧘 Flexibility", "🌀 Recovery")
    val muscleOptions = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Full Body")

    val selectedCategories = remember {
        mutableStateListOf<String>().apply { initial?.category?.split(",")?.let { addAll(it) } }
    }
    val selectedMuscles = remember {
        mutableStateListOf<String>().apply { initial?.muscleGroup?.split(",")?.let { addAll(it) } }
    }

    var showError by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<ExerciseSection?>(null) }
    var renameText by remember { mutableStateOf("") }

    /**
     * Replace any groups containing the supplied ids and store the new grouping.
     * A group must contain more than one exercise id to be persisted.
     */
    fun addSuperset(ids: List<Long>) {
        supersets.removeAll { group -> group.any { it in ids } }
        if (ids.size > 1) {
            supersets.add(ids.sorted().toMutableList())
        }
    }

    // Convenience overloads for callers using vararg or two-arg versions
    fun addSuperset(vararg ids: Long) = addSuperset(ids.toList())

    /** Remove any superset containing the given id(s). */
    fun removeSuperset(id: Long) {
        supersets.removeAll { group -> group.contains(id) }
    }

    fun removeSuperset(vararg ids: Long) {
        supersets.removeAll { group -> ids.any { it in group } }
    }

    fun findSupersetPartners(id: Long): List<Long> {
        return supersets.firstOrNull { it.contains(id) }?.filter { it != id } ?: emptyList()
    }

    // Backwards compatibility helper for previous single-partner usage
    fun findSupersetPartner(id: Long): Long? = findSupersetPartners(id).firstOrNull()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(supersetSelection.size) {
        if (supersetSelection.size > 1) {
            val res = snackbarHostState.showSnackbar(
                message = "Create superset",
                actionLabel = "Create"
            )
            if (res == SnackbarResult.ActionPerformed) {
                addSuperset(supersetSelection.toList())
                supersetSelection.clear()
            }
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFF5F5DC),
                    contentColor = Color.Black,
                    actionColor = Color.Black
                )
            }
        }
    ) { paddingValues ->
        PaperBackground(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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

    // Convenience overloads for callers using vararg or two-arg versions
    fun addSuperset(vararg ids: Long) = addSuperset(ids.toList())

    /** Remove any superset containing the given id(s). */
    fun removeSuperset(id: Long) {
        supersets.removeAll { group -> group.contains(id) }
    }

    fun removeSuperset(vararg ids: Long) {
        supersets.removeAll { group -> ids.any { it in group } }
    }

    fun findSupersetPartners(id: Long): List<Long> {
        return supersets.firstOrNull { it.contains(id) }?.filter { it != id } ?: emptyList()
    }

            Text("Which movements do you want to add?", fontFamily = GaeguRegular)
            val showExerciseSheet = remember { mutableStateOf(false) }
            val exerciseSearch = remember { mutableStateOf("") }
            var showSectionSheet by remember { mutableStateOf(false) }
            var showCustomSection by remember { mutableStateOf(false) }
            var customSectionName by remember { mutableStateOf("") }
            val filterMuscles = selectedMuscles.ifEmpty {
                allExercises.map { it.muscleGroup.display }.distinct()
            }
            val selectedFilter = remember { mutableStateOf<String?>(null) }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

            GaeguButton(
                text = "➕ Add Exercise",
                onClick = { showExerciseSheet.value = true },
                textColor = Color.Black
            )
            if (res == SnackbarResult.ActionPerformed) {
                addSuperset(supersetSelection.toList())
                supersetSelection.clear()
            }
        } else {
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFF5F5DC),
                    contentColor = Color.Black,
                    actionColor = Color.Black
                )
            }
        }
    ) { paddingValues ->
        PaperBackground(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
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
                    val matchesFilter =
                        selectedFilter.value == null || it.muscleGroup.display == selectedFilter.value
                    val matchesSearch = exerciseSearch.value.isBlank() ||
                            it.name.contains(exerciseSearch.value, ignoreCase = true)
                    matchesFilter && matchesSearch
                }

                GaeguButton(
                    text = "➕ Add Exercise",
                    onClick = { showExerciseSheet.value = true },
                    textColor = Color.Black
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
                                    color = Color.White,
                                    contentColor = Color.Black
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
                        items(filteredExercises) { ex ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        if (allSelectedExercises.none { it.id == ex.id }) {
                                            sections.last().exercises.add(
                                                LineExercise(
                                                    id = ex.id,
                                                    name = ex.name,
                                                    sets = 3,
                                                    repsOrDuration = "10"
                                                )
                                            )
                                        } else {
                                            supersetSelection.remove(item.id)
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White,
                                contentColor = Color.Black
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

            if (allSelectedExercises.isNotEmpty()) {
                Text("Today's selected movements:", fontFamily = GaeguBold)
                sections.forEachIndexed { sectionIndex, section ->
                    val baseIndex = sections.take(sectionIndex).sumOf { it.exercises.size }
                    val reorderState = rememberReorderableLazyListState(
                        onMove = { from, to -> section.exercises.move(from.index, to.index) }
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(section.name, fontFamily = GaeguBold, modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    renameTarget = section
                                    renameText = section.name
                                }) {
                                    Text("✏", fontSize = 14.sp, fontFamily = GaeguRegular)
                                }
                                IconButton(onClick = {
                                    if (sections.size > 1) {
                                        val removed = sections.removeAt(sectionIndex)
                                        val target = sections.getOrNull(sectionIndex - 1) ?: sections.getOrNull(sectionIndex)
                                        target?.exercises?.addAll(removed.exercises)
                                    }
                                }) {
                                    Text("🗑", fontSize = 14.sp, fontFamily = GaeguRegular)
                                }
                            }
                            LazyColumn(
                                state = reorderState.listState,
                                modifier = Modifier
                                    .reorderable(reorderState)
                                    .detectReorderAfterLongPress(reorderState),
                                userScrollEnabled = false
                            ) {
                                itemsIndexed(section.exercises, key = { _, item -> item.id }) { index, item ->
                                    ReorderableItem(reorderState, key = item.id) { isDragging ->
                                        val elevation = if (isDragging) 8.dp else 2.dp
                                        val partnerIndices = findSupersetPartners(item.id).mapNotNull { pid ->
                                            allSelectedExercises.indexOfFirst { it.id == pid }.takeIf { it >= 0 }
                                        }
                                        ReorderableExerciseItem(
                                            index = baseIndex + index,
                                            exercise = item,
                                            onRemove = {
                                                section.exercises.remove(item)
                                                removeSuperset(item.id)
                                                supersetSelection.remove(item.id)
                                            },
                                            isSupersetSelected = supersetSelection.contains(item.id),
                                            onSupersetSelectedChange = { checked ->
                                                if (checked) {
                                                    if (!supersetSelection.contains(item.id)) supersetSelection.add(item.id)
                                                } else {
                                                    supersetSelection.remove(item.id)
                                                }
                                            },
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
                    }
                }

            GaeguButton(
                text = "➕ Create Section",
                onClick = { showSectionSheet = true },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textColor = Color.Black
            )

            PoeticBottomSheet(
                visible = showSectionSheet,
                onDismiss = {
                    showSectionSheet = false
                    showCustomSection = false
                    customSectionName = ""
                }
            ) {
                val options = listOf("Warm-up", "Workout", "Cooldown")
                options.forEach { option ->
                    Text(
                        option,
                        fontFamily = GaeguRegular,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                sections.add(ExerciseSection(option))
                                showSectionSheet = false
                                showCustomSection = false
                                customSectionName = ""
                            }
                    )
                }
                Text(
                    "Custom",
                    fontFamily = GaeguRegular,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { showCustomSection = true }
                )
                if (showCustomSection) {
                    LinedTextField(
                        value = customSectionName,
                        onValueChange = { customSectionName = it },
                        hint = "Section name"
                    )
                    GaeguButton(
                        text = "Add",
                        onClick = {
                            val name = customSectionName.ifBlank { "Custom" }
                            sections.add(ExerciseSection(name))
                            showSectionSheet = false
                            showCustomSection = false
                            customSectionName = ""
                        },
                        modifier = Modifier.align(Alignment.End),
                        textColor = Color.Black
                    )
                }
            }

            if (renameTarget != null) {
                AlertDialog(
                    onDismissRequest = { renameTarget = null },
                    confirmButton = {
                        TextButton(onClick = {
                            renameTarget?.name = renameText
                            renameTarget = null
                        }) { Text("OK", fontFamily = GaeguRegular, color = Color.Black) }
                    },
                    dismissButton = {
                        TextButton(onClick = { renameTarget = null }) {
                            Text("Cancel", fontFamily = GaeguRegular, color = Color.Black)
                        }
                    },
                    title = { Text("Rename section", fontFamily = GaeguBold, color = Color.Black) },
                    text = {
                        LinedTextField(value = renameText, onValueChange = { renameText = it }, hint = "Section name")
                    }
                )
            }


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) {
                    Text("Cancel", fontFamily = GaeguRegular, color = Color.Black)
                }
                Spacer(Modifier.width(16.dp))
                GaeguButton(
                    text = "Create",
                    onClick = {
                        if (title.isBlank() || allSelectedExercises.isEmpty()) {
                            showError = true
                            return@GaeguButton
                        }
                        val newLine = Line(
                            id = initial?.id ?: System.currentTimeMillis(),
                            title = title,
                            category = selectedCategories.joinToString(),
                            muscleGroup = selectedMuscles.joinToString(),
                            mood = null,
                            exercises = allSelectedExercises,
                            supersets = supersets.map { it.toList() },
                            note = note,
                            isArchived = false
                        )
                        onSave(newLine)
                    },
                    textColor = Color.Black
                )
            }

            if (showError) {
                Text("Please fill out title and at least one exercise", color = Color.Red, fontFamily = GaeguRegular)
            }
        }
    }
}
