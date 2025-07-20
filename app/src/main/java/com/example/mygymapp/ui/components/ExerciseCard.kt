package com.example.mygymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.mygymapp.data.Exercise

@Composable
fun ExerciseCard(
    ex: Exercise,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .defaultMinSize(minHeight = 80.dp)
            .indication(interactionSource, LocalIndication.current)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(ex.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${ex.muscleGroup.display} • ${ex.category.display}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (ex.isFavorite) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                    contentDescription = null
                )
            }
        }
    }
}
