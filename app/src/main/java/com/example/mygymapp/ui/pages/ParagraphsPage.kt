package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.R
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.model.PlannedParagraph
import com.example.mygymapp.ui.components.GaeguButton
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticDivider
import com.example.mygymapp.ui.components.ParagraphCard
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun ParagraphsPage(
    paragraphs: List<Paragraph>,
    archived: List<Paragraph>,
    planned: List<PlannedParagraph>,
    onEdit: (Paragraph) -> Unit,
    onPlan: (Paragraph) -> Unit,
    onSaveTemplate: (Paragraph) -> Unit,
    onArchive: (Paragraph) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var search by remember { mutableStateOf("") }
    val query = search.trim().lowercase()
    val filtered = paragraphs.filter { it.title.lowercase().contains(query) }
    val inkColor = Color(0xFF1B1B1B)

    PaperBackground(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            BasicTextField(
                value = search,
                onValueChange = { search = it },
                textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .drawBehind {
                        val strokeWidth = 2f
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = inkColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                cursorBrush = SolidColor(inkColor),
                decorationBox = { innerTextField ->
                    if (search.isEmpty()) {
                        Text("Search paragraphs...", fontFamily = GaeguRegular, fontStyle = FontStyle.Italic, color = Color.Gray)
                    }
                    innerTextField()
                }
            )

            Spacer(Modifier.height(12.dp))

            PoeticDivider(centerText = "Your paragraphs")

            if (filtered.isEmpty()) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No paragraphs yet – this page is waiting for its first chapter.",
                        style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    GaeguButton(
                        text = stringResource(R.string.add_paragraph_button),
                        onClick = onAdd,
                        textColor = Color.Black
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { paragraph ->
                        ParagraphCard(
                            paragraph = paragraph,
                            onEdit = { onEdit(paragraph) },
                            onPlan = { onPlan(paragraph) },
                            onSaveTemplate = { onSaveTemplate(paragraph) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
