package com.example.mygymapp.data

import java.time.LocalDate

data class WorkoutHistoryEntry(
    val date: LocalDate,
    val planId: Long,
    val dayIndex: Int
)