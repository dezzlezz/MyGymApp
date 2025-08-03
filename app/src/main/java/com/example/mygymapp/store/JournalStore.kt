package com.example.mygymapp.store

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.model.Entry

object JournalStore {
    val allLines = mutableStateListOf<Line>()
    val allParagraphs = mutableStateListOf<Paragraph>()
    val currentEntry = mutableStateOf<Entry?>(null)
    val activeParagraph = mutableStateOf<Paragraph?>(null)
}
