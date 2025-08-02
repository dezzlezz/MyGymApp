package com.example.mygymapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.R
import com.example.mygymapp.model.Line
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular

@Composable
fun LineCard(
    line: Line,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    onRestore: () -> Unit,
    onUse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fade by animateFloatAsState(if (line.isArchived) 0.5f else 1f, label = "fade")
    val textColor = Color(0xFF5D4037)
    val buttonBackground = Color(0xFFFFF8E1)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .alpha(fade),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.background_parchment),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(20.dp)
            ) {
                Text(
                    text = line.title,
                    fontFamily = GaeguBold,
                    fontSize = 24.sp,
                    color = textColor
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${line.category} · ${line.muscleGroup}",
                    fontFamily = GaeguRegular,
                    fontSize = 14.sp,
                    color = textColor
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "• ${line.exercises.size} exercises",
                    fontFamily = GaeguRegular,
                    fontSize = 14.sp,
                    color = textColor
                )
                if (line.supersets.isNotEmpty()) {
                    Text(
                        text = "• ${line.supersets.size} superset${if (line.supersets.size == 1) "" else "s"}",
                        fontFamily = GaeguRegular,
                        fontSize = 14.sp,
                        color = textColor
                    )
                }
                if (line.note.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "📎 ${line.note}",
                        fontFamily = GaeguRegular,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onEdit,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = buttonBackground,
                            contentColor = textColor
                        )
                    ) {
                        Text("✏ Edit", fontFamily = GaeguRegular, fontSize = 14.sp)
                    }
                    if (line.isArchived) {
                        TextButton(
                            onClick = onRestore,
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = buttonBackground,
                                contentColor = textColor
                            )
                        ) {
                            Text("🧷 Restore", fontFamily = GaeguRegular, fontSize = 14.sp)
                        }
                    } else {
                        TextButton(
                            onClick = onArchive,
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = buttonBackground,
                                contentColor = textColor
                            )
                        ) {
                            Text("🗃 Archive", fontFamily = GaeguRegular, fontSize = 14.sp)
                        }
                        TextButton(
                            onClick = onUse,
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = buttonBackground,
                                contentColor = textColor
                            )
                        ) {
                            Text("🧾 Use in Entry", fontFamily = GaeguRegular, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
