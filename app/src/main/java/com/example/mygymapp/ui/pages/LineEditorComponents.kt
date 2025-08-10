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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.components.*
import android.widget.Toast
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
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

/** State holder encapsulating superset operations to keep logic consistent. */
class SupersetState(private val supersets: SnapshotStateList<MutableList<Long>>) {

    /** Return partners for [id] within its superset, if any. */
    fun partners(id: Long): List<Long> =
        supersets.firstOrNull { it.contains(id) }?.filter { it != id } ?: emptyList()

    /** Whether [id] belongs to any superset group. */
    fun containsGroup(id: Long): Boolean = supersets.any { it.contains(id) }

    /** Remove [id] from any supersets and drop groups smaller than two. */
    fun removeExercise(id: Long) {
        val iterator = supersets.iterator()
        while (iterator.hasNext()) {
            val group = iterator.next()
            if (group.remove(id) && group.size < 2) iterator.remove()
        }
    }

    /**
     * Create or merge a superset containing all [ids]. Existing groups that share any
     * of the provided ids are merged into a single group. The resulting group maintains
     * the order supplied by [ids] and is only added when it contains at least two items.
     */
    fun addGroup(ids: List<Long>) {
        val distinct = ids.distinct()
        if (distinct.size < 2) return

        val merged = supersets.filter { group -> group.any { it in distinct } }
        val combined = (distinct + merged.flatten()).distinct()
        supersets.removeAll(merged)
        supersets.add(combined.toMutableList())
    }

    /** Remove an entire group matching [ids]. */
    fun removeGroup(ids: List<Long>) {
        supersets.removeAll { group -> ids.all { it in group } }
    }

    /** Replace all groups with [groups]. */
    fun replaceAll(groups: List<List<Long>>) {
        supersets.clear()
        groups.forEach { supersets.add(it.toMutableList()) }
    }

    /** Snapshot of current groups for persistence. */
    val groups: List<List<Long>>
        get() = supersets.map { it.toList() }
}

private fun moveWithSuperset(
    list: SnapshotStateList<LineExercise>,
    supersetState: SupersetState,
    fromIdx: Int,
    toIdx: Int
) {
    if (fromIdx == toIdx) return
    val moving = list.getOrNull(fromIdx) ?: return
    val partnerIds = supersetState.partners(moving.id)
    val groupIds = listOf(moving.id) + partnerIds
    val groupItems = groupIds.mapNotNull { id -> list.find { it.id == id } }
    val indices = groupItems.map { list.indexOf(it) }.sorted()
    val block = indices.map { list[it] }
    for (i in indices.asReversed()) list.removeAt(i)
    var insert = toIdx
    val first = indices.firstOrNull() ?: 0
    if (insert > first) insert -= block.size
    list.addAll(insert.coerceIn(0, list.size), block)
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
    supersetState: SupersetState,
    findInsertIndex: (String, Float) -> Int,
    onStart: () -> Unit = {}
): Modifier = pointerInput(state, exerciseId, allExercises, selectedExercises, supersetState) {
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
                    val partnerIds = supersetState.partners(exerciseId)
                    val groupIds = listOf(exerciseId) + partnerIds
                    val groupItems = groupIds.mapNotNull { id -> selectedExercises.find { it.id == id } }
                    val indices = groupItems.map { selectedExercises.indexOf(it) }.sorted()
                    if (indices.isNotEmpty()) {
                        val firstIdx = indices.first()
                        if (idx >= 0 && selectedExercises[idx].section == sectionName && firstIdx < clampedIdx) {
                            clampedIdx -= indices.size
                        }
                        val block = indices.map { selectedExercises[it] }
                        indices.asReversed().forEach { selectedExercises.removeAt(it) }
                        selectedExercises.addAll(
                            clampedIdx,
                            block.map { it.copy(section = sectionName) }
                        )
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

@OptIn(ExperimentalFoundationApi::class)
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
    titleError: Boolean = false,
    titleBringIntoViewRequester: BringIntoViewRequester? = null
) {
    PoeticDivider(centerText = "What would you title this day?")
    LinedTextField(
        value = title,
        onValueChange = onTitleChange,
        hint = "A poetic title...",
        initialLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally),
        isError = titleError,
        bringIntoViewRequester = titleBringIntoViewRequester
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.LineNotesSection(
    note: String,
    onNoteChange: (String) -> Unit,
    noteBringIntoViewRequester: BringIntoViewRequester? = null
) {
    PoeticDivider(centerText = "Your notes on this movement")
    LinedTextField(
        value = note,
        onValueChange = onNoteChange,
        hint = "Write your thoughts here...",
        initialLines = 3,
        modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
        bringIntoViewRequester = noteBringIntoViewRequester
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SectionsWithDragDrop(
    sections: List<String>,
    selectedExercises: SnapshotStateList<LineExercise>,
    supersetState: SupersetState,
    dragState: DragAndDropState,
    allExercises: List<Exercise>,
    dragModifier: (Long, String, String, () -> Offset, () -> Unit) -> Modifier,
    findInsertIndexForDrop: (String, Float) -> Int,
    snackbarHostState: SnackbarHostState
) {
    var showMoveSheet by remember { mutableStateOf(false) }
    var moveSelectedOption by remember { mutableStateOf<String?>(null) }
    var moveCustomName by remember { mutableStateOf("") }
    val moveSelection = remember { mutableStateListOf<Long>() }
    val scope = rememberCoroutineScope()
    val removeMessage = stringResource(R.string.movement_removed)
    val undoLabel = stringResource(R.string.undo)

    val rangeSelector = remember { SupersetRangeSelector(dragState.itemBounds) }
    var pendingRangeIds by remember { mutableStateOf<List<Long>>(emptyList()) }
    var pendingCaption by remember { mutableStateOf<String?>(null) }
    var listTop by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    val density = LocalDensity.current
    val pullApartThreshold = with(density) { 24.dp.toPx() }

    fun removeExercise(exercise: LineExercise) {
        val index = selectedExercises.indexOf(exercise)
        if (index < 0) return
        val partners = supersetState.partners(exercise.id)
        selectedExercises.removeAt(index)
        supersetState.removeExercise(exercise.id)
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = removeMessage,
                actionLabel = undoLabel
            )
            if (result == SnackbarResult.ActionPerformed) {
                val insertIndex = index.coerceAtMost(selectedExercises.size)
                selectedExercises.add(insertIndex, exercise)
                supersetState.addGroup(listOf(exercise.id) + partners)
            }
        }
    }

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
        Box(
            modifier = Modifier
                .onGloballyPositioned { listTop = it.positionInWindow().y }
                .pointerInput(dragState, selectedExercises) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (dragState.isDragging) {
                                rangeSelector.reset()
                                pendingRangeIds = emptyList()
                                pendingCaption = null
                                continue
                            }
                            val active = event.changes.filter { it.pressed }
                            val pointers = active.map { change ->
                                PointerInfo(
                                    change.id.value,
                                    Offset(0f, listTop + change.position.y)
                                )
                            }
                            val pulledApart = rangeSelector.isOutwardPull(pullApartThreshold)
                            val selection = rangeSelector.onPointerEvent(listTop, pointers)
                            if (active.size == 2 && selection != null) {
                                pendingRangeIds = selection.idsInRange
                                pendingCaption = if (selection.idsInRange.size < 2) {
                                    "Select at least two"
                                } else if (supersetState.partners(selection.startId)
                                        .contains(selection.endId)
                                ) {
                                    "Split Superset (${selection.idsInRange.size})"
                                } else {
                                    "Create Superset (${selection.idsInRange.size})"
                                }
                            } else if (active.isEmpty()) {
                                pendingRangeIds = emptyList()
                                pendingCaption = null
                                selection?.let { sel ->
                                    if (sel.idsInRange.size >= 2) {
                                        val firstSection =
                                            selectedExercises.find { it.id == sel.idsInRange.first() }?.section
                                        val sameSection = sel.idsInRange.all { id ->
                                            selectedExercises.find { it.id == id }?.section == firstSection
                                        }
                                        if (!sameSection) {
                                            Toast.makeText(
                                                context,
                                                "Can't span multiple sections",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            val sameGroup = supersetState.partners(sel.startId)
                                                .contains(sel.endId)
                                            if (sameGroup && pulledApart) {
                                                val before = supersetState.groups
                                                val rangeIds = sel.idsInRange
                                                val exactGroup = supersetState.groups.any { group ->
                                                    group.size == rangeIds.size && group.containsAll(
                                                        rangeIds
                                                    )
                                                }
                                                if (exactGroup) {
                                                    supersetState.removeGroup(rangeIds)
                                                } else {
                                                    rangeIds.forEach {
                                                        supersetState.removeExercise(
                                                            it
                                                        )
                                                    }
                                                }
                                                scope.launch {
                                                    val result = snackbarHostState.showSnackbar(
                                                        message = context.getString(R.string.superset_split),
                                                        actionLabel = undoLabel
                                                    )
                                                    if (result == SnackbarResult.ActionPerformed) {
                                                        supersetState.replaceAll(before)
                                                    }
                                                }
                                            } else if (!sameGroup) {
                                                supersetState.addGroup(sel.idsInRange)
                                                scope.launch {
                                                    val result = snackbarHostState.showSnackbar(
                                                        message = context.getString(R.string.create_superset),
                                                        actionLabel = undoLabel
                                                    )
                                                    if (result == SnackbarResult.ActionPerformed) {
                                                        supersetState.removeGroup(sel.idsInRange)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                pendingRangeIds = emptyList()
                                pendingCaption = null
                            }
                        }
                    }
                }
        ) {
            if (sections.isEmpty()) {
                Text("Today's selected movements:", fontFamily = GaeguBold, color = Color.Black)
                val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
                    moveWithSuperset(selectedExercises, supersetState, from.index, to.index)
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
                        itemsIndexed(
                            selectedExercises,
                            key = { _, item -> item.id }) { index, item ->
                            ReorderableItem(reorderState, key = item.id) { dragging ->
                                val elevation = if (dragging) 8.dp else 2.dp
                                val partnerIndices =
                                    supersetState.partners(item.id).mapNotNull { pid ->
                                        selectedExercises.indexOfFirst { it.id == pid }
                                            .takeIf { it >= 0 }
                                    }
                                val isDraggingPartner = dragState.draggingExerciseId?.let {
                                    supersetState.partners(it).contains(item.id)
                                } == true
                                val isInRange = pendingRangeIds.contains(item.id)
                                val caption =
                                    if (item.id == pendingRangeIds.firstOrNull()) pendingCaption else null
                                Column {
                                    if (caption != null) {
                                        Text(
                                            caption,
                                            fontFamily = GaeguRegular,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(
                                                start = 32.dp,
                                                bottom = 4.dp
                                            )
                                        )
                                    }
                                    val dismissState = rememberDismissState(confirmValueChange = {
                                        if (it == DismissValue.DismissedToStart) {
                                            removeExercise(item)
                                            true
                                        } else {
                                            false
                                        }
                                    })
                                    SwipeToDismiss(
                                        state = dismissState,
                                        directions = if (dragState.isDragging) emptySet() else setOf(DismissDirection.EndToStart),
                                        background = {
                                            Box(
                                                Modifier
                                                    .fillMaxSize()
                                                    .background(Color.Red)
                                                    .padding(horizontal = 20.dp),
                                                contentAlignment = Alignment.CenterEnd
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                            }
                                        },
                                        dismissContent = {
                                            ReorderableExerciseItem(
                                                index = index,
                                                exercise = item,
                                                onMove = {
                                                    showMoveSheet = true
                                                    moveSelection.clear(); moveSelection.add(item.id)
                                                    moveSelectedOption = null; moveCustomName = ""
                                                },
                                                modifier = Modifier
                                                    .alpha(if (dragState.draggingExerciseId == item.id) 0f else 1f)
                                                    .onGloballyPositioned {
                                                        val topLeft = it.positionInWindow()
                                                        val size = it.size.toSize()
                                                        dragState.itemBounds[item.id] =
                                                            topLeft.y to (topLeft.y + size.height)
                                                    },
                                                dragHandle = {
                                                    var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                                    Icon(
                                                        imageVector = Icons.Default.DragHandle,
                                                        contentDescription = stringResource(R.string.reorder_movement),
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
                                                isDragTarget = isInRange,
                                                elevation = elevation
                                            )
                                        }
                                    )
                                }
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
                        title = stringResource(R.string.unassigned),
                        modifier = Modifier
                            .onGloballyPositioned {
                                val top = it.positionInWindow().y
                                val bottom = top + it.size.height
                                dragState.sectionBounds[""] = top to bottom
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
                                current.getOrNull(from.index)
                                    ?: return@rememberReorderableLazyListState
                            val toItem =
                                current.getOrNull(to.index)
                                    ?: return@rememberReorderableLazyListState
                            val fromIdx = selectedExercises.indexOf(fromItem)
                            val toIdx = selectedExercises.indexOf(toItem)
                            if (fromIdx >= 0 && toIdx >= 0) {
                                moveWithSuperset(selectedExercises, supersetState, fromIdx, toIdx)
                            }
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
                            itemsIndexed(
                                unassignedItems,
                                key = { _, item -> item.id }) { index, item ->
                                ReorderableItem(reorderState, key = item.id) { dragging ->
                                    val elevation = if (dragging) 8.dp else 2.dp
                                    val partnerIndices =
                                        supersetState.partners(item.id).mapNotNull { pid ->
                                            selectedExercises.indexOfFirst { it.id == pid }
                                                .takeIf { it >= 0 }
                                        }
                                    val isDraggingPartner = dragState.draggingExerciseId?.let {
                                        supersetState.partners(it).contains(item.id)
                                    } == true
                                    val isInRange = pendingRangeIds.contains(item.id)
                                    val caption =
                                        if (item.id == pendingRangeIds.firstOrNull()) pendingCaption else null
                                    Column {
                                        if (caption != null) {
                                            Text(
                                                caption,
                                                fontFamily = GaeguRegular,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(
                                                    start = 32.dp,
                                                    bottom = 4.dp
                                                )
                                            )
                                        }
                                        val dismissState = rememberDismissState(confirmValueChange = {
                                            if (it == DismissValue.DismissedToStart) {
                                                removeExercise(item)
                                                true
                                            } else {
                                                false
                                            }
                                        })
                                        SwipeToDismiss(
                                            state = dismissState,
                                            directions = if (dragState.isDragging) emptySet() else setOf(DismissDirection.EndToStart),
                                            background = {
                                                Box(
                                                    Modifier
                                                        .fillMaxSize()
                                                        .background(Color.Red)
                                                        .padding(horizontal = 20.dp),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = null,
                                                        tint = Color.White
                                                    )
                                                }
                                            },
                        
                                            dismissContent = {
                                                ReorderableExerciseItem(
                                                    index = index,
                                                    exercise = item,
                                                    onMove = {
                                                        showMoveSheet = true
                                                        moveSelection.clear(); moveSelection.add(item.id)
                                                        moveSelectedOption = null; moveCustomName = ""
                                                    },
                                                    modifier = Modifier
                                                        .alpha(if (dragState.draggingExerciseId == item.id) 0f else 1f)
                                                        .onGloballyPositioned {
                                                            val topLeft = it.positionInWindow()
                                                            val size = it.size.toSize()
                                                            dragState.itemBounds[item.id] =
                                                                topLeft.y to (topLeft.y + size.height)
                                                        },
                                                    dragHandle = {
                                                        var handleOffset by remember { mutableStateOf(Offset.Zero) }
                                                        Icon(
                                                            imageVector = Icons.Default.DragHandle,
                                                            contentDescription = stringResource(R.string.reorder_movement),
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
                                                    isDragTarget = isInRange,
                                                    elevation = elevation
                                                )
                                            }
                                        )
                                    }
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
                                val top = it.positionInWindow().y
                                val bottom = top + it.size.height
                                dragState.sectionBounds[sectionName] = top to bottom
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
                            val reorderState =
                                rememberReorderableLazyListState(onMove = { from, to ->
                                    val fromIdx = selectedExercises.indexOf(items[from.index])
                                    val toIdx = selectedExercises.indexOf(items[to.index])
                                    if (fromIdx >= 0 && toIdx >= 0) {
                                        moveWithSuperset(
                                            selectedExercises,
                                            supersetState,
                                            fromIdx,
                                            toIdx
                                        )
                                    }
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
                                            supersetState.partners(item.id).mapNotNull { pid ->
                                                selectedExercises.indexOfFirst { it.id == pid }
                                                    .takeIf { it >= 0 }
                                            }
                                        val isDraggingPartner = dragState.draggingExerciseId?.let {
                                            supersetState.partners(it).contains(item.id)
                                        } == true
                                        val isInRange = pendingRangeIds.contains(item.id)
                                        val caption =
                                            if (item.id == pendingRangeIds.firstOrNull()) pendingCaption else null
                                        Column {
                                            if (caption != null) {
                                                Text(
                                                    caption,
                                                    fontFamily = GaeguRegular,
                                                    color = Color.Gray,
                                                    modifier = Modifier.padding(
                                                        start = 32.dp,
                                                        bottom = 4.dp
                                                    )
                                                )
                                            }
                                            val dismissState = rememberDismissState(confirmValueChange = {
                                                if (it == DismissValue.DismissedToStart) {
                                                    removeExercise(item)
                                                    true
                                                } else {
                                                    false
                                                }
                                            })
                                            SwipeToDismiss(
                                                state = dismissState,
                                                directions = if (dragState.isDragging) emptySet() else setOf(DismissDirection.EndToStart),
                                                background = {
                                                    Box(
                                                        Modifier
                                                            .fillMaxSize()
                                                            .background(Color.Red)
                                                            .padding(horizontal = 20.dp),
                                                        contentAlignment = Alignment.CenterEnd
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Delete,
                                                            contentDescription = null,
                                                            tint = Color.White
                                                        )
                                                    }
                                                },
                                                dismissContent = {
                                                    ReorderableExerciseItem(
                                                        index = index,
                                                        exercise = item,
                                                        onMove = {
                                                            showMoveSheet = true
                                                            moveSelection.clear(); moveSelection.add(item.id)
                                                            moveSelectedOption = null; moveCustomName = ""
                                                        },
                                                        modifier = Modifier
                                                            .alpha(if (dragState.draggingExerciseId == item.id) 0f else 1f)
                                                            .onGloballyPositioned {
                                                                val topLeft = it.positionInWindow()
                                                                val size = it.size.toSize()
                                                                dragState.itemBounds[item.id] =
                                                                    topLeft.y to (topLeft.y + size.height)
                                                            },
                                                        dragHandle = {
                                                            var handleOffset by remember {
                                                                mutableStateOf(
                                                                    Offset.Zero
                                                                )
                                                            }
                                                            Icon(
                                                                imageVector = Icons.Default.DragHandle,
                                                                contentDescription = stringResource(R.string.reorder_movement),
                                                                tint = Color.Gray,
                                                                modifier = Modifier
                                                                    .onGloballyPositioned {
                                                                        if (!dragState.isDragging) {
                                                                            handleOffset =
                                                                                it.positionInWindow()
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
                                                        isDragTarget = isInRange,
                                                        elevation = elevation
                                                    )
                                                }
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
}

