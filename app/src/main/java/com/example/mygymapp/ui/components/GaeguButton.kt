package com.example.mygymapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.pages.GaeguBold

@Composable
fun GaeguButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    font: FontFamily = GaeguBold,
    gradientColors: List<Color> = listOf(Color(0xFFFFAFBD), Color(0xFFFFC3A0)),
    textColor: Color = Color.Black,
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 2.dp,
    fontSize: TextUnit = 16.sp,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "button-scale"
    )

    val targetColors = if (isPressed) {
        gradientColors.map { lerp(it, Color.White, 0.2f) }
    } else gradientColors
    val startColor by animateColorAsState(targetColors[0], label = "start-color")
    val endColor by animateColorAsState(targetColors[1], label = "end-color")
    val brush = Brush.linearGradient(listOf(startColor, endColor))

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null, // ⛔ Kein Ripple
                onClick = onClick
            )
            .padding(vertical = 12.dp, horizontal = 20.dp)
            .shadow(elevation, RoundedCornerShape(cornerRadius))
    ) {
        Text(
            text = text,
            fontFamily = font,
            fontSize = fontSize,
            color = textColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
