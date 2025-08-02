package com.example.mygymapp.ui.pages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.model.PlannedParagraph
import com.example.mygymapp.ui.pages.LinesPage
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.pages.ParagraphsPage
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import com.example.mygymapp.viewmodel.ParagraphViewModel
import com.example.mygymapp.viewmodel.LineViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineParagraphPage(
    navController: NavController,
    paragraphViewModel: ParagraphViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Lines", "Paragraphs")
    val paragraphs by paragraphViewModel.paragraphs.collectAsState()
    val templates by paragraphViewModel.templates.collectAsState()
    val planned by paragraphViewModel.planned.collectAsState()
    val lineViewModel: LineViewModel = viewModel()
    val lines by lineViewModel.lines.collectAsState()
    var editingParagraph by remember { mutableStateOf<Paragraph?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var editingLine by remember { mutableStateOf<Line?>(null) }
    var showLineEditor by remember { mutableStateOf(false) }
    var planTarget by remember { mutableStateOf<Paragraph?>(null) }
    var showTemplateChooser by remember { mutableStateOf(false) }

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
                        text = { Text(title, fontFamily = FontFamily.Serif) }
                    )
                }
            }

            Crossfade(targetState = selectedTab, label = "tab") { tab ->
                when (tab) {
                    0 -> LinesPage(
                        lines = lines.filter { !it.isArchived },
                        onAdd = {
                            editingLine = null
                            showLineEditor = true
                        },
                        onEdit = {
                            editingLine = it
                            showLineEditor = true
                        },
                        onArchive = { lineViewModel.archive(it.id) },
                        onManageExercises = { navController.navigate("exercise_management") }
                    )
                    else -> ParagraphsPage(
                        paragraphs = paragraphs,
                        planned = planned,
                        onEdit = { paragraph ->
                            editingParagraph = paragraph
                            showEditor = true
                        },
                        onPlan = { planTarget = it },
                        onSaveTemplate = { paragraphViewModel.saveTemplate(it) }
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
                        if (templates.isNotEmpty()) {
                            showTemplateChooser = true
                        } else {
                            editingParagraph = null
                            showEditor = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (selectedTab == 0) "➕ Write a new line" else "➕ Add Paragraph",
                    fontFamily = FontFamily.Serif
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showEditor) {
        ParagraphEditorPage(
            initial = editingParagraph,
            onSave = { paragraph ->
                if (editingParagraph == null) {
                    paragraphViewModel.addParagraph(paragraph)
                } else {
                    paragraphViewModel.editParagraph(paragraph)
                }
                showEditor = false
            },
            onCancel = { showEditor = false }
        )
    }

    if (showLineEditor) {
        LineEditorPage(
            initial = editingLine,
            onSave = { line ->
                if (editingLine == null) lineViewModel.add(line)
                else lineViewModel.update(line)
                showLineEditor = false
            },
            onCancel = { showLineEditor = false }
        )
    }

    planTarget?.let { target ->
        val dateState = rememberDatePickerState()
        ModalBottomSheet(onDismissRequest = { planTarget = null }) {
            Column(Modifier.padding(16.dp)) {
                DatePicker(state = dateState)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { planTarget = null }) {
                        Text("Cancel", fontFamily = FontFamily.Serif)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        val millis = dateState.selectedDateMillis ?: return@Button
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        paragraphViewModel.planParagraph(target, date)
                        planTarget = null
                    }) {
                        Text("Plan", fontFamily = FontFamily.Serif)
                    }
                }
            }
        }
    }

    if (showTemplateChooser) {
        ModalBottomSheet(onDismissRequest = { showTemplateChooser = false }) {
            Column(Modifier.padding(16.dp)) {
                Text("Choose Template", fontFamily = FontFamily.Serif, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                templates.forEach { template ->
                    TextButton(onClick = {
                        editingParagraph = template
                        showTemplateChooser = false
                        showEditor = true
                    }) { Text(template.title, fontFamily = FontFamily.Serif) }
                }
                TextButton(onClick = {
                    editingParagraph = null
                    showTemplateChooser = false
                    showEditor = true
                }) { Text("Blank", fontFamily = FontFamily.Serif) }
            }
        }
    }
}

