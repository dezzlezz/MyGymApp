package com.example.mygymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.MuscleGroupStat

@Composable
fun MuscleGroupStatsChart(stats: List<MuscleGroupStat>) {
    if (stats.isEmpty()) return

    val max = stats.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

    stats.forEach { stat ->
        val fraction = stat.count.toFloat() / max.toFloat()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            androidx.compose.material3.Text(
                stat.group.display,
                modifier = Modifier.width(80.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .background(Color.Gray.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            Spacer(Modifier.width(8.dp))
            androidx.compose.material3.Text("${stat.count}")
        }
    }
}

