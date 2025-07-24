@file:OptIn(ExperimentalLayoutApi::class)
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.theme.handwritingText
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.EntryHeader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.layout.ExperimentalLayoutApi


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EntryPage() {
    val today = LocalDate.now()
    val entryNumber = 446
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)

    var mood by remember { mutableStateOf<String?>(null) }
    val moods = listOf("calm", "alert", "connected", "alive", "empty", "carried", "searching")
    var story by remember { mutableStateOf("") }
    var isFinished by remember { mutableStateOf(false) }

    PaperBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        EntryHeader(
            entryNumber = entryNumber,
            date = today
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
            textStyle = handwritingText
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
}
