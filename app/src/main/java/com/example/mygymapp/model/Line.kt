package com.example.mygymapp.model

import com.example.mygymapp.model.Exercise
import com.example.mygymapp.model.Mood

/**
 * Represents a single daily workout "Line" in the training diary.
 */
data class Line(
    val id: Long,
    val title: String,
    val category: String,
    val muscleGroup: String,
    val mood: Mood? = null,
    val exercises: List<Exercise>,
    val supersets: List<List<Long>>, // group of exercise ids forming a superset
    val note: String,
    val isArchived: Boolean = false
)
