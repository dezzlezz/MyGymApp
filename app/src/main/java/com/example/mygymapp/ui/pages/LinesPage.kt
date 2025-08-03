package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground

@Composable
fun LinesPage(
    lines: List<Line>,
    onEdit: (Line) -> Unit,
    onArchive: (Line) -> Unit,
    onUse: (Line) -> Unit,
    modifier: Modifier = Modifier
) {
    PaperBackground(modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lines) { line ->
                LineCard(
                    line = line,
                    onEdit = { onEdit(line) },
                    onArchive = { onArchive(line) },
                    onUse = { onUse(line) }
                )
            }
        }
    }
}
