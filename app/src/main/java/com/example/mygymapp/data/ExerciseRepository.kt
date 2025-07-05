package com.example.mygymapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ExerciseRepository(
    private val dao: ExerciseDao
) {
    fun getAllExercises(): Flow<List<Exercise>> = dao.getAllExercises()

    // hier mit Coroutines auf IO-Thread schalten
    suspend fun insert(exercise: Exercise) = withContext(Dispatchers.IO) {
        dao.insert(exercise)
    }

    suspend fun delete(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }
}
