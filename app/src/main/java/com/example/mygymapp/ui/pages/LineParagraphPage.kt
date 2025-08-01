package com.example.mygymapp.ui.pages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.ParagraphCard
import com.example.mygymapp.viewmodel.ParagraphViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LineParagraphPage(
    paragraphViewModel: ParagraphViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Lines", "Paragraphs")
    val paragraphs by paragraphViewModel.paragraphs.collectAsState()
    var lines by remember { mutableStateOf(sampleLines()) }

    PaperBackground(modifier = modifier.fillMaxSize()) {
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
                    0 -> LinesList(lines)
                    else -> ParagraphList(paragraphs, paragraphViewModel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        lines = lines + sampleLine(lines.size.toLong() + 1)
                    } else {
                        paragraphViewModel.addParagraph(sampleParagraph())
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (selectedTab == 0) "➕ Add Line" else "➕ Add Paragraph",
                    fontFamily = FontFamily.Serif
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LinesList(lines: List<Line>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(lines) { line ->
            LineCard(
                line = line,
                onEdit = {},
                onAdd = {},
                onArchive = {},
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun ParagraphList(paragraphs: List<Paragraph>, vm: ParagraphViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(paragraphs) { paragraph ->
            ParagraphCard(
                paragraph = paragraph,
                onEdit = { vm.editParagraph(paragraph) },
                onPlan = {},
                onSaveTemplate = {},
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

private fun sampleLines(): List<Line> = listOf(
    Line(
        id = 1,
        title = "Silent Force",
        category = "Push",
        muscleGroup = "Core",
        mood = "balanced",
        exercises = emptyList(),
        supersets = emptyList(),
        note = "Felt steady and grounded."
    ),
    Line(
        id = 2,
        title = "Night Owl Session",
        category = "Pull",
        muscleGroup = "Back",
        mood = "alert",
        exercises = emptyList(),
        supersets = emptyList(),
        note = "Late session with high focus."
    )
)

private fun sampleLine(id: Long): Line = Line(
    id = id,
    title = "New Line $id",
    category = "Push",
    muscleGroup = "Full",
    mood = "calm",
    exercises = emptyList(),
    supersets = emptyList(),
    note = ""
)

private fun sampleParagraph(): Paragraph = Paragraph(
    id = System.currentTimeMillis(),
    title = "Weekly Notes",
    mood = "reflective",
    tags = emptyList(),
    lineTitles = List(7) { "Line ${it + 1}" }
)

