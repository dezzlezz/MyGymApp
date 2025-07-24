package com.example.mygymapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate

@Composable
fun BookmarkMenu(
    isOpen: Boolean,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .wrapContentWidth()
            .padding(start = 38.dp, top = 8.dp)
    ) {
        // 📜 Menü klappt sich aus
        AnimatedVisibility(visible = isOpen) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF2EDE3))
                    .padding(12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .widthIn(min = 180.dp)
            ) {
                listOf(
                    "Today's Page",
                    "Table of Contents",
                    "Lines & Paragraphs",
                    "Chronicle",
                    "Impressum"
                ).forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(label) }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif)
                    )
                }
            }
        }

        // 📌 Lesezeichen mit Stoffband
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Hauptbereich oben
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(96.dp)
                    .clip(RoundedCornerShape(bottomEnd = 14.dp))
                    .background(Color(0xFF3F4E3A))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isOpen) Icons.Default.Close else Icons.Default.MenuBook,
                    contentDescription = "Toggle Menu",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // 🧵 Quaste erscheint nur, wenn geöffnet
            if (isOpen) {
                BookmarkRibbon(
                    modifier = Modifier
                        .padding(top = 0.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
