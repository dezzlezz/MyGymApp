package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.components.PaperBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParagraphEditorPage(
    initial: Paragraph?,
    onSave: (Paragraph) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var mood by remember { mutableStateOf(initial?.mood ?: "") }
    var tagsText by remember { mutableStateOf(initial?.tags?.joinToString(", ") ?: "") }
    var lineTitles by remember { mutableStateOf(initial?.lineTitles ?: List(7) { "" }) }
    var note by remember { mutableStateOf(initial?.note ?: "") }

    var moodExpanded by remember { mutableStateOf(false) }
    val moods = listOf("calm", "alert", "connected", "alive", "empty", "carried", "searching")

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", fontFamily = GaeguRegular) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = moodExpanded,
                onExpandedChange = { moodExpanded = !moodExpanded }
            ) {
                OutlinedTextField(
                    value = mood,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mood", fontFamily = GaeguRegular) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(moodExpanded) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular)
                )
                ExposedDropdownMenu(
                    expanded = moodExpanded,
                    onDismissRequest = { moodExpanded = false }
                ) {
                    moods.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = GaeguRegular) },
                            onClick = {
                                mood = option
                                moodExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            lineTitles.forEachIndexed { index, value ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { newValue ->
                        lineTitles = lineTitles.toMutableList().also { it[index] = newValue }
                    },
                    label = { Text("Day ${index + 1}", fontFamily = GaeguRegular) },
                    textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            OutlinedTextField(
                value = tagsText,
                onValueChange = { tagsText = it },
                label = { Text("Tags (comma separated)", fontFamily = GaeguRegular) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note", fontFamily = GaeguRegular) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Cancel", fontFamily = GaeguRegular) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val tags = tagsText.split(',').map { it.trim() }.filter { it.isNotBlank() }
                    val paragraph = Paragraph(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        mood = mood,
                        tags = tags,
                        lineTitles = lineTitles,
                        note = note
                    )
                    onSave(paragraph)
                }) {
                    Text("Save", fontFamily = GaeguRegular)
                }
            }
        }
    }
}
