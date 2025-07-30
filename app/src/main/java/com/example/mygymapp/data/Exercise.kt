package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val category: ExerciseCategory,
    val customCategory: String? = null,
    val likeability: Int,
    val muscleGroup: MuscleGroup,
    val muscle: String,
    val imageUri: String? = null,
    val isFavorite: Boolean = false
)

