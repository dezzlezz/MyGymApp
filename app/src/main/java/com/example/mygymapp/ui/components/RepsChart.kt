package com.example.mygymapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.ExerciseLogEntry

@Composable
fun RepsChart(entries: List<ExerciseLogEntry>, modifier: Modifier = Modifier.height(180.dp)) {
    if (entries.isEmpty()) return

    val maxReps = entries.maxOf { it.reps }.coerceAtLeast(1)
    val points = entries.mapIndexed { index, entry -> index to entry.reps }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val w = size.width
        val h = size.height
        val pointGap = if (points.size > 1) w / (points.size - 1) else 0f

        val path = Path()
        points.forEachIndexed { i, (_, reps) ->
            val x = i * pointGap
            val y = h - (reps.toFloat() / maxReps) * h
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = MaterialTheme.colorScheme.primary,
            style = Stroke(width = 4f)
        )

        points.forEachIndexed { i, (_, reps) ->
            val x = i * pointGap
            val y = h - (reps.toFloat() / maxReps) * h
            drawCircle(
                color = MaterialTheme.colorScheme.primary,
                center = Offset(x, y),
                radius = 6f
            )
        }
    }
}

