package com.example.mygymapp.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
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
    showButtons: Boolean = true,
    startDate: String? = null,
    backgroundColor: Color = Color(0xFFFFF8E1),
    moodDotBeforeTitle: Boolean = false
) {
    val moodColor = when (paragraph.mood.lowercase()) {
        "calm" -> Color(0xFFB3E5FC)
        "alert" -> Color(0xFFFFF9C4)
        "connected" -> Color(0xFFE1BEE7)
        "alive" -> Color(0xFFC8E6C9)
        "empty" -> Color(0xFFFFE0B2)
        "carried" -> Color(0xFFD7CCC8)
        "searching" -> Color(0xFFDCE775)
        else -> Color(0xFFD7CCC8)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (moodDotBeforeTitle) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(moodColor, CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                }
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
            if (!moodDotBeforeTitle) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(moodColor, CircleShape)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = paragraph.mood,
                        style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp, color = moodColor)
                    )
                }
            }
            if (paragraph.tags.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    paragraph.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag, fontFamily = GaeguRegular, color = Color.Black) })
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
                        color = Color(0xFF5D4037)
                    )
                )
            }
            if (showButtons) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    TextButton(onClick = onEdit) {
                        Text(
                            "\u270F\uFE0F Edit",
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                    TextButton(onClick = onPlan) {
                        Text(
                            "\uD83D\uDCC6 Plan",
                            fontFamily = GaeguRegular,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                    TextButton(onClick = onSaveTemplate) {
                        Text(
                            "\uD83D\uDCCE Save as Template",
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
                Text("\u2795 Begin a new weekly chapter", fontFamily = GaeguRegular, color = Color.Black)
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
                            style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp, color = Color.Black),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    items(planned) { plannedParagraph ->
                        ParagraphEntryCard(
                            paragraph = plannedParagraph.paragraph,
                            onEdit = {},
                            onPlan = {},
                            onSaveTemplate = {},
                            showButtons = false,
                            startDate = plannedParagraph.startDate.toString(),
                            backgroundColor = Color(0xFFE0E0E0),
                            moodDotBeforeTitle = true
                        )
                    }
                }
            }
        }
    }


}
