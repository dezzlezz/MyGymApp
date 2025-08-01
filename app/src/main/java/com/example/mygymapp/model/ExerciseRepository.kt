package com.example.mygymapp.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object ExerciseRepository {
    private val _exercises = MutableStateFlow(
        listOf(
            Exercise(id = 100, name = "Push-ups", sets = 3, repsOrDuration = "12"),
            Exercise(id = 101, name = "Plank", sets = 3, repsOrDuration = "30s")
        )
    )
    val exercises: StateFlow<List<Exercise>> = _exercises

    fun add(ex: Exercise) {
        _exercises.update { it + ex }
    }
}
