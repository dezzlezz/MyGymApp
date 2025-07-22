package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EntryPage() {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
    val entryNumber = 446

    var mood by remember { mutableStateOf<String?>(null) }
    val moods = listOf("calm", "alert", "connected", "alive", "empty", "carried", "searching")
    var story by remember { mutableStateOf("") }
    var isFinished by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Entry $entryNumber · ${today.format(formatter)}",
            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
            textAlign = TextAlign.Center
        )

        Text(
            text = "What tone colors your day?",
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            moods.forEach { option ->
                AssistChip(
                    onClick = { mood = option },
                    label = { Text(option) },
                    selected = mood == option
                )
            }
        }

        Text(
            text = "Today: Push · 3 movements · 34 minutes",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = story,
            onValueChange = { story = it },
            placeholder = {
                Text("What was quiet, what loud? What moved through you?")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Cursive,
                lineHeight = 22.sp
            )
        )

        Button(
            onClick = { isFinished = true },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Finish this page")
        }

        if (isFinished) {
            Text(
                text = "A new page was written.",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
