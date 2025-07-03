package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?,
    val imageUri: String?,
    val tag: String,
    val rating: Int,
    val muscleGroup: String,
    val specificMuscle: String
)
