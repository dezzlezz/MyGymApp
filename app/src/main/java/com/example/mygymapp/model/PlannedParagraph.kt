package com.example.mygymapp.model

import java.time.LocalDate

/** Represents a paragraph with a chosen start date. */
data class PlannedParagraph(
    val paragraph: Paragraph,
    val startDate: LocalDate
)
