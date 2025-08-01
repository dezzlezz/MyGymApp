package com.example.mygymapp.model

/**
 * Represents a weekly "Paragraph" made up of seven training lines.
 */
data class Paragraph(
    val id: Long,
    val title: String,
    val lineTitles: List<String>,
    val note: String = ""
)
