package com.example.mygymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.pages.GaeguLight
import com.example.mygymapp.ui.pages.GaeguRegular
import android.graphics.Paint

@Composable
fun LinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    lineHeight: Dp = 32.dp,
    minLines: Int = 4
) {
    val density = LocalDensity.current
    val lineHeightPx = with(density) { lineHeight.toPx() }
    val lineCount = maxOf(value.lineSequence().count() + 1, minLines)
    val height = lineHeight * lineCount
    val baselineOffset = remember(density) {
        val paint = Paint().apply {
            textSize = with(density) { 18.sp.toPx() }
        }
        -paint.fontMetrics.ascent
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(4.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            for (i in 0 until lineCount) {
                val y = baselineOffset + i * lineHeightPx
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.2f
                )
            }
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 18.sp,
                lineHeight = lineHeight.value.sp,
                fontFamily = GaeguRegular,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    hint,
                    fontFamily = GaeguLight,
                    fontSize = 18.sp,
                    lineHeight = lineHeight.value.sp,
                    color = Color.Gray
                )
            }
            innerTextField()
        }
    }
}
