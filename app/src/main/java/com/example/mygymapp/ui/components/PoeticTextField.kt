package com.example.mygymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.example.mygymapp.ui.theme.AppColors
import com.example.mygymapp.ui.theme.AppPadding
import com.example.mygymapp.ui.theme.AppShapes
import com.example.mygymapp.ui.theme.AppTypography

/**
 * A calm text field resembling a handwritten note area.
 */
@Composable
fun PoeticTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = "What was quiet? What was loud?",
    textStyle: TextStyle = AppTypography.Body,
    hintStyle: TextStyle = AppTypography.Hint,
    minLines: Int = 4,
    maxLines: Int = 12
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.Small)
            .background(AppColors.Paper)
            .padding(AppPadding.Element)
    ) {
        if (value.isEmpty()) {
            Text(
                text = hint,
                style = hintStyle,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            cursorBrush = SolidColor(AppColors.DeepText),
            maxLines = maxLines,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = (minLines * 24).dp)
        )
    }
}

