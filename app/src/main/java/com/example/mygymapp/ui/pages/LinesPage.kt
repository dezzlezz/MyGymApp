package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.R

@Composable
fun LinesPage(
    lines: List<Line>,
    onAdd: () -> Unit,
    onEdit: (Line) -> Unit,
    onArchive: (Line) -> Unit,
    onManageExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    PaperBackground(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            TextButton(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(stringResource(R.string.write_new_line), fontFamily = GaeguRegular, color = Color.Black)
            }
            TextButton(
                onClick = onManageExercises,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.manage_exercises), fontFamily = GaeguRegular, color = Color.Black)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
            ) {
                items(lines) { line ->
                    LineCard(
                        line = line,
                        onEdit = { onEdit(line) },
                        onAdd = { onAdd() },
                        onArchive = { onArchive(line) },
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}