package com.example.mygymapp.data

import androidx.room.Entity

@Entity(primaryKeys = ["planId", "exerciseId", "dayIndex"])
data class PlanExerciseCrossRef(
    val planId: Long,
    val exerciseId: Long,
    val sets: Int,
    val reps: Int,
    val orderIndex: Int,
    val dayIndex: Int = 0
)
