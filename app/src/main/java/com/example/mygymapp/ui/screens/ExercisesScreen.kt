package com.example.mygymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.widgets.StarRating
import com.example.mygymapp.ui.theme.FogGray
import com.example.mygymapp.ui.theme.PineGreen
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.ui.components.SearchFilterBar
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

private enum class SortOption(val labelRes: Int, val comparator: Comparator<Exercise>) {
    NAME(R.string.sort_name, compareBy { it.name.lowercase() }),
    DIFFICULTY(R.string.sort_difficulty, compareBy { it.likeability }),
    MUSCLE(R.string.sort_muscle_group, compareBy { it.muscleGroup.display })
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun ExercisesScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel(),
    onEditExercise: (Long) -> Unit = {}
) {
    val exercises by viewModel.allExercises.observeAsState(emptyList())

    var query by rememberSaveable { mutableStateOf("") }
    var showFavorites by rememberSaveable { mutableStateOf(false) }
    var selectedCategory by rememberSaveable { mutableStateOf<ExerciseCategory?>(null) }
    var selectedGroup by rememberSaveable { mutableStateOf<MuscleGroup?>(null) }
    var sortExpanded by rememberSaveable { mutableStateOf(false) }
    var sortOption by rememberSaveable { mutableStateOf(SortOption.NAME) }

    val filtered = exercises
        .asSequence()
        .filter { if (showFavorites) it.isFavorite else true }
        .filter { selectedCategory?.let { cat -> it.category == cat } ?: true }
        .filter { selectedGroup?.let { grp -> it.muscleGroup == grp } ?: true }
        .filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
        .sortedWith(sortOption.comparator)
        .toList()
    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addExercise") }) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        if (exercises.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No exercises yet!", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            Column(Modifier.padding(paddingValues)) {
                SearchFilterBar(
                    query = query,
                    onQueryChange = { query = it },
                    favoritesOnly = showFavorites,
                    onFavoritesToggle = { showFavorites = !showFavorites },
                    placeholderRes = R.string.search_exercises
                )

                FlowRow(modifier = Modifier.padding(horizontal = 8.dp)) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text(stringResource(id = R.string.all)) }
                    )
                    ExerciseCategory.values().forEach { cat ->
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.display) }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                FlowRow(modifier = Modifier.padding(horizontal = 8.dp)) {
                    FilterChip(
                        selected = selectedGroup == null,
                        onClick = { selectedGroup = null },
                        label = { Text(stringResource(id = R.string.all)) }
                    )
                    MuscleGroup.values().forEach { grp ->
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = selectedGroup == grp,
                            onClick = { selectedGroup = grp },
                            label = { Text(grp.display) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(expanded = sortExpanded, onExpandedChange = { sortExpanded = !sortExpanded }) {
                    OutlinedTextField(
                        readOnly = true,
                        value = stringResource(id = sortOption.labelRes),
                        onValueChange = {},
                        label = { Text(stringResource(id = R.string.sort_by)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                        modifier = Modifier.menuAnchor().padding(horizontal = 8.dp).fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = sortExpanded, onDismissRequest = { sortExpanded = false }) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(stringResource(id = option.labelRes)) },
                                onClick = {
                                    sortOption = option
                                    sortExpanded = false
                                }
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered, key = { it.id }) { ex ->
                        ExerciseListItem(
                            ex = ex,
                            onEdit = { onEditExercise(it) },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ExerciseListItem(ex: Exercise, onEdit: (Long) -> Unit, viewModel: ExerciseViewModel) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            when (it) {
                DismissValue.DismissedToEnd -> {
                    viewModel.delete(ex.id)
                    true
                }
                DismissValue.DismissedToStart -> {
                    onEdit(ex.id)
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = if (direction == DismissDirection.StartToEnd) Color.Red else FogGray
            val icon = if (direction == DismissDirection.StartToEnd) Icons.Default.Delete else Icons.Default.Edit
            val alignment = if (direction == DismissDirection.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(icon, contentDescription = null, tint = PineGreen)
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ex.imageUri != null) {
                        AsyncImage(
                            model = ex.imageUri,
                            contentDescription = ex.name,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }

                    Column(Modifier.weight(1f)) {
                        Text(ex.name, style = MaterialTheme.typography.titleMedium)
                        Text("${ex.muscle} • ${ex.category.display}", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(ex) }) {
                        Icon(
                            imageVector = if (ex.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = stringResource(id = if (ex.isFavorite) R.string.favorite_marked else R.string.show_favorites)
                        )
                    }
                    StarRating(rating = ex.likeability)
                }

            }
        }
    )
}

