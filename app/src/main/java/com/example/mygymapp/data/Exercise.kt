// src/main/java/com/example/mygymapp/data/Exercise.kt
package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val reps: Int,
    val sets: Int
)
