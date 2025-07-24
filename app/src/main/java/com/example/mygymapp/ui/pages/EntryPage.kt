@file:OptIn(ExperimentalLayoutApi::class)
package com.example.mygymapp.ui.pages
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.ui.components.EntryHeader
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.theme.handwritingText
import androidx.compose.ui.draw.clip
import java.time.LocalDate
import androidx.compose.foundation.layout.ExperimentalLayoutApi


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EntryPage(
    entryNumber: Int,
    onFinished: () -> Unit
) {
    val today = LocalDate.now()

    var mood by remember { mutableStateOf<String?>(null) }
    val moods = listOf("calm", "alert", "connected", "alive", "empty", "carried", "searching")
    var story by remember { mutableStateOf("") }

    PaperBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        EntryHeader(
            entryNumber = entryNumber,
            date = today
        )

        val emotionColors = listOf(
            Color(0xFFFFCDD2),
            Color(0xFFBBDEFB),
            Color(0xFFC8E6C9),
            Color(0xFFFFF9C4),
            Color(0xFFD7CCC8),
            Color(0xFFD1C4E9),
            Color(0xFFFFE0B2)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            moods.forEachIndexed { index, option ->
                val selected = mood == option
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(emotionColors[index % emotionColors.size])
                        .border(
                            width = if (selected) 3.dp else 1.dp,
                            color = if (selected) Color.Black else Color.DarkGray,
                            shape = CircleShape
                        )
                        .clickable { mood = option },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.take(1).uppercase(),
                        color = Color.Black,
                        fontFamily = FontFamily.Serif,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Text(
            text = "Today: Push · 3 movements · 34 minutes",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
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
            onClick = {
                mood = null
                story = ""
                onFinished()
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Finish this page", color = Color.Black)
        }
        }
    }
}
