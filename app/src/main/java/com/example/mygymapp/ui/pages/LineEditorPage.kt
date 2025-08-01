package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.PaperBackground

@Composable
fun LineEditorPage(
    initial: Line? = null,
    onSave: (Line) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }
    var muscleGroup by remember { mutableStateOf(initial?.muscleGroup ?: "") }
    var mood by remember { mutableStateOf(initial?.mood ?: "") }
    var note by remember { mutableStateOf(initial?.note ?: "") }

    PaperBackground(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "✏️ Edit Line",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily.Serif
            )

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") })
            OutlinedTextField(value = muscleGroup, onValueChange = { muscleGroup = it }, label = { Text("Muscle Group") })
            OutlinedTextField(value = mood, onValueChange = { mood = it }, label = { Text("Mood") })
            OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Note") })

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val newLine = Line(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        category = category,
                        muscleGroup = muscleGroup,
                        mood = mood,
                        exercises = initial?.exercises ?: emptyList(),
                        supersets = initial?.supersets ?: emptyList(),
                        note = note,
                        isArchived = false
                    )
                    onSave(newLine)
                }) {
                    Text("Save")
                }
            }
        }
    }
}

