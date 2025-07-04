package com.example.mygymapp.data

import androidx.lifecycle.LiveData

class ExerciseRepository(private val dao: ExerciseDao) {
    suspend fun add(exercise: Exercise) {
        dao.insert(exercise)
    }
}

