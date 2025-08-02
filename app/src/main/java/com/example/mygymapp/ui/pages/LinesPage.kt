package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.LineCard

@Composable
fun LinesPage(
    lines: List<Line>,
    onAdd: () -> Unit,
    onEdit: (Line) -> Unit,
    onArchive: (Line) -> Unit,
    onManageExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        TextButton(
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text("\u2795 Write a new line", fontFamily = GaeguRegular)
        }
        TextButton(
            onClick = onManageExercises,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("\u2699\uFE0F Manage Exercises", fontFamily = GaeguRegular)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lines) { line ->
                LineCard(
                    line = line,
                    onEdit = { onEdit(line) },
                    onAdd = { onAdd() },
                    onArchive = { onArchive(line) }
                )
            }
        }
    }
}
