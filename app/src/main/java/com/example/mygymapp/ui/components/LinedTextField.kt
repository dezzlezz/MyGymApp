package com.example.mygymapp.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.text.getLineBottom
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
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

    // Compute descent from the font metrics so we can translate the layout's
    // line bottoms to baselines and generate additional baselines for empty
    // trailing lines.
    val metrics = remember(textStyle.fontSize, density) {
        Paint().apply {
            textSize = with(density) { textStyle.fontSize.toPx() }
        }.fontMetrics
    }
    val descent = metrics.descent
    val baselineOffset = -metrics.ascent

    val layout = layoutResult
    // Use the layout's measured line spacing when available so that
    // additional lines are spaced identically to the rendered text.
    val baselineSpacing = layout?.let {
        if (it.lineCount > 1) {
            (it.getLineBottom(1) - it.getLineBottom(0)).toFloat()
        } else {
            lineHeightPx
        }
    } ?: lineHeightPx

    val lineCount = maxOf(layout?.lineCount ?: 0, minLines)
    val height = with(density) { (baselineSpacing * lineCount).toDp() }

    // Compute descent from the font metrics so we can translate the layout's
    // line bottoms to baselines and generate additional baselines for empty
    // trailing lines.
    val fontSizePx = with(density) { textStyle.fontSize.toPx() }
    val paint = remember { Paint() }
    paint.textSize = fontSizePx
    val fontMetrics = paint.fontMetrics
    val descent = fontMetrics.descent
    val baselineOffset = -fontMetrics.ascent

    val layout = layoutResult
    // Use the layout's measured line spacing when available so that
    // additional lines are spaced identically to the rendered text.
    val baselineSpacing = layout?.let {
        if (it.lineCount > 1) {
            (it.getLineBottom(1) - it.getLineBottom(0)).toFloat()
        } else {
            lineHeightPx
        }
    } ?: lineHeightPx

    val lineCount = maxOf(layout?.lineCount ?: 0, minLines)
    val height = with(density) { (baselineSpacing * lineCount).toDp() }

    // Compute descent from the font metrics so we can translate the layout's
    // line bottoms to baselines and generate additional baselines for empty
    // trailing lines.
    val fontSizePx = with(density) { textStyle.fontSize.toPx() }
    val paint = remember { Paint() }
    paint.textSize = fontSizePx
    val fontMetrics = paint.fontMetrics
    val descent = fontMetrics.descent
    val baselineOffset = -fontMetrics.ascent

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val lastBaseline = if (layout != null && layout.lineCount > 0) {
                layout.getLineBottom(layout.lineCount - 1) - descent
            } else {
                // Start extra baselines from the first line's baseline when no text is present
                baselineOffset - baselineSpacing
            }

            for (i in 0 until lineCount) {
                val baseline = if (layout != null && i < layout.lineCount) {
                    layout.getLineBottom(i) - descent
                } else {
                    lastBaseline + (i - (layout?.lineCount ?: 0) + 1) * baselineSpacing
                }

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
