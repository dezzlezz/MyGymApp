package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlanExerciseCrossRef(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val planId: Long,
    val exerciseId: Long,
    val sets: Int,
    val reps: Int,
    val orderIndex: Int,
    val dayIndex: Int = 0
)
