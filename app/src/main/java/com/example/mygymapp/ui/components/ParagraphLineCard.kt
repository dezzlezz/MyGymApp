package com.example.mygymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun ParagraphLineCard(
    line: Line,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val moodColor = when (line.mood.lowercase()) {
        "calm" -> Color(0xFFB3E5FC)
        "alert" -> Color(0xFFFFF9C4)
        "connected" -> Color(0xFFE1BEE7)
        "alive" -> Color(0xFFC8E6C9)
        "empty" -> Color(0xFFFFE0B2)
        "carried" -> Color(0xFFD7CCC8)
        "searching" -> Color(0xFFDCE775)
        else -> Color(0xFFFFF8E1)
    }.copy(alpha = 0.6f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = moodColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = line.title,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = GaeguBold, color = Color(0xFF3E2723))
            )
            Text(
                text = line.mood,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = GaeguRegular, color = Color(0xFF5D4037))
            )
            line.exercises.firstOrNull()?.let { first ->
                Text(
                    text = first.name,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = GaeguRegular, color = Color(0xFF5D4037))
                )
            }
        }
    }
}
