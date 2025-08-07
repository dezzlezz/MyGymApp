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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.pages.GaeguRegular

/**
 * A poetic set of radio-style choice chips for selecting exactly one option.
 * Styled softly with rounded corners and font-family support for a book-like feel.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PoeticRadioChips(
    options: List<String>,
    selected: String?,
    onSelected: (String) -> Unit,
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
            val isSelected = option == selected
            Surface(
                color = if (isSelected) selectedBackground else unselectedBackground,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .clickable { onSelected(option) }
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

