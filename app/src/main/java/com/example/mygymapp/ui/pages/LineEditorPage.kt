package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.border
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.components.GaeguButton
import com.example.mygymapp.ui.components.LinedTextField
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticDivider
import com.example.mygymapp.ui.components.WaxSealButton
import com.example.mygymapp.ui.components.PoeticCard
import com.example.mygymapp.viewmodel.ExerciseViewModel
import android.net.Uri

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
            save = { ArrayList(it) },
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
            initial?.exercises?.map { it.section }?.filter { it.isNotBlank() }?.distinct()?.let { addAll(it) }
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
    val supersetHelper = remember { SupersetHelper(supersets) }

    val categoryOptions = listOf("💪 Strength", "🔥 Cardio", "🌱 Warmup", "🧘 Flexibility", "🌈 Recovery")
    val muscleOptions = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Arms", "Full Body")
    val selectedCategories = rememberSaveable(
        saver = listSaver<SnapshotStateList<String>, String>(
            save = { ArrayList(it) },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<String>().apply { initial?.category?.split(",")?.let { addAll(it) } } }
    val selectedMuscles = rememberSaveable(
        saver = listSaver<SnapshotStateList<String>, String>(
            save = { ArrayList(it) },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf<String>().apply { initial?.muscleGroup?.split(",")?.let { addAll(it) } } }

    var showError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val dragState = remember { DragAndDropState() }
    val scrollState = rememberScrollState()
    val exerciseBringIntoView = remember { BringIntoViewRequester() }

    LaunchedEffect(showError) {
        if (showError) {
            if (title.isBlank()) {
                scrollState.animateScrollTo(0)
            } else if (selectedExercises.isEmpty()) {
                exerciseBringIntoView.bringIntoView()
            }
        }
    }

    fun findInsertIndexForDrop(sectionName: String, dropY: Float): Int {
        val entries = selectedExercises.withIndex().filter { it.value.section == sectionName }
        if (entries.isEmpty()) {
            val last = selectedExercises.indexOfLast { it.section == sectionName }
            return if (last >= 0) last + 1 else 0
        }
        val closest = entries.minByOrNull { (_, ex) ->
            val bounds = dragState.itemBounds[ex.id]
            val center = bounds?.let { (it.first + it.second) / 2f } ?: dropY
            kotlin.math.abs(dropY - center)
        } ?: return entries.last().index + 1
        val bounds = dragState.itemBounds[closest.value.id]
        val center = bounds?.let { (it.first + it.second) / 2f } ?: dropY
        return if (dropY >= center) closest.index + 1 else closest.index
    }

    val dragModifier: (Long, String, String, () -> Offset, () -> Unit) -> Modifier = { id, name, section, offset, start ->
        Modifier.exerciseDrag(dragState, id, name, section, offset, allExercises, selectedExercises, sections, ::findInsertIndexForDrop, start)
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
        Box(Modifier.fillMaxSize()) {
            PaperBackground(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .systemBarsPadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✔ Compose your daily line", fontFamily = GaeguBold, fontSize = 24.sp, color = Color.Black)

                    val titleError = showError && title.isBlank()
                    LineTitleAndCategoriesSection(
                        title = title,
                        onTitleChange = { title = it },
                        categoryOptions = categoryOptions,
                        selectedCategories = selectedCategories,
                        onCategoryChange = { selectedCategories.clear(); selectedCategories.addAll(it) },
                        muscleOptions = muscleOptions,
                        selectedMuscles = selectedMuscles,
                        onMuscleChange = { selectedMuscles.clear(); selectedMuscles.addAll(it) },
                        titleError = titleError
                    )

                    LineNotesSection(note = note, onNoteChange = { note = it })

                    PoeticDivider(centerText = "Which movements do you want to add?")
                    val showExerciseSheet = remember { mutableStateOf(false) }
                    GaeguButton(text = "➕ Add Exercise", onClick = { showExerciseSheet.value = true }, textColor = Color.Black)
                    ExercisePickerSheet(
                        visible = showExerciseSheet.value,
                        allExercises = allExercises,
                        selectedMuscles = selectedMuscles,
                        dragState = dragState,
                        dragModifier = dragModifier,
                        onExerciseClicked = { ex ->
                            if (selectedExercises.none { it.id == ex.id }) {
                                selectedExercises.add(LineExercise(id = ex.id, name = ex.name, sets = 3, repsOrDuration = "10"))
                            }
                            showExerciseSheet.value = false
                        },
                        onCreateExercise = { name ->
                            val encoded = Uri.encode(name)
                            navController.navigate("movement_editor?name=$encoded")
                        },
                        onDismiss = { showExerciseSheet.value = false }
                    )

                    val exerciseBorderColor by animateColorAsState(
                        if (showError && selectedExercises.isEmpty()) Color.Red else Color.Transparent
                    )
                    Box(
                        Modifier
                            .border(2.dp, exerciseBorderColor)
                            .bringIntoViewRequester(exerciseBringIntoView)
                    ) {
                        SectionsWithDragDrop(
                            sections = sections,
                            selectedExercises = selectedExercises,
                            supersetHelper = supersetHelper,
                            supersetSelection = supersetSelection,
                            dragState = dragState,
                            allExercises = allExercises,
                            dragModifier = dragModifier,
                            findInsertIndexForDrop = ::findInsertIndexForDrop
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
                }
            }

            if (showError) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xAA000000)),
                    contentAlignment = Alignment.TopCenter
                ) {
                    PoeticCard(modifier = Modifier.padding(top = 48.dp)) {
                        Text(
                            "Diese Seite ist noch unvollständig.",
                            fontFamily = GaeguRegular,
                            color = Color.Black
                        )
                    }
                }
            }

            if (dragState.isDragging && dragState.draggingExerciseId != null) {
                val id = dragState.draggingExerciseId!!
                val lineExercise = selectedExercises.find { it.id == id }
                val previewName = dragState.dragPreview ?: lineExercise?.name ?: allExercises.find { it.id == id }?.name
                previewName?.let { name ->
                    Box(
                        Modifier
                            .absoluteOffset(x = dragState.dragPosition.x.dp, y = dragState.dragPosition.y.dp)
                            .shadow(8.dp)
                            .alpha(0.7f)
                    ) {
                        PoeticCard(tintOverlayAlpha = 0.3f) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
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

