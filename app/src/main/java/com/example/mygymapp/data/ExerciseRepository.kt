package com.example.mygymapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ExerciseRepository(private val dao: ExerciseDao) {
    fun getAll() = dao.getAllExercises()

    suspend fun add(ex: Exercise) = withContext(Dispatchers.IO) {
        dao.insert(ex)
    }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    fun getAllExercises(): Flow<List<Exercise>> = dao.getAllExercises()
    suspend fun insert(exercise: Exercise) = dao.insert(exercise)
}
