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
    initialLines: Int = 3,
    padding: Dp = 12.dp
) {
    val density = LocalDensity.current
    val textStyle = TextStyle(
        fontSize = 18.sp,
        lineHeight = lineHeight.value.sp,
        fontFamily = GaeguRegular,
        color = Color.Black
    )

    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val lineHeightPx = with(density) { lineHeight.toPx() }

    val layoutLineCount = layoutResult?.lineCount ?: 0
    val totalLineCount = maxOf(layoutLineCount, initialLines)
    val fieldHeight = lineHeight * totalLineCount

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(fieldHeight)
            .padding(horizontal = padding)
    ) {
        // 🎯 Linien zeichnen – mit absolutem Schutz gegen Absturz
        Canvas(modifier = Modifier.matchParentSize()) {
            for (i in 0 until totalLineCount) {
                val y = runCatching {
                    layoutResult?.getLineBaseline(i)?.toFloat()
                }.getOrNull() ?: ((i + 1) * lineHeightPx)

                drawLine(
                    color = Color.Black,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.2f
                )
            }
        }

        // ✏️ Texteingabe + Hint
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            onTextLayout = { layoutResult = it },
            decorationBox = { innerTextField ->
                Column {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            fontFamily = GaeguLight,
                            fontSize = 18.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
