package com.example.mygymapp.model

import java.time.LocalDate

/** Simple log entry representing reps completed for an exercise on a given date. */
data class ExerciseLogEntry(
    val date: LocalDate,
    val exerciseId: Long,
    val reps: Int
)
