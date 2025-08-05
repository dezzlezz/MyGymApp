package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguLight
import com.example.mygymapp.ui.pages.GaeguRegular

/**
 * A draggable exercise card with poetic design.
 * Displays:
 * - Index number
 * - Exercise name
 * - Optional metadata (category, muscle group)
 * - Actions: delete, drag and superset selection checkbox
 */
@Composable
fun ReorderableExerciseItem(
    index: Int,
    exercise: LineExercise,
    onRemove: () -> Unit,
    isSupersetSelected: Boolean,
    onSupersetSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    dragHandle: @Composable () -> Unit,
    supersetPartnerIndices: List<Int> = emptyList()
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5DC),
        tonalElevation = 2.dp,
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Index & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${index + 1}.",
                        fontFamily = GaeguBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Column {
                        Text(
                            text = exercise.name,
                            fontFamily = GaeguRegular,
                            fontSize = 16.sp
                        )
                        exercise.repsOrDuration?.let {
                            Text(
                                text = "e.g. $it reps",
                                fontFamily = GaeguLight,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                    Checkbox(
                        checked = isSupersetSelected,
                        onCheckedChange = onSupersetSelectedChange
                    )
                    dragHandle()
                }
            }

            if (supersetPartnerIndices.isNotEmpty()) {
                Text(
                    text = "🧷 Superset with " +
                        supersetPartnerIndices.joinToString(prefix = "#", separator = ", #") { (it + 1).toString() },
                    fontFamily = GaeguLight,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                )
            }
        }
    }
}

