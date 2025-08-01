package com.example.mygymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.mygymapp.ui.pages.GaeguLight

@Composable
fun PoeticLineCard(
    line: Line,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val baseColor = Color(0xFFFFF8E1)
    val selectedColor = Color(0xFFF0E2C2)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) selectedColor else baseColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = line.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = GaeguBold,
                    color = Color(0xFF3E2723)
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${line.category} · ${line.muscleGroup}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = GaeguLight,
                    color = Color(0xFF5D4037)
                )
            )
            Spacer(Modifier.height(2.dp))
            line.exercises.firstOrNull()?.let { first ->
                val more = if (line.exercises.size > 1) " ... ➝" else ""
                Text(
                    text = first.name + more,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = GaeguLight,
                        color = Color(0xFF5D4037)
                    )
                )
            }
        }
    }
}

