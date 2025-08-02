package com.example.mygymapp.ui.pages

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.components.LineCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun LinesPage(
    lines: List<Line>,
    onEdit: (Line) -> Unit,
    onArchive: (Line) -> Unit,
    onRestore: (Line) -> Unit,
    onUse: (Line) -> Unit,
    modifier: Modifier = Modifier
) {
    val (showArchived, setShowArchived) = remember { mutableStateOf(false) }
    val categories = listOf("All", "Push", "Pull", "Core", "Cardio", "Recovery")
    val (selectedCategory, setSelectedCategory) = remember { mutableStateOf("All") }

    PaperBackground(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("Active", "Archived").forEach { label ->
                    val selected = showArchived == (label == "Archived")
                    Text(
                        text = label,
                        fontFamily = GaeguRegular,
                        color = Color.Black.copy(alpha = if (selected) 1f else 0.5f),
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { setShowArchived(label == "Archived") }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { setSelectedCategory(cat) },
                        label = { Text(cat, fontFamily = GaeguRegular, color = Color.Black) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFFFFF8E1),
                            selectedContainerColor = Color(0xFFE0D4B7)
                        )
                    )
                }
            }
            val filtered = lines
                .filter { if (showArchived) it.isArchived else !it.isArchived }
                .filter { selectedCategory == "All" || it.category == selectedCategory }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { line ->
                    LineCard(
                        line = line,
                        onEdit = { onEdit(line) },
                        onArchive = { onArchive(line) },
                        onRestore = { onRestore(line) },
                        onUse = { onUse(line) }
                    )
                }
            }
        }
    }
}
