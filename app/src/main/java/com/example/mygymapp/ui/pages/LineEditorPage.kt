package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.border
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.components.GaeguButton
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticDivider
import com.example.mygymapp.ui.components.WaxSealButton
import com.example.mygymapp.ui.components.PoeticCard
import com.example.mygymapp.R
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.viewmodel.LineEditorViewModel
import com.example.mygymapp.ui.theme.AppTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LineEditorPage(
    initial: Line? = null,
    onSave: (Line) -> Unit,
    onCancel: () -> Unit,
    onCreateExercise: (String) -> Unit
) {
    val vm: ExerciseViewModel = viewModel()
    val allExercises by vm.allExercises.observeAsState(emptyList())

    val editorVm: LineEditorViewModel = viewModel(factory = LineEditorViewModel.Factory(initial))
    val title by editorVm.title.collectAsState()
    val note by editorVm.note.collectAsState()
    val selectedExercises = editorVm.selectedExercises
    val supersetState = editorVm.supersetState
    val selectedCategories = editorVm.selectedCategories
    val selectedMuscles = editorVm.selectedMuscles
    val sections by editorVm.sections

    val categoryOptions = listOf("💪 Strength", "🔥 Cardio", "🌱 Warmup", "🧘 Flexibility", "🌈 Recovery")
    val muscleOptions = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Arms", "Full Body")

    var showError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val dragState = remember { DragAndDropState() }
    val scope = rememberCoroutineScope()
    // Single scroll and bring-into-view instances for validation and layout animation
    val listState = rememberLazyListState()
    val titleBringIntoViewRequester = remember { BringIntoViewRequester() }
    val exerciseBringIntoViewRequester = remember { BringIntoViewRequester() }
    val noteBringIntoViewRequester = remember { BringIntoViewRequester() }
    val errorMessage = stringResource(R.string.add_title_and_movement_error)

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

    val dragModifier: @Composable (Long, String, String, () -> Offset, () -> Unit) -> Modifier =
        { id, name, section, offset, start ->
            Modifier.exerciseDrag(
                state = dragState,
                exerciseId = id,
                exerciseName = name,
                startSection = section,
                getStartOffset = offset,
                allExercises = allExercises,
                selectedExercises = selectedExercises,
                supersetState = supersetState,
                findInsertIndex = ::findInsertIndexForDrop,
                onStart = start
            )
        }

    var saving by remember { mutableStateOf(false) }
    var pendingLine by remember { mutableStateOf<Line?>(null) }
    val saveOffset = remember { Animatable(0f) }
    val saveAlpha = remember { Animatable(1f) }

    LaunchedEffect(saving) {
        val line = pendingLine
        if (saving && line != null) {
            saveOffset.animateTo(300f, tween(600))
            saveAlpha.animateTo(0f, tween(600))
            onSave(line)
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
        Box(Modifier.fillMaxSize()) {
            PaperBackground(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                val showExerciseSheet = remember { mutableStateOf(false) }
                var exerciseShakeTrigger by remember { mutableStateOf(false) }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .padding(24.dp)
                        .graphicsLayer {
                            translationX = saveOffset.value
                            alpha = saveAlpha.value
                        },
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item(key = "header") {
                        Text(
                            text = stringResource(R.string.compose_daily_line),
                            style = AppTypography.Title,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    item(key = "details") {
                        val titleError = showError && title.isBlank()
                        LineTitleAndCategoriesSection(
                            title = title,
                            onTitleChange = { editorVm.title.value = it },
                            categoryOptions = categoryOptions,
                            selectedCategories = selectedCategories,
                            onCategoryChange = { selectedCategories.clear(); selectedCategories.addAll(it) },
                            muscleOptions = muscleOptions,
                            selectedMuscles = selectedMuscles,
                            onMuscleChange = { selectedMuscles.clear(); selectedMuscles.addAll(it) },
                            titleError = titleError,
                            titleBringIntoViewRequester = titleBringIntoViewRequester
                        )
                    }
                    item(key = "notes") {
                        LineNotesSection(
                            note = note,
                            onNoteChange = { editorVm.note.value = it },
                            noteBringIntoViewRequester = noteBringIntoViewRequester
                        )
                    }
                    stickyHeader {
                        PoeticDivider(centerText = stringResource(R.string.movements_prompt))
                    }
                    item(key = "picker") {
                        GaeguButton(text = stringResource(R.string.add_exercise_button), onClick = { showExerciseSheet.value = true }, textColor = Color.Black)
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
                            onCreateExercise = onCreateExercise,
                            onDismiss = { showExerciseSheet.value = false }
                        )
                    }
                    item(key = "exercise_list") {
                        val exerciseError = showError && selectedExercises.isEmpty()
                        val exerciseShake by animateFloatAsState(
                            if (exerciseShakeTrigger) 8f else 0f,
                            animationSpec = keyframes {
                                durationMillis = 300
                                0f at 0
                                -8f at 50
                                8f at 100
                                -8f at 150
                                0f at 200
                            }
                        )
                        LaunchedEffect(exerciseError) {
                            if (exerciseError) exerciseShakeTrigger = !exerciseShakeTrigger
                        }
                        val exerciseBorderColor by animateColorAsState(
                            if (exerciseError) Color.Red else Color.Transparent
                        )
                        Box(
                            Modifier
                                .border(2.dp, exerciseBorderColor)
                                .graphicsLayer { translationX = exerciseShake }
                                .bringIntoViewRequester(exerciseBringIntoViewRequester)
                        ) {
                            SectionsWithDragDrop(
                                sections = sections,
                                selectedExercises = selectedExercises,
                                supersetState = supersetState,
                                dragState = dragState,
                                allExercises = allExercises,
                                dragModifier = dragModifier,
                                findInsertIndexForDrop = ::findInsertIndexForDrop,
                                snackbarHostState = snackbarHostState
                            )
                        }
                    }
                    item(key = "divider_end") { PoeticDivider() }
                      item(key = "actions") {
                          val canSave = title.isNotBlank() && selectedExercises.isNotEmpty()
                          val cancelLabel = stringResource(R.string.cancel)
                          Box(modifier = Modifier.fillMaxWidth()) {
                              GaeguButton(
                                  text = cancelLabel,
                                  onClick = onCancel,
                                  textColor = Color.Black,
                                  modifier = Modifier
                                      .align(Alignment.CenterStart)
                                      .semantics { contentDescription = cancelLabel }
                              )
                              WaxSealButton(
                                  label = stringResource(R.string.create_line),
                                  onClick = {
                                    if (!canSave) {
                                        showError = true
                                        scope.launch {
                                            if (title.isBlank()) {
                                                titleBringIntoViewRequester.bringIntoView()
                                            } else {
                                                exerciseBringIntoViewRequester.bringIntoView()
                                            }
                                            snackbarHostState.showSnackbar(errorMessage)
                                        }
                                        return@WaxSealButton
                                    }
                                    pendingLine = editorVm.buildLine()
                                    saving = true
                                },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .alpha(if (canSave) 1f else 0.5f)
                            )
                        }
                    }
                }
            }

            if (dragState.isDragging && dragState.draggingExerciseId != null) {
                val density = LocalDensity.current
                val id = dragState.draggingExerciseId!!
                val lineExercise = selectedExercises.find { it.id == id }
                val previewName = dragState.dragPreview ?: lineExercise?.name ?: allExercises.find { it.id == id }?.name
                previewName?.let { name ->
                    Box(
                        Modifier
                            .absoluteOffset(
                                x = with(density) { dragState.dragPosition.x.toDp() },
                                y = with(density) { dragState.dragPosition.y.toDp() }
                            )
                            .shadow(8.dp)
                            .alpha(0.85f)
                            .graphicsLayer { rotationZ = -3f }
                    ) {
                        PoeticCard(tintOverlayAlpha = 0.3f) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(name, fontFamily = AppTypography.GaeguRegular, fontSize = 16.sp, color = Color.Black)
                                lineExercise?.let {
                                    Text(
                                        stringResource(R.string.sets_reps_format, it.sets, it.repsOrDuration),
                                        fontFamily = AppTypography.GaeguRegular,
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

