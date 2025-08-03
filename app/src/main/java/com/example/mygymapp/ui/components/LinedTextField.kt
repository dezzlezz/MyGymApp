package com.example.mygymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
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
    val textStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = lineHeight.value.sp,
        fontFamily = GaeguRegular,
        color = Color.Black
    )
    val lineHeightPx = with(density) { textStyle.lineHeight.toPx() }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val textLineCount = layoutResult?.lineCount ?: 1
    val lineCount = maxOf(textLineCount, minLines)
    val height = with(density) { lineHeightPx.toDp() } * lineCount

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .padding(4.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val layout = layoutResult
            val lastBaseline = layout?.getLineBottom((layout.lineCount - 1).coerceAtLeast(0))
                ?: lineHeightPx
            for (i in 0 until lineCount) {
                val y = when {
                    layout != null && i < layout.lineCount -> layout.getLineBottom(i)
                    else -> lastBaseline + (i - (layout?.lineCount ?: 0)) * lineHeightPx
                }
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
            innerTextField()
        }
    }
}
