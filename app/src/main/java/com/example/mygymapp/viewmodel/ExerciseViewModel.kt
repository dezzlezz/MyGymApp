package com.example.mygymapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.InMemoryExerciseRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel exposing exercises from the [InMemoryExerciseRepository].
 * Both the management screen and the line editor share this instance
 * so that they operate on the same data source.
 */
class ExerciseViewModel : ViewModel() {

    val exercises: StateFlow<List<Exercise>> = InMemoryExerciseRepository.exercises

    fun insert(ex: Exercise) {
        InMemoryExerciseRepository.add(ex)
    }

    fun delete(id: Long) {
        InMemoryExerciseRepository.delete(id)
    }

    fun update(ex: Exercise) {
        InMemoryExerciseRepository.update(ex)
    }

    fun toggleFavorite(ex: Exercise) {
        InMemoryExerciseRepository.toggleFavorite(ex)
    }


    suspend fun getById(id: Long): Exercise? {
        return InMemoryExerciseRepository.getById(id)
    }
}
