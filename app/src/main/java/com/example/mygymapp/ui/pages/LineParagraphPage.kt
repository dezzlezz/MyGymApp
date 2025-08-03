package com.example.mygymapp.ui.pages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.store.JournalStore
import com.example.mygymapp.ui.components.PaperBackground

@Composable
fun LineParagraphPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    startTab: Int = 0
) {
    var selectedTab by remember { mutableStateOf(startTab) }
    val tabs = listOf("Lines", "Paragraphs")
    val lines = JournalStore.allLines
    val paragraphs = JournalStore.allParagraphs
    var editingLine by remember { mutableStateOf<Line?>(null) }
    var showLineEditor by remember { mutableStateOf(false) }

    PaperBackground(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontFamily = GaeguRegular, color = Color.Black) }
                    )
                }
            }

            Crossfade(targetState = selectedTab, label = "tab") { tab ->
                when (tab) {
                    0 -> LinesPage(
                        lines = lines,
                        onEdit = {
                            editingLine = it
                            showLineEditor = true
                        },
                        onArchive = { lines.remove(it) },
                        onAdd = {
                            editingLine = null
                            showLineEditor = true
                        },
                        onManageExercises = { navController.navigate("exercise_management") }
                    )
                    else -> ParagraphsPage(
                        paragraphs = paragraphs,
                        archived = emptyList(),
                        planned = emptyList(),
                        onEdit = { paragraph ->
                            navController.navigate("paragraph_editor?id=${paragraph.id}")
                        },
                        onPlan = {},
                        onSaveTemplate = {},
                        onArchive = { paragraphs.remove(it) },
                        onAdd = { navController.navigate("paragraph_editor") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        editingLine = null
                        showLineEditor = true
                    } else {
                        navController.navigate("paragraph_editor")
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (selectedTab == 0) "➕ Compose a new line" else "➕ Add Paragraph",
                    fontFamily = GaeguRegular,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showLineEditor) {
        LineEditorPage(
            initial = editingLine,
            onSave = { line ->
                val index = lines.indexOfFirst { it.id == line.id }
                if (index >= 0) lines[index] = line else lines.add(line)
                showLineEditor = false
            },
            onCancel = { showLineEditor = false }
        )
    }
}
