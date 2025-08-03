package com.example.mygymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.PoeticCard
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun ParagraphLineCard(
    line: Line,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    PoeticCard(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Text(
            text = line.title,
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = GaeguBold, color = Color(0xFF3E2723))
        )
        line.exercises.firstOrNull()?.let { first ->
            Text(
                text = first.name,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = GaeguRegular, color = Color(0xFF5D4037))
            )
        }
    }
}
