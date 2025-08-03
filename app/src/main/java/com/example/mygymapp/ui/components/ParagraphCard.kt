package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun ParagraphCard(
    paragraph: Paragraph,
    onEdit: () -> Unit,
    onPlan: () -> Unit,
    onSaveTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    PoeticCard(modifier = modifier.padding(vertical = 6.dp)) {
        Text(
            text = paragraph.title,
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = GaeguBold)
        )
        Spacer(modifier = Modifier.height(12.dp))
        paragraph.lineTitles.forEachIndexed { index, title ->
            Text(
                text = "Day ${index + 1}: $title",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = GaeguRegular)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = onEdit) { Text("✏️ Edit", fontFamily = GaeguRegular) }
            TextButton(onClick = onPlan) { Text("📆 Plan", fontFamily = GaeguRegular) }
            TextButton(onClick = onSaveTemplate) { Text("📎 Save Template", fontFamily = GaeguRegular) }
        }
    }
}

