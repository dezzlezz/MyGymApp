package com.example.mygymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
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
import com.example.mygymapp.ui.theme.TextSecondary
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import com.example.mygymapp.ui.components.PrimaryButton

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
                    text = "${ex.muscleGroup.display} · ${ex.customCategory ?: ex.category.name}",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseHighlightCard(
    exercise: Exercise,
    query: String,
    modifier: Modifier = Modifier
) {
    var showSheet by remember { mutableStateOf(false) }

    val backgroundColor = if (exercise.isFavorite) Color(0xFFFFF8E1) else Color(0xFFEDE5D0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { showSheet = true }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            exercise.imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = highlightQuery(exercise.name, query),
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = GaeguBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${exercise.muscleGroup.display} · ${exercise.customCategory ?: exercise.category.name}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = GaeguRegular),
                    color = TextSecondary
                )
            }
        }
    }

    if (showSheet) {
        ExerciseDetailSheet(exercise = exercise) { showSheet = false }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailSheet(exercise: Exercise, onClose: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = GaeguBold),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${exercise.category.name} • ${exercise.muscleGroup.display}",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = GaeguRegular)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Likeability: ${exercise.likeability}/5",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = GaeguRegular)
            )

            if (exercise.description.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Notes:",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = GaeguBold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = GaeguRegular)
                )
            }

            if (exercise.isFavorite) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "⭐ Marked as favorite",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = GaeguRegular)
                )
            }

            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                textRes = R.string.close
            )
        }
    }
}