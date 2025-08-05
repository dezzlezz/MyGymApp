package com.example.mygymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .background(
                color = Color(0xFFF5F5DC),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            fontFamily = GaeguBold,
            fontSize = 18.sp,
            color = Color(0xFF3E2723),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        content()
    }
}
