package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Line

@Composable
fun LinesPage(
    lines: List<Line>,
    onEdit: (Line) -> Unit,
    onArchive: (Line) -> Unit,
    onManageExercises: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            line.title,
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 22.sp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${line.category} · ${line.muscleGroup} · ${line.mood}",
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp)
                        )
                        Spacer(Modifier.height(4.dp))
                        val supersetInfo = if (line.supersets.isNotEmpty()) " • ${line.supersets.size} supersets" else ""
                        Text(
                            "${line.exercises.size} exercises$supersetInfo",
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp)
                        )
                        if (line.note.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "\uD83D\uDCCC ${line.note}",
                                style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TextButton(onClick = { onEdit(line) }) {
                                Text("\u270F\uFE0F Edit", fontFamily = GaeguRegular)
                            }
                            TextButton(onClick = { onArchive(line) }) {
                                Text("\uD83D\uDCC3 Archive", fontFamily = GaeguRegular)
                            }
                        }
                    }
                }
            }
        }
    }
}
