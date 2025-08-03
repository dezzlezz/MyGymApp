package com.example.mygymapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.mygymapp.model.Entry
import com.example.mygymapp.model.Mood

/** Card showing a journal entry summary. */
@Composable
fun EntryCard(
    entry: Entry,
    mood: Mood? = null,
    modifier: Modifier = Modifier
) {
    PoeticCard(modifier = modifier) {
        Text(
            text = "Entry on ${entry.date}",
            fontFamily = FontFamily.Serif,
            color = Color.Black
        )
        mood?.let {
            Spacer(modifier = Modifier.height(8.dp))
            MoodChip(mood = it)
        }
    }
}

