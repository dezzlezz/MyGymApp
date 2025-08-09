package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.pages.GaeguBold

// In SectionWrapper.kt – Signatur minimal erweitern:
@Composable
fun SectionWrapper(
    title: String,
    modifier: Modifier = Modifier,
    minDropHeightDp: Int = 64,
    isDropActive: Boolean = false,            // NEW
    content: @Composable ColumnScope.() -> Unit
) {
    val paddingY by animateDpAsState(targetValue = if (isDropActive) 20.dp else 12.dp)
    val infinite = rememberInfiniteTransition()
    val animatedStroke by infinite.animateFloat(
        initialValue = 2f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )
    val scale by if (isDropActive) {
        infinite.animateFloat(
            initialValue = 1.02f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
        )
    } else {
        remember { mutableStateOf(1f) }
    }
    Box(
        modifier = Modifier
            .padding(vertical = paddingY)
            .then(modifier)
            .fillMaxWidth()
            .defaultMinSize(minHeight = minDropHeightDp.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .drawBehind {
                val stroke = if (isDropActive) animatedStroke.dp.toPx() else 2.dp.toPx()
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
                    color = if (isDropActive) Color(0xFF2E7D32) else Color.Black, // grün beim Hover
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            }
    ) {
        Column(modifier = Modifier
            .padding(start = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = title,
                fontFamily = GaeguBold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
            Spacer(Modifier.height(4.dp))
        }
    }
}

