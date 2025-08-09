package com.example.mygymapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.mygymapp.R
import com.example.mygymapp.model.Line
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.ui.pages.GaeguRegular
import com.example.mygymapp.ui.pages.GaeguBold

@Composable
fun LineCard(
    line: Line,
    onEdit: () -> Unit,
    onAdd: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fade by animateFloatAsState(if (line.isArchived) 0f else 1f, label = "fade")
    val textColor = Color(0xFF5D4037)
    val buttonBackground = Color(0xFFFFF8E1)

    PoeticCard(
        modifier = modifier
            .alpha(fade)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = line.title,
                style = TextStyle(
                    fontFamily = GaeguBold,
                    fontSize = 24.sp,
                    color = textColor
                )
            )
            line.mood?.let {
                Spacer(modifier = Modifier.width(8.dp))
                MoodChip(mood = it)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        val supersetWord = if (line.supersets.size == 1) stringResource(R.string.superset_singular) else stringResource(R.string.superset_plural)
        Text(
            text = stringResource(R.string.line_card_summary, line.exercises.size, line.supersets.size, supersetWord),
            style = TextStyle(
                fontFamily = GaeguRegular,
                fontSize = 14.sp,
                color = textColor
            )
        )
        if (line.note.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.note_prefix, line.note),
                style = TextStyle(
                    fontFamily = GaeguRegular,
                    fontSize = 14.sp,
                    color = textColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (line.exercises.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            line.exercises.forEach { ex ->
                ExerciseItem(exercise = ex)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(
                onClick = onEdit,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = buttonBackground,
                    contentColor = textColor
                )
            ) {
                Text(
                    stringResource(R.string.edit_label),
                    style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp)
                )
            }
            TextButton(
                onClick = onAdd,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = buttonBackground,
                    contentColor = textColor
                )
            ) {
                Text(
                    stringResource(R.string.add_label),
                    style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp)
                )
            }
            TextButton(
                onClick = onArchive,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = buttonBackground,
                    contentColor = textColor
                )
            ) {
                Text(
                    stringResource(R.string.archive_label),
                    style = TextStyle(fontFamily = GaeguRegular, fontSize = 14.sp)
                )
            }
        }
    }
}