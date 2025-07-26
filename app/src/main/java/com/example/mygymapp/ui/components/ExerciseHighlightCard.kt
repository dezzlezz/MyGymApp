package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Exercise

@Composable
fun ExerciseCardWithHighlight(
    ex: Exercise,
    query: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = highlightQuery(ex.name, query),
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Serif,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${ex.muscleGroup.display} · ${ex.category.display}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Text("Edit", fontFamily = FontFamily.Serif)
                }
                TextButton(onClick = onDelete) {
                    Text("Delete", fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}

fun highlightQuery(text: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text)
    val normalizedText = text.replace("\\s".toRegex(), "").lowercase()
    val start = normalizedText.indexOf(query)
    if (start < 0) return AnnotatedString(text)

    var actualStart = 0
    var currentIndex = 0
    var normalizedIndex = 0
    while (normalizedIndex < start && currentIndex < text.length) {
        if (!text[currentIndex].isWhitespace()) {
            normalizedIndex++
        }
        currentIndex++
    }
    actualStart = currentIndex

    var matchCount = 0
    var endIndex = actualStart
    while (endIndex < text.length && matchCount < query.length) {
        if (!text[endIndex].isWhitespace()) {
            matchCount++
        }
        endIndex++
    }

    return buildAnnotatedString {
        append(text.substring(0, actualStart))
        withStyle(SpanStyle(color = Color.Red)) {
            append(text.substring(actualStart, endIndex))
        }
        append(text.substring(endIndex))
    }
}
