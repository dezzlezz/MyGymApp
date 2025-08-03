package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun LineCard(
    line: Line,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onUse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (line.isArchived) 0.5f else 1f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = line.title,
                fontFamily = GaeguBold,
                fontSize = 22.sp,
                color = Color.Black
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${line.category} · ${line.muscleGroup}",
                fontFamily = GaeguRegular,
                fontSize = 16.sp,
                color = Color.Black
            )
            if (line.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = line.note,
                    fontFamily = GaeguRegular,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Text(
                        "✏️ Edit",
                        fontFamily = GaeguRegular,
                        color = Color.Black,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
                TextButton(onClick = onArchive, modifier = Modifier.weight(1f)) {
                    Text(
                        "🗃 Archive",
                        fontFamily = GaeguRegular,
                        color = Color.Black,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
                TextButton(onClick = onUse, modifier = Modifier.weight(1f)) {
                    Text(
                        "➕ Use in Entry",
                        fontFamily = GaeguRegular,
                        color = Color.Black,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
