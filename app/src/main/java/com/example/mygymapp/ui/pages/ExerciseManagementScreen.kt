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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.ui.components.ExerciseCardWithHighlight
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch




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

            val inkColor = Color(0xFF1B1B1B)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .drawBehind {
                            val strokeWidth = 2f
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                color = inkColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        },
                    cursorBrush = SolidColor(inkColor),
                    decorationBox = { innerTextField ->
                        if (search.isEmpty()) {
                            Text("Search", fontFamily = GaeguRegular, color = Color.Gray)
                        }
                        innerTextField()
                    }
                )
            }

            Spacer(Modifier.height(12.dp))

            val highlightColor = Color(0xFF5D4037).copy(alpha = 0.2f)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "All",
                    modifier = Modifier
                        .background(
                            if (selectedCategory == null) highlightColor else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedCategory = null }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp)
                )
                categories.forEach { cat ->
                    Text(
                        text = cat.display,
                        modifier = Modifier
                            .background(
                                if (selectedCategory == cat) highlightColor else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp)
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(highlightColor, RoundedCornerShape(4.dp))
                                    .clickable { collapsedStates[muscleGroup] = !collapsed }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (collapsed) "▶ $muscleGroup" else "▼ $muscleGroup",
                                    fontFamily = GaeguBold
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

    }
}
