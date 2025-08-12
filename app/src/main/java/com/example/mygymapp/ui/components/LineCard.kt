package com.example.mygymapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.mygymapp.R
import com.example.mygymapp.model.Line
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.ui.pages.GaeguRegular
import com.example.mygymapp.ui.pages.GaeguBold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Edit

@Composable
fun LineCard(
    line: Line,
    onEdit: () -> Unit,
    onAdd: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fade by animateFloatAsState(if (line.isArchived) 0f else 1f, label = "fade")
    val headerColor = Color.Black
    val secondaryColor = Color(0xFF555D50)
    val noteColor = Color(0xFF6D6D6D)

    PoeticCard(
        modifier = modifier
            .alpha(fade)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = line.title,
                    style = TextStyle(
                        fontFamily = GaeguBold,
                        fontSize = 26.sp,
                        color = headerColor
                    )
                )
                Text(
                    text = "${line.category} \u00B7 ${line.muscleGroup}",
                    style = TextStyle(
                        fontFamily = GaeguRegular,
                        fontSize = 14.sp,
                        color = secondaryColor
                    )
                )
            }
            line.mood?.let { MoodChip(mood = it) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        val supersetWord = if (line.supersets.size == 1) stringResource(R.string.superset_singular) else stringResource(R.string.superset_plural)
        Text(
            text = stringResource(R.string.line_card_summary, line.exercises.size, line.supersets.size, supersetWord),
            style = TextStyle(
                fontFamily = GaeguRegular,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = secondaryColor
            )
        )
        if (line.note.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.note_prefix, line.note),
                style = TextStyle(
                    fontFamily = GaeguRegular,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = noteColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(
                onClick = onEdit,
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = headerColor)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.edit_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
            TextButton(
                onClick = onAdd,
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = headerColor)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.add_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
            TextButton(
                onClick = onArchive,
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = headerColor)
            ) {
                Icon(Icons.Filled.Archive, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.archive_label), style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp))
            }
        }
    }
}