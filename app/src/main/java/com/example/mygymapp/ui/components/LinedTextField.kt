package com.example.mygymapp.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.pages.GaeguLight
import com.example.mygymapp.ui.pages.GaeguRegular

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
    val textStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = lineHeight.value.sp,
        fontFamily = GaeguRegular,
        color = Color.Black
    )

    val lineHeightPx = with(density) { textStyle.lineHeight.toPx() }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val metrics = remember(textStyle.fontSize, density) {
        Paint().apply {
            textSize = with(density) { textStyle.fontSize.toPx() }
        }.fontMetrics
    }
    val baselineOffset = -metrics.ascent

    val lineCount = maxOf(layoutResult?.lineCount ?: 0, minLines)

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            for (i in 0 until lineCount) {
                val baseline = baselineOffset + i * lineHeightPx
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, baseline),
                    end = Offset(size.width, baseline),
                    strokeWidth = 1.2f
                )
            }
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            onTextLayout = { layoutResult = it }
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
        }
    }
}

