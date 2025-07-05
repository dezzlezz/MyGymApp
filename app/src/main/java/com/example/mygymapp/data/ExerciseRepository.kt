package com.example.mygymapp.data

import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun insertExercise(exercise: Exercise): Long = exerciseDao.insert(exercise)

    fun updateExercise(exercise: Exercise): Int = exerciseDao.update(exercise)

    fun deleteExerciseById(id: Long): Int = exerciseDao.deleteById(id)

    fun getExerciseById(id: Long): Exercise? = exerciseDao.getById(id)
}
