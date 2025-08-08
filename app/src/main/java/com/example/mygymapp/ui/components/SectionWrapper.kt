package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.pages.GaeguBold

/**
 * A poetic wrapper for grouping exercises into a section (e.g., Warm-up, Workout, Cooldown).
 * Instead of a full card, it draws a left and bottom line joined by a rounded corner,
 * giving the impression that the section gently hugs its exercises.
 */
@Composable
fun SectionWrapper(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 64.dp)
            .padding(vertical = 12.dp)
            .drawBehind {
                val stroke = 2.dp.toPx()
                val radius = 12.dp.toPx()
                val w = size.width
                val h = size.height
                val path = Path().apply {
                    moveTo(stroke / 2, 0f)
                    lineTo(stroke / 2, h - radius - stroke / 2)
                    arcTo(
                        Rect(
                            stroke / 2,
                            h - 2 * radius - stroke / 2,
                            stroke / 2 + 2 * radius,
                            h - stroke / 2
                        ),
                        180f,
                        -90f,
                        false
                    )
                    lineTo(w - stroke / 2, h - stroke / 2)
                }
                drawPath(
                    path = path,
                    color = Color.Black,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
    ) {
        Column(modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)) {
            Text(
                text = title,
                fontFamily = GaeguBold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            content()
        }
    }
}
