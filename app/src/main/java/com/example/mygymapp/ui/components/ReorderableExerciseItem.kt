package com.example.mygymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun ReorderableExerciseItem(
    index: Int,
    exercise: com.example.mygymapp.model.Exercise,
    onRemove: () -> Unit,
    onMove: () -> Unit,
    isSupersetSelected: Boolean,
    onSupersetSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    dragHandle: @Composable () -> Unit,
    supersetPartnerIndices: List<Int> = emptyList(),
    isDraggingPartner: Boolean = false,
    elevation: Dp = 2.dp
) {
    val indices = (listOf(index) + supersetPartnerIndices).sorted()
    val isSuperset = supersetPartnerIndices.isNotEmpty()
    val isFirst = isSuperset && index == indices.first()
    val isLast = isSuperset && index == indices.last()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSuperset) {
            Box(modifier = Modifier.width(16.dp).fillMaxHeight()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 2.dp.toPx()
                    val centerX = size.width / 2f
                    val startY = if (isFirst) size.height / 2f else 0f
                    val endY = if (isLast) size.height / 2f else size.height
                    drawLine(Color.Black, Offset(centerX, startY), Offset(centerX, endY), stroke)
                    drawLine(Color.Black, Offset(centerX, size.height / 2f), Offset(size.width, size.height / 2f), stroke)
                }
            }
        } else {
            Spacer(Modifier.width(16.dp))
        }

        val highlightColor by animateColorAsState(
            targetValue = when {
                isDraggingPartner -> Color(0xFFFFF59D)
                isSuperset -> Color(0xFFFFFDE7)
                else -> Color.Transparent
            }
        )
        val borderColor by animateColorAsState(
            targetValue = when {
                isDraggingPartner -> Color(0xFFFBC02D)
                isSuperset -> Color(0xFFFFF59D)
                else -> Color.Transparent
            }
        )
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .weight(1f)
                .graphicsLayer(clip = false)
                .background(highlightColor)
                .border(1.dp, borderColor)
        ) {
            PoeticCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = elevation
            ) {
                Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Index & Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${index + 1}.",
                            fontFamily = GaeguBold,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = exercise.name,
                            fontFamily = GaeguRegular,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                    // Actions
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onRemove) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                        TextButton(onClick = onMove) {
                            Text(
                                "Move",
                                fontFamily = GaeguRegular,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        Checkbox(
                            checked = isSupersetSelected,
                            onCheckedChange = onSupersetSelectedChange
                        )
                        dragHandle()
                    }
                }
            }
        }
    }
}


