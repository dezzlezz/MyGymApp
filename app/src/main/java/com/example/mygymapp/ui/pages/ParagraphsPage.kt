package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.model.PlannedParagraph
import com.example.mygymapp.ui.components.PaperBackground


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParagraphEntryCard(
    paragraph: Paragraph,
    onEdit: () -> Unit,
    onPlan: () -> Unit,
    onSaveTemplate: () -> Unit,
    modifier: Modifier = Modifier,
    showButtons: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = paragraph.title,
                style = TextStyle(fontFamily = GaeguBold, fontSize = 22.sp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = paragraph.mood,
                style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp)
            )
            if (paragraph.tags.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    paragraph.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag, fontFamily = GaeguRegular) })
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            val days = listOf(
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"
            )
            paragraph.lineTitles.forEachIndexed { index, title ->
                if (title.isNotBlank()) {
                    Text(
                        text = "${days.getOrNull(index) ?: "Day ${index + 1}"} \u2192 $title",
                        style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp)
                    )
                }
            }
            if (paragraph.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "\uD83D\uDCCC ${paragraph.note}",
                    style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp)
                )
            }
            if (showButtons) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = onEdit) {
                        Text("\u270F\uFE0F Edit", fontFamily = GaeguRegular)
                    }
                    TextButton(onClick = onPlan) {
                        Text("\uD83D\uDCC6 Plan", fontFamily = GaeguRegular)
                    }
                    TextButton(onClick = onSaveTemplate) {
                        Text("\uD83D\uDCCE Save as Template", fontFamily = GaeguRegular)
                    }
                }
            }
        }
    }
}

/**
 * Displays a poetic list of paragraphs and planned paragraphs.
 */
@Composable
fun ParagraphsPage(
    paragraphs: List<Paragraph>,
    planned: List<PlannedParagraph>,
    onEdit: (Paragraph) -> Unit,
    onPlan: (Paragraph) -> Unit,
    onSaveTemplate: (Paragraph) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    PaperBackground(modifier = modifier.fillMaxSize()) {
        Column {
            TextButton(
                onClick = onAdd,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("\u2795 Begin a new weekly chapter", fontFamily = GaeguRegular)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(paragraphs) { paragraph ->
                    ParagraphEntryCard(
                        paragraph = paragraph,
                        onEdit = { onEdit(paragraph) },
                        onPlan = { onPlan(paragraph) },
                        onSaveTemplate = { onSaveTemplate(paragraph) }
                    )
                }
                if (planned.isNotEmpty()) {
                    item {
                        Text(
                            text = "Planned paragraphs:",
                            style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    items(planned) { plannedParagraph ->
                        Column {
                            ParagraphEntryCard(
                                paragraph = plannedParagraph.paragraph,
                                onEdit = {},
                                onPlan = {},
                                onSaveTemplate = {},
                                showButtons = false
                            )
                            Text(
                                text = "Starts on: ${plannedParagraph.startDate}",
                                style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp),
                                modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }


}
