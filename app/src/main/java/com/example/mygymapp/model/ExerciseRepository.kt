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

    /** Public stream of all available exercises. */
    val exercises: StateFlow<List<Exercise>> = _exercises

    /** Add a new exercise to the list. */
    fun add(ex: Exercise) {
        _exercises.update { it + ex }
    }

    /** Replace an existing exercise with an updated version. */
    fun update(ex: Exercise) {
        _exercises.update { list ->
            list.map { if (it.id == ex.id) ex else it }
        }
    }

    /** Remove the exercise with the given id. */
    fun delete(id: Long) {
        _exercises.update { list -> list.filterNot { it.id == id } }
    }

    /** Toggle the favourite flag for the given exercise. */
    fun toggleFavorite(ex: Exercise) {
        update(ex.copy(isFavorite = !ex.isFavorite))
    }

    /** Return the exercise with the given id, if present. */
    fun getById(id: Long): Exercise? {
        return _exercises.value.find { it.id == id }
    }
}
