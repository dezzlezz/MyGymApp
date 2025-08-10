package com.example.mygymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Exercise as LineExercise
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.rotate

@Composable
fun ReorderableExerciseItem(
    index: Int,
    exercise: com.example.mygymapp.model.Exercise,
    onRemove: () -> Unit,
    onMove: () -> Unit,
    modifier: Modifier = Modifier,
    dragHandle: @Composable () -> Unit,
    supersetPartnerIndices: List<Int> = emptyList(),
    isDraggingPartner: Boolean = false,
    isDragTarget: Boolean = false,
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
            Box(
                modifier = Modifier.width(16.dp).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Superset",
                    tint = Color.Gray,
                    modifier = Modifier.rotate(90f)
                )
            }
        } else {
            Spacer(Modifier.width(16.dp))
        }

        val highlightColor by animateColorAsState(
            when {
                isDragTarget -> Color(0xFFC8E6C9)
                isDraggingPartner -> Color(0xFFFFF59D)
                else -> Color.Transparent
            }
        )
        val borderColor by animateColorAsState(
            when {
                isDragTarget -> Color(0xFF2E7D32)
                isDraggingPartner -> Color(0xFFFBC02D)
                isSuperset -> Color(0xFFFFF59D)
                else -> Color.Transparent
            }
        )
        val backgroundBrush = if (isSuperset) {
            Brush.verticalGradient(listOf(Color(0xFFFDF6EC), Color(0xFFE8F5E9)))
        } else {
            Brush.verticalGradient(listOf(highlightColor, highlightColor))
        }
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .weight(1f)
                .graphicsLayer(clip = false, rotationZ = if (isSuperset) if (index % 2 == 0) -2f else 2f else 0f)
                .background(backgroundBrush)
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
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                            TextButton(onClick = onMove) {
                                Text(
                                    "Move",
                                    fontFamily = GaeguRegular,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                            dragHandle()
                        }
                    }
                }
            }
        }
    }
}


