package com.example.mygymapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel exposing exercises from the [ExerciseRepository].
 * Both the management screen and the line editor share this instance
 * so that they operate on the same data source.
 */
class ExerciseViewModel : ViewModel() {

    val exercises: StateFlow<List<Exercise>> = ExerciseRepository.exercises

    fun insert(ex: Exercise) {
        ExerciseRepository.add(ex)
    }

    fun delete(id: Long) {
        ExerciseRepository.delete(id)
    }

    fun update(ex: Exercise) {
        ExerciseRepository.update(ex)
    }

    fun toggleFavorite(ex: Exercise) {
        ExerciseRepository.toggleFavorite(ex)
    }


    suspend fun getById(id: Long): Exercise? {
        return ExerciseRepository.getById(id)
    }
}
