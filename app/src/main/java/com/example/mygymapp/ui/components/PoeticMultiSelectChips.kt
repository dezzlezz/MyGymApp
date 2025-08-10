package com.example.mygymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.pages.GaeguRegular
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

/**
 * A poetic chip selector allowing multiple selections.
 * Styled gently with rounded corners and soft selection highlights.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PoeticMultiSelectChips(
    options: List<String>,
    selectedItems: List<String>,
    onSelectionChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    font: FontFamily = GaeguRegular,
    selectedBackground: Color = Color(0xFFD7CCC8),
    unselectedBackground: Color = Color.Transparent,
    textColor: Color = Color.Black,
    spacing: Dp = 8.dp
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        options.forEach { option ->
            val isSelected = option in selectedItems
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) selectedBackground else unselectedBackground,
                animationSpec = tween(durationMillis = 150)
            )
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.98f,
                animationSpec = tween(durationMillis = 150)
            )
            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.6f,
                animationSpec = tween(durationMillis = 150)
            )
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .alpha(alpha)
                    .clickable {
                        val updated = if (isSelected) {
                            selectedItems - option
                        } else {
                            selectedItems + option
                        }
                        onSelectionChange(updated)
                    }
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = option,
                    fontFamily = font,
                    color = textColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

