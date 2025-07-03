package com.example.mygymapp.data

import androidx.lifecycle.LiveData

class ExerciseRepository(private val dao: ExerciseDao) {
    val allExercises: LiveData<List<Exercise>> = dao.getAll()
    suspend fun add(ex: Exercise) = dao.insert(ex)
}
