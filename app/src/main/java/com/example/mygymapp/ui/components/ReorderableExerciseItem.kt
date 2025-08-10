package com.example.mygymapp.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.animateItemPlacement
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.Icon
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
import com.example.mygymapp.ui.motion.MotionSpec

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderableExerciseItem(
    index: Int,
    exercise: LineExercise,
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
            targetValue = when {
                isDragTarget -> Color(0xFFC8E6C9)
                isDraggingPartner -> Color(0xFFFFF59D)
                else -> Color.Transparent
            }, animationSpec = MotionSpec.tweenMedium()
        )
        val borderColor by animateColorAsState(
            targetValue = when {
                isDragTarget -> Color(0xFF2E7D32)
                isDraggingPartner -> Color(0xFFFBC02D)
                isSuperset -> Color(0xFFFFF59D)
                else -> Color.Transparent
            }, animationSpec = MotionSpec.tweenMedium()
        )
        val backgroundBrush = if (isSuperset) {
            Brush.verticalGradient(listOf(Color(0xFFFDF6EC), Color(0xFFE8F5E9)))
        } else {
            Brush.verticalGradient(listOf(highlightColor, highlightColor))
        }
        val isDragging = elevation > 2.dp
        val scale by animateFloatAsState(
            targetValue = if (isDragging) 1.02f else 1f,
            animationSpec = MotionSpec.springSoft()
        )
        val underlineAlpha by animateFloatAsState(
            targetValue = if (isDragging) 1f else 0f,
            animationSpec = MotionSpec.tweenFast()
        )
        val animatedElevation by animateDpAsState(
            targetValue = elevation,
            animationSpec = MotionSpec.springSoft()
        )
        Box(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .weight(1f)
                .animateItemPlacement(MotionSpec.springSoft())
                .graphicsLayer(
                    clip = false,
                    rotationZ = if (isSuperset) if (index % 2 == 0) -2f else 2f else 0f,
                    scaleX = scale,
                    scaleY = scale
                )
                .background(backgroundBrush)
                .border(1.dp, borderColor)
        ) {
            PoeticCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = animatedElevation
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
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(2.dp)
                    .alpha(underlineAlpha)
                    .background(Color.Black.copy(alpha = 0.1f))
            )
        }
    }
}


