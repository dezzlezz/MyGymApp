package com.example.mygymapp.ui.pages

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.ui.components.ExerciseCardWithHighlight
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.ui.platform.LocalContext
import com.example.mygymapp.data.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull




@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseManagementScreen(navController: NavController) {
    val vm: ExerciseViewModel = viewModel()
    val exercises by vm.allExercises.observeAsState(emptyList())

    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExerciseCategory?>(null) }
    val rawQuery = search.trim().lowercase().replace("\\s+".toRegex(), "")

    val categories = ExerciseCategory.values()
    val isSearching = rawQuery.isNotEmpty()

    val filtered = exercises.filter {
        (selectedCategory == null || it.category == selectedCategory) &&
                it.name.lowercase().replace("\\s+".toRegex(), "").contains(rawQuery)
    }

    val grouped = if (!isSearching) filtered.groupBy { it.muscleGroup.display } else emptyMap()
    val collapsedStates = remember { mutableStateMapOf<String, Boolean>() }


    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun resetExercises() {
        scope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(context).exerciseDao()
            val input = context.assets.open("default_exercises.json")
            val json = input.bufferedReader().use { it.readText() }

            val gson = Gson()
            val type = object : TypeToken<List<Exercise>>() {}.type
            val exercises: List<Exercise> = gson.fromJson(json, type)

            dao.getAllExercises().firstOrNull()?.forEach {
                dao.deleteById(it.id)
            }

            exercises.forEach {
                dao.insert(it.copy(id = 0L, isFavorite = false))
            }
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(bottom = 72.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.background_parchment),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Exercise Library",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = GaeguBold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search", fontFamily = GaeguRegular) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All", fontFamily = GaeguRegular) }
                )
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.display, fontFamily = GaeguRegular) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Text(
                            "No exercises found.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = GaeguRegular),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else if (isSearching) {
                    items(filtered) { ex ->
                        ExerciseCardWithHighlight(
                            ex = ex,
                            query = rawQuery,
                            onEdit = { navController.navigate("exercise_editor?editId=${ex.id}") },
                            onDelete = { vm.delete(ex.id) },
                            onToggleFavorite = { vm.toggleFavorite(ex) }
                        )
                    }
                } else {
                    grouped.forEach { (muscleGroup, list) ->
                        val collapsed = collapsedStates[muscleGroup] ?: true
                        item {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { collapsedStates[muscleGroup] = !collapsed },
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 2.dp
                            ) {
                                Text(
                                    text = if (collapsed) "▶ $muscleGroup" else "▼ $muscleGroup",
                                    fontFamily = GaeguBold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                        if (!collapsed) {
                            items(list) { ex ->
                                ExerciseCardWithHighlight(
                                    ex = ex,
                                    query = "",
                                    onEdit = { navController.navigate("exercise_editor?editId=${ex.id}") },
                                    onDelete = { vm.delete(ex.id) },
                                    onToggleFavorite = { vm.toggleFavorite(ex) }
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("movement_editor") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F4E3A))
        ) {
            Text("➕ Add", fontFamily = GaeguBold, color = Color.White)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { resetExercises() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text("♻ Reset Exercises", color = Color.White)
        }


    }
}
