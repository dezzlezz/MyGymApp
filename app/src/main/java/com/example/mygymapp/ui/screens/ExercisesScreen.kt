package com.example.mygymapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExercisesScreen() {
    // Dummy data with correct enums
    var exercises by remember { mutableStateOf(
        mutableListOf(
            Exercise(1, "Push Up", "Classic push up", ExerciseCategory.Calisthenics, 4, MuscleGroup.Chest, "Chest"),
            Exercise(2, "Squat", "Deep squat", ExerciseCategory.Gym, 5, MuscleGroup.Legs, "Legs"),
        )
    )}
    var showSheet by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }
    var search by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    var filterMuscle by remember { mutableStateOf<MuscleGroup?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercises") },
                actions = {
                    IconButton(onClick = {
                        editingExercise = null
                        showSheet = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Exercise")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingExercise = null
                showSheet = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search by name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            EnumFilterChipBar(
                label = "Category",
                items = ExerciseCategory.entries.toList(),
                selected = filterCategory,
                onSelected = { filterCategory = it }
            )
            EnumFilterChipBar(
                label = "Muscle",
                items = MuscleGroup.entries.toList(),
                selected = filterMuscle,
                onSelected = { filterMuscle = it }
            )
            val filtered = exercises.filter {
                (search.isBlank() || it.name.contains(search, ignoreCase = true))
                        && (filterCategory == null || it.category == filterCategory)
                        && (filterMuscle == null || it.muscleGroup == filterMuscle)
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    filtered,
                    key = { it.id }
                ) { exercise ->
                    val dismissState = rememberDismissState()
                    // Delete action
                    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                        val deleted = exercise
                        exercises = exercises.toMutableList().apply { remove(deleted) }
                        LaunchedEffect(dismissState) {
                            val result = snackbarHostState.showSnackbar(
                                "Deleted \"${deleted.name}\"",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                exercises = exercises.toMutableList().apply { add(deleted) }
                            }
                        }
                    }
                    // Edit action
                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        editingExercise = exercise
                        showSheet = true
                        LaunchedEffect(dismissState) { dismissState.reset() }
                    }
                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                        background = {
                            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                            val color = when (direction) {
                                DismissDirection.StartToEnd -> MaterialTheme.colorScheme.error
                                DismissDirection.EndToStart -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.surface
                            }
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(20.dp),
                                contentAlignment = when (direction) {
                                    DismissDirection.StartToEnd -> Alignment.CenterStart
                                    DismissDirection.EndToStart -> Alignment.CenterEnd
                                    else -> Alignment.Center
                                }
                            ) {
                                Icon(
                                    imageVector = if (direction == DismissDirection.StartToEnd) Icons.Default.Delete else Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onError
                                )
                            }
                        },
                        dismissContent = {
                            ExerciseCard(
                                exercise = exercise,
                                onClick = {
                                    editingExercise = exercise
                                    showSheet = true
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    if (showSheet) {
        AddEditExerciseSheet(
            exercise = editingExercise,
            onSave = { ex ->
                if (editingExercise != null) {
                    exercises = exercises.map {
                        if (it.id == editingExercise!!.id) ex.copy(id = it.id)
                        else it
                    }.toMutableList()
                } else {
                    exercises = (exercises + ex.copy(id = (exercises.maxOfOrNull { it.id } ?: 0) + 1)).toMutableList()
                }
                showSheet = false
                editingExercise = null
            },
            onCancel = {
                showSheet = false
                editingExercise = null
            }
        )
    }
}

// --------- Components for filter chips, add/edit sheet, and cards ----------

@Composable
fun <T : Enum<T>> EnumFilterChipBar(
    label: String,
    items: List<T>,
    selected: T?,
    onSelected: (T?) -> Unit
) {
    Row(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        AssistChip(
            onClick = { onSelected(null) },
            label = { Text("All $label") },
            colors = AssistChipDefaults.assistChipColors()
        )
        Spacer(Modifier.width(8.dp))
        items.forEach { item ->
            AssistChip(
                onClick = { onSelected(item) },
                label = { Text((item as? ExerciseCategory)?.display ?: (item as? MuscleGroup)?.display ?: item.name) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected == item) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface
                )
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExerciseSheet(
    exercise: Exercise?,
    onSave: (Exercise) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(exercise?.name ?: "") }
    var description by remember { mutableStateOf(exercise?.description ?: "") }
    var category by remember { mutableStateOf(exercise?.category ?: ExerciseCategory.Gym) }
    var muscleGroup by remember { mutableStateOf(exercise?.muscleGroup ?: MuscleGroup.Legs) }
    var muscle by remember { mutableStateOf(exercise?.muscle ?: "") }
    var likeability by remember { mutableStateOf(exercise?.likeability ?: 3) }
    var imageUri by remember { mutableStateOf<String?>(exercise?.imageUri) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri?.toString()
    }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(if (exercise == null) "Add Exercise" else "Edit Exercise", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!imageUri.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Exercise image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(8.dp))
                }
                OutlinedButton(onClick = { imagePicker.launch("image/*") }) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = "Select Image")
                    Spacer(Modifier.width(4.dp))
                    Text("Image")
                }
            }
            Spacer(Modifier.height(8.dp))
            CategoryDropdown(category, onCategoryChange = { category = it })
            Spacer(Modifier.height(8.dp))
            MuscleGroupDropdown(muscleGroup, onMuscleGroupChange = { muscleGroup = it })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = muscle,
                onValueChange = { muscle = it },
                label = { Text("Muscle") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text("Likeability:")
            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= likeability) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = "$i Star",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { likeability = i }
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Row {
                Button(
                    onClick = {
                        if (name.isNotBlank() && muscle.isNotBlank()) {
                            onSave(
                                Exercise(
                                    id = exercise?.id ?: 0L,
                                    name = name,
                                    description = description,
                                    category = category,
                                    likeability = likeability,
                                    muscleGroup = muscleGroup,
                                    muscle = muscle,
                                    imageUri = imageUri
                                )
                            )
                        }
                    },
                    enabled = name.isNotBlank() && muscle.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (exercise == null) "Add" else "Save")
                }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
fun CategoryDropdown(selected: ExerciseCategory, onCategoryChange: (ExerciseCategory) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selected.display)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            ExerciseCategory.entries.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.display) },
                    onClick = {
                        onCategoryChange(cat)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MuscleGroupDropdown(selected: MuscleGroup, onMuscleGroupChange: (MuscleGroup) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selected.display)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            MuscleGroup.entries.forEach { mg ->
                DropdownMenuItem(
                    text = { Text(mg.display) },
                    onClick = {
                        onMuscleGroupChange(mg)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            if (!exercise.imageUri.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(exercise.imageUri),
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${exercise.category.display} • ${exercise.muscleGroup.display}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Row {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < exercise.likeability) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
