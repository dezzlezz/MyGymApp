package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String,
    val category: ExerciseCategory,
    val likeability: Int,
    val muscleGroup: MuscleGroup,
    val muscle: String
)
