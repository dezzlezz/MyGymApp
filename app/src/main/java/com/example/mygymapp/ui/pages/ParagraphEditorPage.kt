package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.viewmodel.LineViewModel

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
    var note by remember { mutableStateOf(initial?.note ?: "") }

    val lineViewModel: LineViewModel = viewModel()
    val lines by lineViewModel.lines.collectAsState()
    val selectedLineIds = remember {
        mutableStateListOf<Long?>().apply { repeat(7) { add(null) } }
    }

    LaunchedEffect(lines) {
        if (initial != null && selectedLineIds.all { it == null }) {
            initial.lineTitles.forEachIndexed { idx, title ->
                selectedLineIds[idx] = lines.find { it.title == title }?.id
            }
        }
    }

    var moodExpanded by remember { mutableStateOf(false) }
    val moods = listOf("calm", "alert", "connected", "alive", "empty", "carried", "searching")
    val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

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
                label = { Text("Title", fontFamily = GaeguRegular, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black),
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
                    label = { Text("Mood", fontFamily = GaeguRegular, color = Color.Black) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(moodExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black)
                )
                ExposedDropdownMenu(
                    expanded = moodExpanded,
                    onDismissRequest = { moodExpanded = false }
                ) {
                    moods.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = GaeguRegular, color = Color.Black) },
                            onClick = {
                                mood = option
                                moodExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            dayNames.forEachIndexed { index, day ->
                var expanded by remember { mutableStateOf(false) }
                val selectedTitle = lines.find { it.id == selectedLineIds[index] }?.title ?: ""
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedTitle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(day, fontFamily = GaeguRegular, color = Color.Black) },
                        placeholder = { Text("Select line for $day", fontFamily = GaeguRegular, color = Color.DarkGray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        lines.forEach { line ->
                            DropdownMenuItem(
                                text = { Text(line.title, fontFamily = GaeguRegular, color = Color.Black) },
                                onClick = {
                                    selectedLineIds[index] = line.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = tagsText,
                onValueChange = { tagsText = it },
                label = { Text("Tags (comma separated)", fontFamily = GaeguRegular, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note", fontFamily = GaeguRegular, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onCancel) { Text("Cancel", fontFamily = GaeguRegular, color = Color.Black) }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val tags = tagsText.split(',').map { it.trim() }.filter { it.isNotBlank() }
                    val selectedLineTitles = selectedLineIds.map { id ->
                        lines.find { it.id == id }?.title ?: ""
                    }
                    val paragraph = Paragraph(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        mood = mood,
                        tags = tags,
                        lineTitles = selectedLineTitles,
                        note = note
                    )
                    onSave(paragraph)
                }) {
                    Text("Save", fontFamily = GaeguRegular, color = Color.Black)
                }
            }
        }
    }
}
