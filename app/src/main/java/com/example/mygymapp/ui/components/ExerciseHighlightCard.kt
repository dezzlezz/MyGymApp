package com.example.mygymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular
import com.example.mygymapp.ui.theme.PrimaryGreen
import com.example.mygymapp.ui.theme.TextSecondary
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun ExerciseCardWithHighlight(
    ex: Exercise,
    query: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: (() -> Unit)? = null
) {
    val cardColor = Color.White.copy(alpha = 0.8f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardColor)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = GaeguBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ex.muscleGroup.display} · ${ex.category.display}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = GaeguRegular),
                    color = Color.DarkGray
                )

                if (ex.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ex.description,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = GaeguRegular),
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (ex.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ex.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = GaeguRegular,
                    color = Color.DarkGray
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

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
                        Text("Edit", fontFamily = GaeguRegular)
                    }
                    TextButton(onClick = onDelete) {
                        Text("Delete", fontFamily = GaeguRegular)
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