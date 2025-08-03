package com.example.mygymapp.store

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.mygymapp.model.Entry
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Mood
import com.example.mygymapp.model.Paragraph

object JournalStore {
    val currentEntry = mutableStateOf<Entry?>(null)
    val allLines = mutableStateListOf<Line>()
    val allParagraphs = mutableStateListOf<Paragraph>()
    val activeParagraph = mutableStateOf<Paragraph?>(null)
    val currentMood = mutableStateOf<Mood?>(null)
}
