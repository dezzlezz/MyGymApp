package com.example.mygymapp.data

import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val dao: ExerciseDao) {
    fun getAllExercises(): Flow<List<Exercise>> = dao.getAllExercises()
    fun insert(ex: Exercise): Long = dao.insert(ex)
    fun delete(id: Long) = dao.deleteById(id)
}
