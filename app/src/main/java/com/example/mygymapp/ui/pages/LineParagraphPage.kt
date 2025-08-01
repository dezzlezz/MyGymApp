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

@Composable
fun LineParagraphPage(
    lines: List<Line>,
    paragraphs: List<Paragraph>,
    onAddLine: () -> Unit,
    onAddParagraph: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Lines", "Paragraphs")

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
                    else -> ParagraphList(paragraphs)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = if (selectedTab == 0) onAddLine else onAddParagraph,
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
private fun ParagraphList(paragraphs: List<Paragraph>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(paragraphs) { paragraph ->
            ParagraphCard(
                paragraph = paragraph,
                onEdit = {},
                onPlan = {},
                onSaveTemplate = {},
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

