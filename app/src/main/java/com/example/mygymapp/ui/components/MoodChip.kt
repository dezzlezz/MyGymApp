package com.example.mygymapp.ui.components

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
    modifier: Modifier = Modifier
) {
    val label = mood.name.lowercase().replaceFirstChar { it.uppercase() }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE0D8C8)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontFamily = FontFamily.Serif,
            color = Color.Black
        )
    }
}

