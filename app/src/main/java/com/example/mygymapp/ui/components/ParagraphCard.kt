package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Paragraph

@Composable
fun ParagraphCard(
    paragraph: Paragraph,
    onEdit: () -> Unit,
    onPlan: () -> Unit,
    onSaveTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = paragraph.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Mood: ${paragraph.mood}",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif)
            )
            if (paragraph.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = paragraph.tags.joinToString(),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            paragraph.lineTitles.forEachIndexed { index, title ->
                Text(
                    text = "Day ${index + 1}: $title",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Serif)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onEdit) { Text("✏️ Edit") }
                TextButton(onClick = onPlan) { Text("📆 Plan") }
                TextButton(onClick = onSaveTemplate) { Text("📎 Save Template") }
            }
        }
    }
}

