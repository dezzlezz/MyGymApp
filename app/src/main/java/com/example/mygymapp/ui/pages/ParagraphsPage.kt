package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.model.PlannedParagraph
import com.example.mygymapp.ui.components.PaperBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParagraphEntryCard(
    paragraph: Paragraph,
    onEdit: () -> Unit,
    onPlan: () -> Unit,
    onSaveTemplate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showButtons: Boolean = true,
    startDate: String? = null,
    backgroundColor: Color = Color(0xFFFFF8E1),
    onPreview: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .combinedClickable(onClick = {}, onLongClick = onPreview),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = paragraph.title,
                    style = TextStyle(fontFamily = GaeguBold, fontSize = 22.sp, color = Color.Black)
                )
            }
            if (startDate != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Starts on: $startDate",
                    style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.DarkGray)
                )
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
                    Row {
                        Text(
                            text = days.getOrNull(index) ?: "Day ${index + 1}",
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.Black)
                        )
                        Text(
                            text = " \u2192 ",
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.Black)
                        )
                        Text(
                            text = title,
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.Black)
                        )
                    }
                }
            }
            if (paragraph.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = paragraph.note,
                    style = TextStyle(
                        fontFamily = GaeguRegular,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray
                    )
                )
            }
            if (showButtons) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                        Text(
                            "\u270F Edit",
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                    TextButton(onClick = onPlan, modifier = Modifier.weight(1f)) {
                        Text(
                            "\uD83D\uDCC6 Plan",
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                    TextButton(onClick = onSaveTemplate, modifier = Modifier.weight(1f)) {
                        Text(
                            "\uD83D\uDCCE Save Template",
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                    TextButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                        Text(
                            "\uD83D\uDDD1 Delete",
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                    TextButton(onClick = onDelete) {
                        Text(
                            "\uD83D\uDDD1 Delete",
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays a poetic list of paragraphs and planned paragraphs.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParagraphsPage(
    paragraphs: List<Paragraph>,
    planned: List<PlannedParagraph>,
    onEdit: (Paragraph) -> Unit,
    onPlan: (Paragraph) -> Unit,
    onSaveTemplate: (Paragraph) -> Unit,
    onDelete: (Paragraph) -> Unit,
    onAdd: () -> Unit,
    onPreview: (Paragraph) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PaperBackground(modifier = modifier.fillMaxSize()) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\uD83D\uDCDA Weekly Chapters",
                    style = TextStyle(fontFamily = GaeguBold, fontSize = 24.sp, color = Color.Black)
                )
                Text(
                    text = "Browse what you\u2019ve composed \u2013 week by week.",
                    style = TextStyle(fontFamily = GaeguRegular, fontSize = 16.sp, color = Color.DarkGray)
                )
            }
            TextButton(
                onClick = onAdd,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "\u2795 Begin a new weekly paragraph",
                    fontFamily = GaeguRegular,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 72.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Saved Paragraphs",
                        style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp, color = Color.Black),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
                items(paragraphs, key = { it.id }) { paragraph ->
                    ParagraphEntryCard(
                        paragraph = paragraph,
                        onEdit = { onEdit(paragraph) },
                        onPlan = { onPlan(paragraph) },
                        onSaveTemplate = { onSaveTemplate(paragraph) },
                        onDelete = { onDelete(paragraph) },
                        modifier = Modifier.animateItemPlacement(),
                        showButtons = true,
                        onPreview = { onPreview(paragraph) },
                    )
                }
                if (planned.isNotEmpty()) {
                    item {
                        Divider(
                            color = Color.Black.copy(alpha = 0.2f),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    item {
                        Text(
                            text = "\u23F3 Planned for the Future",
                            style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp, color = Color.Black),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    items(planned, key = { it.paragraph.id }) { plannedParagraph ->
                        ParagraphEntryCard(
                            paragraph = plannedParagraph.paragraph,
                            onEdit = {},
                            onPlan = {},
                            onSaveTemplate = {},
                            onDelete = {},
                            modifier = Modifier.animateItemPlacement(),
                            showButtons = false,
                            startDate = plannedParagraph.startDate.toString(),
                            onPreview = { onPreview(plannedParagraph.paragraph) },
                        )
                    }
                }
            }
        }
    }
}

