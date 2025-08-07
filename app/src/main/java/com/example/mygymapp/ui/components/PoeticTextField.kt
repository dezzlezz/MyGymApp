package com.example.mygymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.mygymapp.ui.theme.AppColors
import com.example.mygymapp.ui.theme.AppPadding
import com.example.mygymapp.ui.theme.AppShapes
import com.example.mygymapp.ui.theme.AppTypography
import androidx.compose.ui.draw.clip

@Composable
fun PoeticTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "What did you reflect on today?",
    minLines: Int = 4,
    maxLines: Int = 12
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = hint,
                style = AppTypography.Hint
            )
        },
        textStyle = AppTypography.Body,
        shape = AppShapes.Card,
        minLines = minLines,
        maxLines = maxLines,
        modifier = modifier
            .fillMaxWidth()
            .clip(AppShapes.Card)
            .background(Color(0xFFDAD2BD)) // zarter papierfarbener Hintergrund
            .padding(AppPadding.Element),
        colors = TextFieldDefaults.colors(
            focusedTextColor = AppColors.DeepText,
            unfocusedTextColor = AppColors.DeepText,
            disabledTextColor = AppColors.SubtleText,
            errorTextColor = AppColors.ErrorMaroon,

            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,

            cursorColor = AppColors.DeepText,
            errorCursorColor = AppColors.ErrorMaroon,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = AppColors.ErrorMaroon,

            focusedPlaceholderColor = AppColors.SubtleText,
            unfocusedPlaceholderColor = AppColors.SubtleText,
            disabledPlaceholderColor = AppColors.SubtleText,
            errorPlaceholderColor = AppColors.ErrorMaroon
        )
    )

}

