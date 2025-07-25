package com.example.mygymapp.ui.pages

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.ui.components.AddEditExerciseSheet
import com.example.mygymapp.ui.components.ExerciseCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.viewmodel.ExerciseViewModel

@Composable
fun ExerciseManagementScreen(navController: NavController) {
    val vm: ExerciseViewModel = viewModel()
    val exercises by vm.allExercises.observeAsState(emptyList())

    var editing by remember { mutableStateOf<Exercise?>(null) }
    var showSheet by remember { mutableStateOf(false) }

    val categories = ExerciseCategory.values().map { it.display }
    val muscles = MuscleGroup.values().map { it.display }

    PaperBackground(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Exercise Library",
                    style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif)
                )
                TextButton(onClick = { editing = null; showSheet = true }) {
                    Text("Add", fontFamily = FontFamily.Serif)
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(exercises) { ex ->
                    ExerciseCard(
                        ex = ex,
                        onClick = {
                            editing = ex
                            showSheet = true
                        },
                        onToggleFavorite = { vm.toggleFavorite(ex) }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            editing = ex
                            showSheet = true
                        }) { Text("Edit", fontFamily = FontFamily.Serif) }
                        TextButton(onClick = { vm.delete(ex.id) }) {
                            Text("Delete", fontFamily = FontFamily.Serif)
                        }
                    }
                }
            }
        }
    }

    if (showSheet) {
        AddEditExerciseSheet(
            initialName = editing?.name ?: "",
            initialCategory = editing?.category?.display ?: "",
            initialMuscleGroup = editing?.muscleGroup?.display ?: "",
            initialRating = editing?.likeability ?: 3,
            initialImageUri = editing?.imageUri?.let { Uri.parse(it) },
            onSave = { name, cat, group, rating, uri ->
                val category = ExerciseCategory.values().find { it.display == cat } ?: ExerciseCategory.Calisthenics
                val muscleGroup = MuscleGroup.values().find { it.display == group } ?: MuscleGroup.Core
                val exercise = Exercise(
                    id = editing?.id ?: 0,
                    name = name,
                    description = "",
                    category = category,
                    likeability = rating,
                    muscleGroup = muscleGroup,
                    muscle = muscleGroup.display,
                    imageUri = uri?.toString(),
                    isFavorite = editing?.isFavorite ?: false
                )
                if (editing == null) vm.insert(exercise) else vm.update(exercise)
                showSheet = false
            },
            onCancel = { showSheet = false },
            categories = categories,
            muscleGroups = muscles
        )
    }
}
