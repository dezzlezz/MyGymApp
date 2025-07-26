package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import com.example.mygymapp.model.Line
import androidx.compose.runtime.getValue

@Composable
fun LineCard(
    line: Line,
    onEdit: () -> Unit,
    onAdd: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fade by animateFloatAsState(if (line.isArchived) 0f else 1f, label = "fade")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(fade),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = line.title,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${line.category} · ${line.muscleGroup} · ${line.mood}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${line.exercises.size} exercises · ${line.supersets.size} superset${if (line.supersets.size == 1) "" else "s"}",
                style = MaterialTheme.typography.bodySmall
            )
            if (line.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📎 ${line.note}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onEdit) { Text("✏️ Edit") }
                TextButton(onClick = onAdd) { Text("📥 Add") }
                TextButton(onClick = onArchive) { Text("📦 Archive") }
            }
        }
    }
}
