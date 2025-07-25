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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line

@Composable
fun LineCard(
    line: Line,
    onEdit: () -> Unit,
    onAddToToday: () -> Unit,
    onArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = line.title,
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${line.category} · ${line.muscleGroup} · ${line.mood}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${line.exercises.size} exercises · ${line.supersets.size} superset${if (line.supersets.size == 1) "" else "s"}",
                style = MaterialTheme.typography.bodySmall
            )
            if (line.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                val preview = line.note.take(60)
                Text(
                    text = "📎 $preview",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) { Text("✏️ Edit") }
                TextButton(onClick = onAddToToday) { Text("📥 Add") }
                TextButton(onClick = onArchive) { Text("📦 Archive") }
            }
        }
    }
}
