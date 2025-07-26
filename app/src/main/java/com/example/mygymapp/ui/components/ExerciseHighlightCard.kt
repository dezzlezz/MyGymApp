package com.example.mygymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
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
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.data.Exercise
import androidx.compose.ui.draw.clip

@Composable
fun ExerciseCardWithHighlight(
    ex: Exercise,
    query: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {

            if (ex.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(ex.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .padding(end = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = highlightQuery(ex.name, query),
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${ex.muscleGroup.display} · ${ex.category.display}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Serif,
                    color = Color.DarkGray
                )

                if (ex.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ex.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Serif,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (onToggleFavorite != null) {
                        IconButton(onClick = onToggleFavorite) {
                            Icon(
                                imageVector = if (ex.isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favorite",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
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
