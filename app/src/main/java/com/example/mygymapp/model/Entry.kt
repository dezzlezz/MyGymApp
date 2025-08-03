package com.example.mygymapp.model

import java.time.LocalDate

/** Represents a daily journal entry. */
data class Entry(
    val id: Long,
    val date: LocalDate,
    val paragraphs: List<Paragraph> = emptyList()
)
