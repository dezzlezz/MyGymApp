package com.example.mygymapp.ui.pages

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.components.*
import com.example.mygymapp.ui.util.move
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.reorderable

/** Container for drag and drop state shared across composables. */
class DragAndDropState {
    var isDragging by mutableStateOf(false)
    var draggingExerciseId by mutableStateOf<Long?>(null)
    var dragPreview by mutableStateOf<String?>(null)
    var dragPosition by mutableStateOf(Offset.Zero)
    var draggingSection by mutableStateOf<String?>(null)
    var dragStartPointer by mutableStateOf(Offset.Zero)
    var dragStartLocal by mutableStateOf(Offset.Zero)
    var hoveredSection by mutableStateOf<String?>(null)
    val itemBounds = mutableStateMapOf<Long, Pair<Float, Float>>()
    val sectionBounds = mutableStateMapOf<String, Pair<Float, Float>>()
}

/** Helper encapsulating superset operations to keep logic consistent. */
class SupersetHelper(private val supersets: SnapshotStateList<MutableList<Long>>) {

    /** Return partners for [id] within its superset, if any. */
    fun partners(id: Long): List<Long> =
        supersets.firstOrNull { it.contains(id) }?.filter { it != id } ?: emptyList()

    /** Remove [id] from any supersets and drop groups smaller than two. */
    fun removeExercise(id: Long) {
        val iterator = supersets.iterator()
        while (iterator.hasNext()) {
            val group = iterator.next()
            if (group.remove(id) && group.size < 2) iterator.remove()
        }
    }

    /** Create a new superset with [ids], cleaning up prior groupings. */
    fun create(ids: List<Long>) {
        val distinctIds = ids.distinct()
        supersets.removeAll { group -> group.any { it in distinctIds } }
        if (distinctIds.size > 1) supersets.add(distinctIds.sorted().toMutableList())
    }
}

/** Unified drag handler used by picker items and list handles. */
fun Modifier.exerciseDrag(
    state: DragAndDropState,
    exerciseId: Long,
    exerciseName: String,
    startSection: String,
    getStartOffset: () -> Offset,
    allExercises: List<Exercise>,
    selectedExercises: SnapshotStateList<LineExercise>,
    findInsertIndex: (String, Float) -> Int,
    onStart: () -> Unit = {}
): Modifier = pointerInput(state, exerciseId, allExercises, selectedExercises) {
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            onStart()
            state.isDragging = true
            state.draggingExerciseId = exerciseId
            state.dragPreview = exerciseName
            state.draggingSection = startSection
            state.dragStartLocal = offset
            state.dragStartPointer = getStartOffset() + offset
            state.dragPosition = state.dragStartPointer
        },
        onDrag = { change, _ ->
            change.consume()
            state.dragPosition = state.dragStartPointer + (change.position - state.dragStartLocal)
            state.hoveredSection = state.sectionBounds.entries.find { entry ->
                state.dragPosition.y in entry.value.first..entry.value.second
            }?.key
        },
        onDragEnd = {
            state.hoveredSection?.let { sectionName ->
                val idx = selectedExercises.indexOfFirst { it.id == exerciseId }
                val fromSection = state.draggingSection
                if (sectionName != fromSection || idx < 0) {
                    val insertIdx = findInsertIndex(sectionName, state.dragPosition.y)
                    var clampedIdx = insertIdx.coerceIn(0, selectedExercises.size)
                    if (idx >= 0 && selectedExercises[idx].section == sectionName && idx < clampedIdx) {
                        clampedIdx -= 1
                    }
                    if (idx >= 0) {
                        val item = selectedExercises.removeAt(idx)
                        selectedExercises.add(clampedIdx, item.copy(section = sectionName))
                    } else {
                        allExercises.firstOrNull { it.id == exerciseId }?.let { ex ->
                            selectedExercises.add(
                                clampedIdx,
                                LineExercise(id = ex.id, name = ex.name, sets = 3, repsOrDuration = "10", section = sectionName)
                            )
                        }
                    }
                }
            }
            state.isDragging = false
            state.draggingExerciseId = null
            state.dragPreview = null
            state.draggingSection = null
            state.hoveredSection = null
        },
        onDragCancel = {
            state.isDragging = false
            state.draggingExerciseId = null
            state.dragPreview = null
            state.draggingSection = null
            state.hoveredSection = null
        }
    )
}

@Composable
fun ColumnScope.LineTitleAndCategoriesSection(
    title: String,
    onTitleChange: (String) -> Unit,
    categoryOptions: List<String>,
    selectedCategories: List<String>,
    onCategoryChange: (List<String>) -> Unit,
    muscleOptions: List<String>,
    selectedMuscles: List<String>,
    onMuscleChange: (List<String>) -> Unit,
    titleError: Boolean = false
) {
    PoeticDivider(centerText = "What would you title this day?")
    LinedTextField(
        value = title,
        onValueChange = onTitleChange,
        hint = "A poetic title...",
        initialLines = 1,
        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
        isError = titleError
    )
    PoeticDivider(centerText = "What kind of movement is this?")
    PoeticMultiSelectChips(
        options = categoryOptions,
        selectedItems = selectedCategories,
        onSelectionChange = onCategoryChange,
        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
    )
    PoeticDivider(centerText = "Which areas are involved?")
    PoeticMultiSelectChips(
        options = muscleOptions,
        selectedItems = selectedMuscles,
        onSelectionChange = onMuscleChange,
        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
    )
}

@Composable
fun ColumnScope.LineNotesSection(
    note: String,
    onNoteChange: (String) -> Unit
) {
    PoeticDivider(centerText = "Your notes on this movement")
    LinedTextField(
        value = note,
        onValueChange = onNoteChange,
        hint = "Write your thoughts here...",
        initialLines = 3,
        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerSheet(
    visible: Boolean,
    allExercises: List<Exercise>,
    selectedMuscles: List<String>,
    dragState: DragAndDropState,
    dragModifier: (Long, String, String, () -> Offset, () -> Unit) -> Modifier,
    onExerciseClicked: (Exercise) -> Unit,
    onCreateExercise: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val showExerciseSheet = visible
    var pickerAlpha by remember { mutableStateOf(1f) }
    val pickerAnimatedAlpha by animateFloatAsState(
        targetValue = pickerAlpha,
        animationSpec = tween(durationMillis = 200),
        finishedListener = { if (it == 0f) onDismiss() }
    )
    LaunchedEffect(showExerciseSheet) { if (showExerciseSheet) pickerAlpha = 1f }
    val exerciseSearch = remember { mutableStateOf("") }
    val filterOptions by remember(selectedMuscles) {
        derivedStateOf {
            val base = listOf("All", "Full Body")
            if (selectedMuscles.isEmpty()) base else (base + selectedMuscles).distinct()
        }
    }
    val selectedFilter = remember { mutableStateOf<String?>(null) }
    val filteredExercises by remember(exerciseSearch.value, selectedFilter.value, allExercises) {
        derivedStateOf {
            val query = exerciseSearch.value.trim().lowercase()
            allExercises.filter { ex ->
                val matchesFilter = selectedFilter.value == null || ex.muscleGroup.display == selectedFilter.value
                val matchesSearch = query.isEmpty() || ex.name.lowercase().contains(query)
                matchesFilter && matchesSearch
            }
        }
    }
    PoeticBottomSheet(visible = showExerciseSheet, onDismiss = { pickerAlpha = 0f }) {
        Column(modifier = Modifier.alpha(pickerAnimatedAlpha)) {
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
                        fontFamily = GaeguLight,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(12.dp)
                    )
                    GaeguButton(
                        text = "Create \"${exerciseSearch.value.trim()}\"",
                        onClick = { onCreateExercise(exerciseSearch.value.trim()) },
                        textColor = Color.Black
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp).fillMaxWidth()) {
                    items(filteredExercises, key = { it.id }) { ex ->
                        var cardOffset by remember { mutableStateOf(Offset.Zero) }
                        PoeticCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .onGloballyPositioned { cardOffset = it.positionInWindow() }
                                .alpha(if (dragState.draggingExerciseId == ex.id) 0f else 1f)
                                .then(dragModifier(ex.id, ex.name, "", { cardOffset }) { pickerAlpha = 0f })
                                .clickable {
                                    onExerciseClicked(ex)
                                    pickerAlpha = 0f
                                    exerciseSearch.value = ""
                                    selectedFilter.value = null
                                }
                        ) {
                            Text(ex.name, fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.Black)
                            Text(
                                "${ex.muscleGroup.display} · ${ex.category.display}",
                                fontFamily = GaeguLight,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SectionsWithDragDrop(
    sections: List<String>,
    selectedExercises: SnapshotStateList<LineExercise>,
    supersetHelper: SupersetHelper,
    dragState: DragAndDropState,
    allExercises: List<Exercise>,
    dragModifier: (Long, String, String, () -> Offset, () -> Unit) -> Modifier,
    findInsertIndexForDrop: (String, Float) -> Int
) {
    var showMoveSheet by remember { mutableStateOf(false) }
    var moveSelectedOption by remember { mutableStateOf<String?>(null) }
    var moveCustomName by remember { mutableStateOf("") }
    val moveSelection = remember { mutableStateListOf<Long>() }

    PoeticBottomSheet(
        visible = showMoveSheet,
        onDismiss = {
            showMoveSheet = false
            moveSelection.clear(); moveSelectedOption = null; moveCustomName = ""
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PoeticRadioChips(
                options = listOf("Warm-up", "Workout", "Cooldown", "Custom"),
                selected = moveSelectedOption ?: "",
                onSelected = { moveSelectedOption = it },
                modifier = Modifier.fillMaxWidth()
            )
            if (moveSelectedOption == "Custom") {
                Spacer(Modifier.height(12.dp))
                LinedTextField(
                    value = moveCustomName,
                    onValueChange = { moveCustomName = it },
                    hint = "Section name",
                    modifier = Modifier.fillMaxWidth(),
                    initialLines = 1
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.heightIn(max = 240.dp).fillMaxWidth()) {
                items(selectedExercises) { ex ->
                    val checked = moveSelection.contains(ex.id)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (checked) moveSelection.remove(ex.id) else moveSelection.add(
                                    ex.id
                                )
                            }
                    ) {
                        Checkbox(checked = checked, onCheckedChange = null)
                        Text(
                            ex.name,
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            GaeguButton(
                text = "Move",
                onClick = {
                    val name =
                        if (moveSelectedOption == "Custom") moveCustomName else moveSelectedOption
                            ?: ""
                    if (name.isNotBlank()) {
                        selectedExercises.forEachIndexed { idx, ex ->
                            if (moveSelection.contains(ex.id)) {
                                selectedExercises[idx] = ex.copy(section = name)
                            }
                        }
                    }
                    showMoveSheet = false
                    moveSelection.clear(); moveSelectedOption = null; moveCustomName = ""
                },
                textColor = Color.Black
            )
        }
    }
    if (selectedExercises.isNotEmpty()) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp
        if (sections.isEmpty()) {
            Text("Today's selected movements:", fontFamily = GaeguBold, color = Color.Black)
            val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
                selectedExercises.move(
                    from.index,
                    to.index
                )
            })
            val isDropActive = dragState.hoveredSection == ""
            val bgColor by animateColorAsState(if (isDropActive) Color(0xFFF5F5DC) else Color.Transparent)
            val borderColor by animateColorAsState(if (isDropActive) Color(0xFFE0DCC8) else Color.Transparent)
            val extraPadding by animateDpAsState(if (isDropActive) 8.dp else 0.dp)
            Box(
                modifier = Modifier
                    .onGloballyPositioned {
                        val top = it.positionInWindow().y
                        val bottom = top + it.size.height
                        dragState.sectionBounds[""] = top to bottom
                    }
                    .background(bgColor)
                    .border(1.dp, borderColor)
                    .shadow(if (isDropActive) 4.dp else 0.dp)
                    .padding(vertical = extraPadding)
                    .fillMaxWidth()
            ) {
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
                        ReorderableItem(reorderState, key = item.id) { dragging ->
                            val elevation = if (dragging) 8.dp else 2.dp
                            val partnerIndices =
                                supersetHelper.partners(item.id).mapNotNull { pid ->
                                    selectedExercises.indexOfFirst { it.id == pid }
                                        .takeIf { it >= 0 }
                                }
                            val isDraggingPartner = dragState.draggingExerciseId?.let {
                                supersetHelper.partners(it).contains(item.id)
                            } == true
                            ReorderableExerciseItem(
                                index = index,
                                exercise = item,
                                onRemove = {
                                    selectedExercises.remove(item)
                                    supersetHelper.removeExercise(item.id)
                                },
                                onMove = {
                                    showMoveSheet = true
                                    moveSelection.clear(); moveSelection.add(item.id)
                                    moveSelectedOption = null; moveCustomName = ""
                                },
                                modifier = Modifier
                                    .alpha(if (dragState.draggingExerciseId == item.id) 0f else 1f)
                                    .onGloballyPositioned {
                                        if (dragState.isDragging) {
                                            val topLeft = it.positionInWindow()
                                            val size = it.size.toSize()
                                            dragState.itemBounds[item.id] =
                                                topLeft.y to (topLeft.y + size.height)
                                        }
                                    },
                                dragHandle = {
                                    var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = "Drag",
                                        tint = Color.Gray,
                                        modifier = Modifier
                                            .onGloballyPositioned {
                                                if (!dragState.isDragging) {
                                                    handleOffset = it.positionInWindow()
                                                }
                                            }
                                            .then(
                                                dragModifier(
                                                    item.id,
                                                    item.name,
                                                    item.section,
                                                    { handleOffset }) { })
                                    )
                                },
                                supersetPartnerIndices = partnerIndices,
                                isDraggingPartner = isDraggingPartner,
                                elevation = elevation
                            )
                        }
                    }
                }
            }
        } else {
            val unassignedItems by remember(selectedExercises) { derivedStateOf { selectedExercises.filter { it.section.isBlank() } } }
            if (unassignedItems.isNotEmpty()) {
                val isDropActive = dragState.hoveredSection == ""
                val bgColor by animateColorAsState(if (isDropActive) Color(0xFFF5F5DC) else Color.Transparent)
                val borderColor by animateColorAsState(if (isDropActive) Color(0xFFE0DCC8) else Color.Transparent)
                val scale by animateFloatAsState(if (isDropActive) 1.02f else 1f)
                SectionWrapper(
                    title = "Unassigned",
                    modifier = Modifier
                        .onGloballyPositioned {
                            if (dragState.isDragging) {
                                val top = it.positionInWindow().y
                                val bottom = top + it.size.height
                                dragState.sectionBounds[""] = top to bottom
                            }
                        }
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .background(bgColor)
                        .border(1.dp, borderColor)
                        .shadow(if (isDropActive) 4.dp else 0.dp),
                    isDropActive = isDropActive
                ) {
                    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
                        val current = selectedExercises.filter { it.section.isBlank() }
                        val fromItem =
                            current.getOrNull(from.index) ?: return@rememberReorderableLazyListState
                        val toItem =
                            current.getOrNull(to.index) ?: return@rememberReorderableLazyListState
                        val fromIdx = selectedExercises.indexOf(fromItem)
                        val toIdx = selectedExercises.indexOf(toItem)
                        if (fromIdx >= 0 && toIdx >= 0) selectedExercises.move(fromIdx, toIdx)
                    })
                    LazyColumn(
                        state = reorderState.listState,
                        modifier = Modifier
                            .heightIn(max = screenHeight)
                            .reorderable(reorderState)
                            .detectReorderAfterLongPress(reorderState)
                            .fillMaxWidth(),
                        userScrollEnabled = false
                    ) {
                        itemsIndexed(unassignedItems, key = { _, item -> item.id }) { index, item ->
                            ReorderableItem(reorderState, key = item.id) { dragging ->
                                val elevation = if (dragging) 8.dp else 2.dp
                                val partnerIndices =
                                    supersetHelper.partners(item.id).mapNotNull { pid ->
                                        selectedExercises.indexOfFirst { it.id == pid }
                                            .takeIf { it >= 0 }
                                    }
                                val isDraggingPartner = dragState.draggingExerciseId?.let {
                                    supersetHelper.partners(it).contains(item.id)
                                } == true
                                ReorderableExerciseItem(
                                    index = index,
                                    exercise = item,
                                    onRemove = {
                                        selectedExercises.remove(item)
                                        supersetHelper.removeExercise(item.id)
                                    },
                                    onMove = {
                                        showMoveSheet = true
                                        moveSelection.clear(); moveSelection.add(item.id)
                                        moveSelectedOption = null; moveCustomName = ""
                                    },
                                    modifier = Modifier
                                        .alpha(if (dragState.draggingExerciseId == item.id) 0f else 1f)
                                        .onGloballyPositioned {
                                            if (dragState.isDragging) {
                                                val topLeft = it.positionInWindow()
                                                val size = it.size.toSize()
                                                dragState.itemBounds[item.id] =
                                                    topLeft.y to (topLeft.y + size.height)
                                            }
                                        },
                                    dragHandle = {
                                        var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                        Icon(
                                            imageVector = Icons.Default.DragHandle,
                                            contentDescription = "Drag",
                                            tint = Color.Gray,
                                            modifier = Modifier
                                                .onGloballyPositioned {
                                                    if (!dragState.isDragging) {
                                                        handleOffset = it.positionInWindow()
                                                    }
                                                }
                                                .then(
                                                    dragModifier(
                                                        item.id,
                                                        item.name,
                                                        item.section,
                                                        { handleOffset }) { })
                                        )
                                    },
                                    supersetPartnerIndices = partnerIndices,
                                    isDraggingPartner = isDraggingPartner,
                                    elevation = elevation
                                )
                            }
                        }
                    }
                }
            }
            sections.forEach { sectionName ->
                val items = selectedExercises.filter { it.section == sectionName }
                val isDropActive = dragState.hoveredSection == sectionName
                val bgColor by animateColorAsState(if (isDropActive) Color(0xFFF5F5DC) else Color.Transparent)
                val borderColor by animateColorAsState(if (isDropActive) Color(0xFFE0DCC8) else Color.Transparent)
                SectionWrapper(
                    title = sectionName,
                    modifier = Modifier
                        .onGloballyPositioned {
                            if (dragState.isDragging) {
                                val top = it.positionInWindow().y
                                val bottom = top + it.size.height
                                dragState.sectionBounds[sectionName] = top to bottom
                            }
                        }
                        .background(bgColor)
                        .border(1.dp, borderColor)
                        .shadow(if (isDropActive) 4.dp else 0.dp),
                    isDropActive = isDropActive
                ) {
                    if (items.isEmpty()) {
                        Box(
                            Modifier.fillMaxWidth().height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Drop a movement here",
                                fontFamily = GaeguRegular,
                                color = Color.Gray
                            )
                        }
                    } else {
                        val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
                            val fromIdx = selectedExercises.indexOf(items[from.index])
                            val toIdx = selectedExercises.indexOf(items[to.index])
                            if (fromIdx >= 0 && toIdx >= 0) selectedExercises.move(fromIdx, toIdx)
                        })
                        LazyColumn(
                            state = reorderState.listState,
                            modifier = Modifier
                                .heightIn(max = screenHeight)
                                .reorderable(reorderState)
                                .detectReorderAfterLongPress(reorderState)
                                .fillMaxWidth(),
                            userScrollEnabled = false,
                        ) {
                            itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                                ReorderableItem(reorderState, key = item.id) { dragging ->
                                    val elevation = if (dragging) 8.dp else 2.dp
                                    val partnerIndices =
                                        supersetHelper.partners(item.id).mapNotNull { pid ->
                                            selectedExercises.indexOfFirst { it.id == pid }
                                                .takeIf { it >= 0 }
                                        }
                                    val isDraggingPartner = dragState.draggingExerciseId?.let {
                                        supersetHelper.partners(it).contains(item.id)
                                    } == true
                                    ReorderableExerciseItem(
                                        index = index,
                                        exercise = item,
                                        onRemove = {
                                            selectedExercises.remove(item)
                                            supersetHelper.removeExercise(item.id)
                                        },
                                        onMove = {
                                            showMoveSheet = true
                                            moveSelection.clear(); moveSelection.add(item.id)
                                            moveSelectedOption = null; moveCustomName = ""
                                        },
                                        modifier = Modifier
                                            .alpha(if (dragState.draggingExerciseId == item.id) 0f else 1f)
                                            .onGloballyPositioned {
                                                if (dragState.isDragging) {
                                                    val topLeft = it.positionInWindow()
                                                    val size = it.size.toSize()
                                                    dragState.itemBounds[item.id] =
                                                        topLeft.y to (topLeft.y + size.height)
                                                }
                                            },
                                        dragHandle = {
                                            var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                            Icon(
                                                imageVector = Icons.Default.DragHandle,
                                                contentDescription = "Drag",
                                                tint = Color.Gray,
                                                modifier = Modifier
                                                    .onGloballyPositioned {
                                                        if (!dragState.isDragging) {
                                                            handleOffset = it.positionInWindow()
                                                        }
                                                    }
                                                    .then(
                                                        dragModifier(
                                                            item.id,
                                                            item.name,
                                                            item.section,
                                                            { handleOffset }) { })
                                            )
                                        },
                                        supersetPartnerIndices = partnerIndices,
                                        isDraggingPartner = isDraggingPartner,
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
}

