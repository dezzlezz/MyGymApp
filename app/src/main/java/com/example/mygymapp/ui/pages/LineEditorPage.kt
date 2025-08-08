package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.toMutableStateList
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.components.GaeguButton
import com.example.mygymapp.ui.components.LinedTextField
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticBottomSheet
import com.example.mygymapp.ui.components.PoeticCard
import com.example.mygymapp.ui.components.PoeticDivider
import com.example.mygymapp.ui.components.PoeticMultiSelectChips
import com.example.mygymapp.ui.components.PoeticRadioChips
import com.example.mygymapp.ui.components.ReorderableExerciseItem
import com.example.mygymapp.ui.components.SectionWrapper
import com.example.mygymapp.ui.components.WaxSealButton
import com.example.mygymapp.ui.util.move
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LineEditorPage(
    navController: NavController,
    initial: Line? = null,
    onSave: (Line) -> Unit,
    onCancel: () -> Unit
) {
    val vm: ExerciseViewModel = viewModel()
    val allExercises by vm.allExercises.observeAsState(emptyList())

    var title by rememberSaveable { mutableStateOf(initial?.title ?: "") }
    var note by rememberSaveable { mutableStateOf(initial?.note ?: "") }
    val selectedExercises = rememberSaveable(
        saver = listSaver<SnapshotStateList<LineExercise>, LineExercise>(
            save = { stateList -> ArrayList(stateList) },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf<LineExercise>().apply { initial?.exercises?.let { addAll(it) } }
    }
    val sections = rememberSaveable(
        saver = listSaver<SnapshotStateList<String>, String>(
            save = { ArrayList(it) },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf<String>().apply {
            initial?.exercises?.map { it.section }?.filter { it.isNotBlank() }?.distinct()
                ?.let { addAll(it) }
        }
    }
    val supersets = rememberSaveable(
        saver = listSaver<SnapshotStateList<MutableList<Long>>, ArrayList<Long>>(
            save = { list -> list.map { ArrayList(it) } },
            restore = { restored -> restored.map { it.toMutableList() }.toMutableStateList() }
        )
    ) {
        mutableStateListOf<MutableList<Long>>().apply {
            initial?.supersets?.let { addAll(it.map { grp -> grp.toMutableList() }) }
        }
    }
    val supersetSelection = rememberSaveable(
        saver = listSaver<SnapshotStateList<Long>, Long>(
            save = { ArrayList(it) },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<Long>() }

    val snackbarHostState = remember { SnackbarHostState() }

    val categoryOptions =
        listOf("💪 Strength", "🔥 Cardio", "🌱 Warmup", "🧘 Flexibility", "🌈 Recovery")
    val muscleOptions = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Arms", "Full Body")

    val selectedCategories = rememberSaveable(
        saver = listSaver<SnapshotStateList<String>, String>(
            save = { ArrayList(it) },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf<String>().apply { initial?.category?.split(",")?.let { addAll(it) } }
    }
    val selectedMuscles = rememberSaveable(
        saver = listSaver<SnapshotStateList<String>, String>(
            save = { ArrayList(it) },
            restore = { it.toMutableStateList() }
        )
    ) {
        mutableStateListOf<String>().apply { initial?.muscleGroup?.split(",")?.let { addAll(it) } }
    }

    var showError by remember { mutableStateOf(false) }

    // --- DnD Preview/State (unverändert, aber konsistente Window-Koordinaten) ---
    var draggingSection by remember { mutableStateOf<String?>(null) }
    var dragPreview by remember { mutableStateOf<String?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var draggingExerciseId by remember { mutableStateOf<Long?>(null) }
    val itemBounds = remember { mutableStateMapOf<Long, Pair<Float, Float>>() }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartPointer by remember { mutableStateOf(Offset.Zero) }
    val sectionBounds = remember { mutableStateMapOf<String, Pair<Float, Float>>() }
    var hoveredSection by remember { mutableStateOf<String?>(null) }

    fun addSuperset(ids: List<Long>) {
        supersets.removeAll { group -> group.any { it in ids } }
        if (ids.size > 1) supersets.add(ids.sorted().toMutableList())
    }
    fun addSuperset(vararg ids: Long) = addSuperset(ids.toList())
    fun removeSuperset(id: Long) { supersets.removeAll { group -> group.contains(id) } }
    fun removeSuperset(vararg ids: Long) { supersets.removeAll { group -> ids.any { it in group } } }
    fun findSupersetPartners(id: Long): List<Long> =
        supersets.firstOrNull { it.contains(id) }?.filter { it != id } ?: emptyList()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(supersetSelection.size) {
        if (supersetSelection.size > 1) {
            val res = snackbarHostState.showSnackbar("Create superset", actionLabel = "Create")
            if (res == SnackbarResult.ActionPerformed) {
                addSuperset(supersetSelection.toList())
                supersetSelection.clear()
            }
        } else snackbarHostState.currentSnackbarData?.dismiss()
    }

    fun findInsertIndexForDrop(sectionName: String, dropY: Float): Int {
        val entries = selectedExercises.withIndex().filter { it.value.section == sectionName }
        if (entries.isEmpty()) {
            val last = selectedExercises.indexOfLast { it.section == sectionName }
            return if (last >= 0) last + 1 else 0
        }
        val closest = entries.minByOrNull { (_, ex) ->
            val bounds = itemBounds[ex.id]
            val center = bounds?.let { (it.first + it.second) / 2f } ?: dropY
            kotlin.math.abs(dropY - center)
        } ?: return entries.last().index + 1
        val bounds = itemBounds[closest.value.id]
        val center = bounds?.let { (it.first + it.second) / 2f } ?: dropY
        return if (dropY >= center) closest.index + 1 else closest.index
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
            Box(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .systemBarsPadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "✔ Compose your daily line",
                        fontFamily = GaeguBold, fontSize = 24.sp, color = Color.Black
                    )

                    PoeticDivider(centerText = "What would you title this day?")
                    LinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        hint = "A poetic title...",
                        initialLines = 1,
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                    )

                    PoeticDivider(centerText = "What kind of movement is this?")
                    PoeticMultiSelectChips(
                        options = categoryOptions,
                        selectedItems = selectedCategories,
                        onSelectionChange = {
                            selectedCategories.clear(); selectedCategories.addAll(it)
                        },
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                    )

                    PoeticDivider(centerText = "Which areas are involved?")
                    PoeticMultiSelectChips(
                        options = muscleOptions,
                        selectedItems = selectedMuscles,
                        onSelectionChange = {
                            selectedMuscles.clear(); selectedMuscles.addAll(it)
                        },
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                    )

                    PoeticDivider(centerText = "Your notes on this movement")
                    LinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        hint = "Write your thoughts here...",
                        initialLines = 3,
                        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                    )

                    PoeticDivider(centerText = "Which movements do you want to add?")
                    val showExerciseSheet = remember { mutableStateOf(false) }
                    val showSectionSheet = remember { mutableStateOf(false) }
                    val exerciseSearch = remember { mutableStateOf("") }
                    val filterOptions by remember {
                        derivedStateOf {
                            val base = listOf("All", "Full Body")
                            if (selectedMuscles.isEmpty()) base else (base + selectedMuscles).distinct()
                        }
                    }
                    val selectedFilter = remember { mutableStateOf<String?>(null) }
                    LaunchedEffect(filterOptions) {
                        if (selectedFilter.value !in filterOptions) selectedFilter.value = null
                    }

                    val allExercisesState = allExercises
                    val filteredExercises by remember(
                        exerciseSearch.value, selectedFilter.value, allExercisesState
                    ) {
                        derivedStateOf {
                            val query = exerciseSearch.value.trim().lowercase()
                            allExercisesState.filter { ex ->
                                val matchesFilter =
                                    selectedFilter.value == null || ex.muscleGroup.display == selectedFilter.value
                                val matchesSearch = query.isEmpty() || ex.name.lowercase().contains(query)
                                matchesFilter && matchesSearch
                            }
                        }
                    }

                    GaeguButton(
                        text = "➕ Add Exercise",
                        onClick = { showExerciseSheet.value = true },
                        textColor = Color.Black
                    )

                    // --- Exercise Picker Sheet (Drag-Quelle am Card-Body lassen, aber Window-Koords nutzen) ---
                    PoeticBottomSheet(
                        visible = showExerciseSheet.value,
                        onDismiss = { showExerciseSheet.value = false }
                    ) {
                        LinedTextField(
                            value = exerciseSearch.value,
                            onValueChange = { exerciseSearch.value = it },
                            hint = "Search exercises",
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                            initialLines = 1
                        )
                        Spacer(Modifier.height(12.dp))
                        PoeticRadioChips(
                            options = filterOptions,
                            selected = selectedFilter.value ?: "All",
                            onSelected = { selectedFilter.value = if (it == "All") null else it },
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        )
                        Spacer(Modifier.height(12.dp))
                        if (filteredExercises.isEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "No matching exercises found.",
                                    fontFamily = GaeguLight, fontSize = 14.sp, color = Color.Black,
                                    modifier = Modifier.padding(12.dp)
                                )
                                GaeguButton(
                                    text = "Create \"${exerciseSearch.value.trim()}\"",
                                    onClick = {
                                        val encoded = Uri.encode(exerciseSearch.value.trim())
                                        navController.navigate("movement_editor?name=$encoded")
                                    },
                                    textColor = Color.Black
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 320.dp).fillMaxWidth()
                            ) {
                                items(filteredExercises, key = { it.id }) { ex ->
                                    var cardOffset by remember { mutableStateOf(Offset.Zero) }
                                    PoeticCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .onGloballyPositioned { cardOffset = it.positionInWindow() }
                                            .alpha(if (draggingExerciseId == ex.id) 0f else 1f)
                                            .pointerInput(Unit) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = { offset ->
                                                        isDragging = true
                                                        dragPreview = ex.name
                                                        draggingExerciseId = ex.id
                                                        draggingSection = ""
                                                        dragStartPointer = cardOffset + offset
                                                        dragPosition = dragStartPointer
                                                        showExerciseSheet.value = false
                                                    },
                                                    onDrag = { change, _ ->
                                                        change.consume()
                                                        dragPosition = cardOffset + change.position
                                                        hoveredSection = sectionBounds.entries.find { entry ->
                                                            dragPosition.y in entry.value.first..entry.value.second
                                                        }?.key
                                                    },
                                                    onDragEnd = {
                                                        hoveredSection?.let { sectionName ->
                                                            val insertIdx = findInsertIndexForDrop(sectionName, dragPosition.y)
                                                            val idx = selectedExercises.indexOfFirst { it.id == ex.id }
                                                            var clampedIdx = insertIdx.coerceIn(0, selectedExercises.size)
                                                            if (idx >= 0 && selectedExercises[idx].section == sectionName && idx < clampedIdx) {
                                                                clampedIdx -= 1
                                                            }
                                                            if (idx >= 0) {
                                                                val item = selectedExercises.removeAt(idx)
                                                                val oldSection = item.section
                                                                selectedExercises.add(clampedIdx, item.copy(section = sectionName))
                                                                if (oldSection.isNotBlank() && oldSection != sectionName &&
                                                                    selectedExercises.none { it.section == oldSection }) {
                                                                    sections.remove(oldSection)
                                                                }
                                                            } else {
                                                                allExercises.firstOrNull { it.id == ex.id }?.let { exx ->
                                                                    selectedExercises.add(
                                                                        clampedIdx,
                                                                        LineExercise(id = exx.id, name = exx.name, sets = 3, repsOrDuration = "10", section = sectionName)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        isDragging = false
                                                        draggingExerciseId = null
                                                        dragPreview = null
                                                        draggingSection = null
                                                        hoveredSection = null
                                                    },
                                                    onDragCancel = {
                                                        isDragging = false
                                                        draggingExerciseId = null
                                                        dragPreview = null
                                                        draggingSection = null
                                                        hoveredSection = null
                                                    }
                                                )
                                            }
                                            .clickable {
                                                if (selectedExercises.none { it.id == ex.id }) {
                                                    selectedExercises.add(
                                                        LineExercise(
                                                            id = ex.id, name = ex.name, sets = 3, repsOrDuration = "10"
                                                        )
                                                    )
                                                }
                                                showExerciseSheet.value = false
                                                exerciseSearch.value = ""
                                                selectedFilter.value = null
                                            }
                                    ) {
                                        Text(ex.name, fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.Black)
                                        Text(
                                            "${ex.muscleGroup.display} · ${ex.category.display}",
                                            fontFamily = GaeguLight, fontSize = 13.sp, color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (selectedExercises.isNotEmpty()) {
                        if (sections.isEmpty()) {
                            Text("Today's selected movements:", fontFamily = GaeguBold, color = Color.Black)
                            val reorderState = rememberReorderableLazyListState(
                                onMove = { from, to -> selectedExercises.move(from.index, to.index) }
                            )
                            LazyColumn(
                                state = reorderState.listState,
                                modifier = Modifier
                                    .heightIn(max = screenHeight)
                                    .graphicsLayer { clip = false }
                                    .reorderable(reorderState)
                                    .detectReorderAfterLongPress(reorderState)
                                    .fillMaxWidth(),
                                userScrollEnabled = false
                            ) {
                                itemsIndexed(selectedExercises, key = { _, item -> item.id }) { index, item ->
                                    ReorderableItem(reorderState, key = item.id) { itemDragging ->
                                        val elevation = if (itemDragging) 8.dp else 2.dp
                                        val partnerIndices = findSupersetPartners(item.id).mapNotNull { pid ->
                                            selectedExercises.indexOfFirst { it.id == pid }.takeIf { it >= 0 }
                                        }
                                        var itemOffset by remember { mutableStateOf(Offset.Zero) }
                                        ReorderableExerciseItem(
                                            index = index,
                                            exercise = item,
                                            onRemove = {
                                                selectedExercises.remove(item)
                                                removeSuperset(item.id)
                                                supersetSelection.remove(item.id)
                                            },
                                            isSupersetSelected = supersetSelection.contains(item.id),
                                            onSupersetSelectedChange = { checked ->
                                                if (checked) {
                                                    if (!supersetSelection.contains(item.id)) supersetSelection.add(item.id)
                                                } else supersetSelection.remove(item.id)
                                            },
                                            modifier = Modifier
                                                .alpha(if (draggingExerciseId == item.id) 0f else 1f)
                                                .zIndex(if (isDragging) 1000f else 0f)
                                                .animateItemPlacement()
                                                .onGloballyPositioned {
                                                    val topLeft = it.positionInWindow()
                                                    itemOffset = topLeft
                                                    val size = it.size.toSize()
                                                    itemBounds[item.id] = topLeft.y to (topLeft.y + size.height)
                                                },
                                            dragHandle = {
                                                var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                                Icon(
                                                    imageVector = Icons.Default.DragHandle,
                                                    contentDescription = "Drag",
                                                    tint = Color.Gray,
                                                    modifier = Modifier
                                                        .onGloballyPositioned { handleOffset = it.positionInWindow() }
                                                        .pointerInput(Unit) {
                                                            detectDragGesturesAfterLongPress(
                                                                onDragStart = { offset ->
                                                                    isDragging = true
                                                                    draggingSection = item.section
                                                                    dragPreview = item.name
                                                                    draggingExerciseId = item.id
                                                                    dragStartPointer = handleOffset + offset
                                                                    dragPosition = dragStartPointer
                                                                },
                                                                onDrag = { change, _ ->
                                                                    change.consume()
                                                                    dragPosition = handleOffset + change.position
                                                                    hoveredSection = sectionBounds.entries.find { entry ->
                                                                        dragPosition.y in entry.value.first..entry.value.second
                                                                    }?.key
                                                                },
                                                                onDragEnd = {
                                                                    hoveredSection?.let { sectionName ->
                                                                        val insertIdx = findInsertIndexForDrop(sectionName, dragPosition.y)
                                                                        val idx = selectedExercises.indexOfFirst { it.id == item.id }
                                                                        var clampedIdx = insertIdx.coerceIn(0, selectedExercises.size)
                                                                        if (idx >= 0 && selectedExercises[idx].section == sectionName && idx < clampedIdx) {
                                                                            clampedIdx -= 1
                                                                        }
                                                                        if (idx >= 0) {
                                                                            val moved = selectedExercises.removeAt(idx)
                                                                            val oldSection = moved.section
                                                                            selectedExercises.add(clampedIdx, moved.copy(section = sectionName))
                                                                            if (oldSection.isNotBlank() && oldSection != sectionName &&
                                                                                selectedExercises.none { it.section == oldSection }) {
                                                                                sections.remove(oldSection)
                                                                            }
                                                                        }
                                                                    }
                                                                    isDragging = false
                                                                    draggingSection = null
                                                                    dragPreview = null
                                                                    draggingExerciseId = null
                                                                    hoveredSection = null
                                                                },
                                                                onDragCancel = {
                                                                    isDragging = false
                                                                    draggingSection = null
                                                                    dragPreview = null
                                                                    draggingExerciseId = null
                                                                    hoveredSection = null
                                                                }
                                                            )
                                                        }
                                                        .detectReorderAfterLongPress(reorderState)
                                                )
                                            },
                                            supersetPartnerIndices = partnerIndices,
                                            elevation = elevation
                                        )
                                    }
                                }
                            }
                        } else {
                            val unassignedItems by remember(selectedExercises) {
                                derivedStateOf { selectedExercises.filter { it.section.isBlank() } }
                            }
                            if (unassignedItems.isNotEmpty()) {
                                SectionWrapper(
                                    title = "Unassigned",
                                    modifier = Modifier
                                        .zIndex(if (draggingSection == "") 1f else 0f)
                                        .onGloballyPositioned {
                                            val top = it.positionInWindow().y
                                            val bottom = top + it.size.height
                                            sectionBounds[""] = top to bottom
                                        },
                                    isDropActive = hoveredSection == "",
                                ) {
                                    val reorderState = rememberReorderableLazyListState(
                                        onMove = { from, to ->
                                            val current = selectedExercises.filter { it.section.isBlank() }
                                            val fromItem = current.getOrNull(from.index) ?: return@rememberReorderableLazyListState
                                            val toItem = current.getOrNull(to.index) ?: return@rememberReorderableLazyListState
                                            val fromIdx = selectedExercises.indexOf(fromItem)
                                            val toIdx = selectedExercises.indexOf(toItem)
                                            if (fromIdx >= 0 && toIdx >= 0) selectedExercises.move(fromIdx, toIdx)
                                        }
                                    )
                                    LazyColumn(
                                        state = reorderState.listState,
                                        modifier = Modifier
                                            .heightIn(max = screenHeight)
                                            .graphicsLayer { clip = false }
                                            .reorderable(reorderState)
                                            .detectReorderAfterLongPress(reorderState)
                                            .fillMaxWidth(),
                                        userScrollEnabled = false
                                    ) {
                                        itemsIndexed(unassignedItems, key = { _, item -> item.id }) { index, item ->
                                            ReorderableItem(reorderState, key = item.id) { itemDragging ->
                                                val elevation = if (itemDragging) 8.dp else 2.dp
                                                val partnerIndices = findSupersetPartners(item.id).mapNotNull { pid ->
                                                    selectedExercises.indexOfFirst { it.id == pid }.takeIf { it >= 0 }
                                                }
                                                var itemOffset by remember { mutableStateOf(Offset.Zero) }
                                                ReorderableExerciseItem(
                                                    index = index,
                                                    exercise = item,
                                                    onRemove = {
                                                        selectedExercises.remove(item)
                                                        removeSuperset(item.id)
                                                        supersetSelection.remove(item.id)
                                                    },
                                                    isSupersetSelected = supersetSelection.contains(item.id),
                                                    onSupersetSelectedChange = { checked ->
                                                        if (checked) {
                                                            if (!supersetSelection.contains(item.id)) supersetSelection.add(item.id)
                                                        } else supersetSelection.remove(item.id)
                                                    },
                                                    modifier = Modifier
                                                        .alpha(if (draggingExerciseId == item.id) 0f else 1f)
                                                        .zIndex(if (isDragging) 1000f else 0f)
                                                        .animateItemPlacement()
                                                        .onGloballyPositioned {
                                                            val topLeft = it.positionInWindow()
                                                            itemOffset = topLeft
                                                            val size = it.size.toSize()
                                                            itemBounds[item.id] = topLeft.y to (topLeft.y + size.height)
                                                        },
                                                    dragHandle = {
                                                        var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                                        Icon(
                                                            imageVector = Icons.Default.DragHandle,
                                                            contentDescription = "Drag",
                                                            tint = Color.Gray,
                                                            modifier = Modifier
                                                                .onGloballyPositioned { handleOffset = it.positionInWindow() }
                                                                .pointerInput(Unit) {
                                                                    detectDragGesturesAfterLongPress(
                                                                        onDragStart = { offset ->
                                                                            isDragging = true
                                                                            draggingSection = item.section
                                                                            dragPreview = item.name
                                                                            draggingExerciseId = item.id
                                                                            dragStartPointer = handleOffset + offset
                                                                            dragPosition = dragStartPointer
                                                                        },
                                                                        onDrag = { change, _ ->
                                                                            change.consume()
                                                                            dragPosition = handleOffset + change.position
                                                                            hoveredSection = sectionBounds.entries.find { entry ->
                                                                                dragPosition.y in entry.value.first..entry.value.second
                                                                            }?.key
                                                                        },
                                                                        onDragEnd = {
                                                                            hoveredSection?.let { sectionName ->
                                                                                val insertIdx = findInsertIndexForDrop(sectionName, dragPosition.y)
                                                                                val idx = selectedExercises.indexOfFirst { it.id == item.id }
                                                                                var clampedIdx = insertIdx.coerceIn(0, selectedExercises.size)
                                                                                if (idx >= 0 && selectedExercises[idx].section == sectionName && idx < clampedIdx) {
                                                                                    clampedIdx -= 1
                                                                                }
                                                                                if (idx >= 0) {
                                                                                    val moved = selectedExercises.removeAt(idx)
                                                                                    val oldSection = moved.section
                                                                                    selectedExercises.add(clampedIdx, moved.copy(section = sectionName))
                                                                                    if (oldSection.isNotBlank() && oldSection != sectionName &&
                                                                                        selectedExercises.none { it.section == oldSection }) {
                                                                                        sections.remove(oldSection)
                                                                                    }
                                                                                }
                                                                            }
                                                                            isDragging = false
                                                                            draggingSection = null
                                                                            dragPreview = null
                                                                            draggingExerciseId = null
                                                                            hoveredSection = null
                                                                        },
                                                                        onDragCancel = {
                                                                            isDragging = false
                                                                            draggingSection = null
                                                                            dragPreview = null
                                                                            draggingExerciseId = null
                                                                            hoveredSection = null
                                                                        }
                                                                    )
                                                                }
                                                                .detectReorderAfterLongPress(reorderState)
                                                        )
                                                    },
                                                    supersetPartnerIndices = partnerIndices,
                                                    elevation = elevation
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            sections.forEach { sectionName ->
                                val sectionItems by remember(selectedExercises, sectionName) {
                                    derivedStateOf { selectedExercises.filter { it.section == sectionName } }
                                }
                                SectionWrapper(
                                    title = sectionName,
                                    modifier = Modifier
                                        .zIndex(if (draggingSection == sectionName) 1f else 0f)
                                        .onGloballyPositioned {
                                            val top = it.positionInWindow().y
                                            val bottom = top + it.size.height
                                            sectionBounds[sectionName] = top to bottom
                                        },
                                    isDropActive = hoveredSection == sectionName,
                                ) {
                                    if (sectionItems.isEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                    } else {
                                        val reorderState = rememberReorderableLazyListState(
                                            onMove = { from, to ->
                                                val current = selectedExercises.filter { it.section == sectionName }
                                                val fromItem = current.getOrNull(from.index) ?: return@rememberReorderableLazyListState
                                                val toItem = current.getOrNull(to.index) ?: return@rememberReorderableLazyListState
                                                val fromIdx = selectedExercises.indexOf(fromItem)
                                                val toIdx = selectedExercises.indexOf(toItem)
                                                if (fromIdx >= 0 && toIdx >= 0) selectedExercises.move(fromIdx, toIdx)
                                            }
                                        )
                                        LazyColumn(
                                            state = reorderState.listState,
                                            modifier = Modifier
                                                .heightIn(max = screenHeight)
                                                .graphicsLayer { clip = false }
                                                .reorderable(reorderState)
                                                .detectReorderAfterLongPress(reorderState)
                                                .fillMaxWidth(),
                                            userScrollEnabled = false
                                        ) {
                                            itemsIndexed(sectionItems, key = { _, item -> item.id }) { index, item ->
                                                ReorderableItem(reorderState, key = item.id) { itemDragging ->
                                                    val elevation = if (itemDragging) 8.dp else 2.dp
                                                    val partnerIndices = findSupersetPartners(item.id).mapNotNull { pid ->
                                                        selectedExercises.indexOfFirst { it.id == pid }.takeIf { it >= 0 }
                                                    }
                                                    var itemOffset by remember { mutableStateOf(Offset.Zero) }
                                                    ReorderableExerciseItem(
                                                        index = index,
                                                        exercise = item,
                                                        onRemove = {
                                                            selectedExercises.remove(item)
                                                            removeSuperset(item.id)
                                                            supersetSelection.remove(item.id)
                                                            if (selectedExercises.none { it.section == sectionName }) {
                                                                sections.remove(sectionName)
                                                            }
                                                        },
                                                        isSupersetSelected = supersetSelection.contains(item.id),
                                                        onSupersetSelectedChange = { checked ->
                                                            if (checked) {
                                                                if (!supersetSelection.contains(item.id)) supersetSelection.add(item.id)
                                                            } else supersetSelection.remove(item.id)
                                                        },
                                                        modifier = Modifier
                                                            .alpha(if (draggingExerciseId == item.id) 0f else 1f)
                                                            .zIndex(if (isDragging) 1000f else 0f)
                                                            .animateItemPlacement()
                                                            .onGloballyPositioned {
                                                                val topLeft = it.positionInWindow()
                                                                itemOffset = topLeft
                                                                val size = it.size.toSize()
                                                                itemBounds[item.id] = topLeft.y to (topLeft.y + size.height)
                                                            },
                                                        dragHandle = {
                                                            var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                                            Icon(
                                                                imageVector = Icons.Default.DragHandle,
                                                                contentDescription = "Drag",
                                                                tint = Color.Gray,
                                                                modifier = Modifier
                                                                    .onGloballyPositioned { handleOffset = it.positionInWindow() }
                                                                    .pointerInput(Unit) {
                                                                        detectDragGesturesAfterLongPress(
                                                                            onDragStart = { offset ->
                                                                                isDragging = true
                                                                                draggingSection = item.section
                                                                                dragPreview = item.name
                                                                                draggingExerciseId = item.id
                                                                                dragStartPointer = handleOffset + offset
                                                                                dragPosition = dragStartPointer
                                                                            },
                                                                            onDrag = { change, _ ->
                                                                                change.consume()
                                                                                dragPosition = handleOffset + change.position
                                                                                hoveredSection = sectionBounds.entries.find { entry ->
                                                                                    dragPosition.y in entry.value.first..entry.value.second
                                                                                }?.key
                                                                            },
                                                                            onDragEnd = {
                                                                                hoveredSection?.let { sectionName ->
                                                                                    val insertIdx = findInsertIndexForDrop(sectionName, dragPosition.y)
                                                                                    val idx = selectedExercises.indexOfFirst { it.id == item.id }
                                                                                    var clampedIdx = insertIdx.coerceIn(0, selectedExercises.size)
                                                                                    if (idx >= 0 && selectedExercises[idx].section == sectionName && idx < clampedIdx) {
                                                                                        clampedIdx -= 1
                                                                                    }
                                                                                    if (idx >= 0) {
                                                                                        val moved = selectedExercises.removeAt(idx)
                                                                                        val oldSection = moved.section
                                                                                        selectedExercises.add(clampedIdx, moved.copy(section = sectionName))
                                                                                        if (oldSection.isNotBlank() && oldSection != sectionName &&
                                                                                            selectedExercises.none { it.section == oldSection }) {
                                                                                            sections.remove(oldSection)
                                                                                        }
                                                                                    }
                                                                                }
                                                                                isDragging = false
                                                                                draggingSection = null
                                                                                dragPreview = null
                                                                                draggingExerciseId = null
                                                                                hoveredSection = null
                                                                            },
                                                                            onDragCancel = {
                                                                                isDragging = false
                                                                                draggingSection = null
                                                                                dragPreview = null
                                                                                draggingExerciseId = null
                                                                                hoveredSection = null
                                                                            }
                                                                        )
                                                                    }
                                                                    .detectReorderAfterLongPress(reorderState)
                                                            )
                                                        },
                                                        supersetPartnerIndices = partnerIndices,
                                                        elevation = elevation
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    GaeguButton(
                        text = "➕ Create Section",
                        onClick = { showSectionSheet.value = true },
                        textColor = Color.Black
                    )

                    // (unverändert) – Section erstellen …
                    PoeticBottomSheet(
                        visible = showSectionSheet.value,
                        onDismiss = { showSectionSheet.value = false }
                    ) {
                        var selectedOption by remember { mutableStateOf<String?>(null) }
                        var customName by remember { mutableStateOf("") }
                        val selection = remember { mutableStateListOf<Long>() }

                        PoeticRadioChips(
                            options = listOf("Warm-up", "Workout", "Cooldown", "Custom"),
                            selected = selectedOption ?: "",
                            onSelected = { selectedOption = it },
                            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                        )

                        if (selectedOption == "Custom") {
                            Spacer(Modifier.height(12.dp))
                            LinedTextField(
                                value = customName,
                                onValueChange = { customName = it },
                                hint = "Section name",
                                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                                initialLines = 1
                            )
                        }

                        Spacer(Modifier.height(12.dp))
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 240.dp).fillMaxWidth()
                        ) {
                            items(selectedExercises) { ex ->
                                val checked = selection.contains(ex.id)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (checked) selection.remove(ex.id) else selection.add(ex.id)
                                        }
                                ) {
                                    Checkbox(checked = checked, onCheckedChange = null)
                                    Text(ex.name, fontFamily = GaeguRegular, color = Color.Black, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        GaeguButton(
                            text = "Add",
                            onClick = {
                                val name = if (selectedOption == "Custom") customName else selectedOption ?: ""
                                if (name.isNotBlank()) {
                                    if (!sections.contains(name)) sections.add(name)
                                    selectedExercises.forEachIndexed { idx, ex ->
                                        if (selection.contains(ex.id)) selectedExercises[idx] = ex.copy(section = name)
                                    }
                                }
                                showSectionSheet.value = false
                                selection.clear(); selectedOption = null; customName = ""
                            },
                            textColor = Color.Black
                        )
                    }

                    PoeticDivider()

                    Box(modifier = Modifier.fillMaxWidth()) {
                        GaeguButton(
                            text = "Cancel",
                            onClick = onCancel,
                            textColor = Color.Black,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                        WaxSealButton(
                            label = "Create",
                            onClick = {
                                if (title.isBlank() || selectedExercises.isEmpty()) { showError = true; return@WaxSealButton }
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
                            },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    if (showError) {
                        Text("Please fill out title and at least one exercise", color = Color.Black, fontFamily = GaeguRegular)
                    }
                }

                // Drag Preview (Koordinaten jetzt konsistent in Window-Space)
                if (isDragging && draggingExerciseId != null) {
                    val id = draggingExerciseId!!
                    val lineExercise = selectedExercises.find { it.id == id }
                    val previewName = dragPreview ?: lineExercise?.name ?: allExercises.find { it.id == id }?.name
                    previewName?.let { name ->
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(dragPosition.x.toInt(), dragPosition.y.toInt()),
                            properties = PopupProperties(
                                focusable = false,
                                dismissOnClickOutside = false,
                                dismissOnBackPress = false,
                                clippingEnabled = false
                            )
                        ) {
                            PoeticCard {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(name, fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.Black)
                                    lineExercise?.let {
                                        Text(
                                            "${it.sets} x ${it.repsOrDuration}",
                                            fontFamily = GaeguRegular,
                                            fontSize = 12.sp,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
