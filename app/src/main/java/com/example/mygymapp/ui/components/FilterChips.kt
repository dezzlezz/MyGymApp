package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterChips(
    items: List<String>,
    selected: String?,
    onSelected: (String?) -> Unit
) {
    Row {
        AssistChip(
            onClick = { onSelected(null) },
            label = { Text("All") },
            colors = AssistChipDefaults.assistChipColors()
        )
        Spacer(Modifier.width(8.dp))
        items.forEach { item ->
            AssistChip(
                onClick = { onSelected(item) },
                label = { Text(item) },
                colors = AssistChipDefaults.assistChipColors()
            )
            Spacer(Modifier.width(8.dp))
        }
    }
}
