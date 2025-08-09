package com.example.mygymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.RepeatMode
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.mygymapp.ui.pages.GaeguLight
import com.example.mygymapp.ui.pages.GaeguRegular
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch

@Composable
fun LinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    lineHeight: Dp = 32.dp,
    initialLines: Int = 3,
    padding: Dp = 12.dp,
    isError: Boolean = false,
    bringIntoViewRequester: BringIntoViewRequester? = null
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

    var shakeTrigger by remember { mutableStateOf(false) }
    val shakeOffset by animateFloatAsState(
        if (shakeTrigger) 8f else 0f,
        animationSpec = keyframes {
            durationMillis = 300
            0f at 0
            -8f at 50
            8f at 100
            -8f at 150
            0f at 200
        }
    )
    LaunchedEffect(isError) {
        if (isError) shakeTrigger = !shakeTrigger
    }
    val infinite = rememberInfiniteTransition()
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )
    val borderBrush = if (isError) {
        Brush.radialGradient(listOf(Color.Red.copy(alpha = glowAlpha), Color.Transparent))
    } else {
        Brush.radialGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(fieldHeight)
            .padding(horizontal = padding)
            .graphicsLayer { translationX = shakeOffset }
            .border(BorderStroke(2.dp, borderBrush))
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
        val scope = rememberCoroutineScope()
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .then(
                    if (bringIntoViewRequester != null) {
                        Modifier
                            .bringIntoViewRequester(bringIntoViewRequester)
                            .onFocusChanged { state ->
                                if (state.isFocused) {
                                    scope.launch { bringIntoViewRequester.bringIntoView() }
                                }
                            }
                    } else Modifier
                ),
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
