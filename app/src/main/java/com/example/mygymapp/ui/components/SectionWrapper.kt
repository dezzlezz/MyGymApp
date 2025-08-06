package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.pages.GaeguBold

/**
 * A poetic wrapper for grouping exercises into a section (e.g., Warm-up, Workout, Cooldown).
 * Wraps its content in a softly styled card with a header label.
 */
@Composable
fun SectionWrapper(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    PoeticCard(modifier = modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            fontFamily = GaeguBold,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        content()
    }
}
