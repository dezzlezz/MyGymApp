package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.pages.GaeguBold

/**
 * A poetic, book-themed button with Gaegu font and app-specific styling.
 * Designed for consistent use across the app (e.g., for actions like "Write", "Save", "Back").
 */
@Composable
fun GaeguButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    font: FontFamily = GaeguBold,
    backgroundColor: Color = Color(0xFF3F4E3A),
    textColor: Color = Color.White,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    shape: Shape = RoundedCornerShape(12.dp)
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = shape,
        contentPadding = contentPadding,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            fontFamily = font,
            color = textColor,
            fontSize = 16.sp
        )
    }
}

