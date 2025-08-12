package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.R
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.FilterChips
import com.example.mygymapp.ui.components.GaeguButton
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticDivider
import com.example.mygymapp.ui.components.PoeticMultiSelectChips
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun LinesPage(
    lines: List<Line>,
    onAdd: () -> Unit,
    onEdit: (Line) -> Unit,
    onArchive: (Line) -> Unit,
    onManageExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val selectedMuscles = remember { mutableStateListOf<String>() }

    val query = search.trim().lowercase()
    val filtered = lines.filter { line ->
        val matchesCategory = selectedCategory == null || line.category == selectedCategory
        val matchesMuscle = selectedMuscles.isEmpty() || selectedMuscles.contains(line.muscleGroup)
        val matchesSearch = line.title.lowercase().contains(query)
        matchesCategory && matchesMuscle && matchesSearch
    }

    val inkColor = Color(0xFF1B1B1B)

    PaperBackground(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            TextButton(
                onClick = onManageExercises,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(stringResource(R.string.manage_exercises), fontFamily = GaeguRegular, color = Color.Black)
            }

            BasicTextField(
                value = search,
                onValueChange = { search = it },
                textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
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
                        Text("Search lines…", fontFamily = GaeguRegular, color = Color.Gray)
                    }
                    innerTextField()
                }
            )

            Spacer(Modifier.height(8.dp))

            Box(Modifier.padding(horizontal = 24.dp)) {
                FilterChips(
                    items = listOf("Push", "Pull", "Core", "Cardio", "Recovery"),
                    selected = selectedCategory,
                    onSelected = { selectedCategory = it }
                )
            }

            Spacer(Modifier.height(8.dp))

            PoeticMultiSelectChips(
                options = listOf("Back", "Legs", "Core", "Shoulders", "Chest", "Arms", "Full Body"),
                selectedItems = selectedMuscles,
                onSelectionChange = { selections ->
                    selectedMuscles.clear()
                    selectedMuscles.addAll(selections)
                },
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            PoeticDivider(centerText = "Your saved lines")

            if (filtered.isEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No lines found.",
                        style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp),
                        color = Color.Black
                    )
                    Spacer(Modifier.height(12.dp))
                    GaeguButton(
                        text = stringResource(R.string.compose_new_line_button),
                        onClick = onAdd,
                        textColor = Color.Black
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                ) {
                    items(filtered) { line ->
                        LineCard(
                            line = line,
                            onEdit = { onEdit(line) },
                            onAdd = onAdd,
                            onArchive = { onArchive(line) },
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 24.dp)
                        )
                    }
                }
            }
        }
    }
}
