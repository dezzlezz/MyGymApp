package com.example.mygymapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Mood

/** Simple chip displaying the current mood. */
@Composable
fun MoodChip(
    mood: Mood,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val label = mood.name.lowercase().replaceFirstChar { it.uppercase() }
    val color = when (mood) {
        Mood.CALM -> Color(0xFFD0E8F2)
        Mood.FOCUSED -> Color(0xFFE8E0F7)
        Mood.BALANCED -> Color(0xFFE0F2E9)
        Mood.SEARCHING -> Color(0xFFF7E0E0)
    }
    Surface(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        color = color,
        border = if (selected) BorderStroke(2.dp, Color.Black) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontFamily = FontFamily.Serif,
            color = Color.Black
        )
    }
}

