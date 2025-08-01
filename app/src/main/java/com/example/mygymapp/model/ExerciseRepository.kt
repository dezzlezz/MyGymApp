package com.example.mygymapp.model

import com.example.mygymapp.data.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * Simple in-memory repository used for preview and editor screens.
 * It exposes a list of [Exercise] objects that both the management
 * screen and the line editor can observe.
 */
object ExerciseRepository {
    private val _exercises = MutableStateFlow(
        listOf(
            Exercise(
                id = 100,
                name = "Push-ups",
                description = "",
                category = ExerciseCategory.Gym,
                likeability = 3,
                muscleGroup = MuscleGroup.Chest,
                muscle = "Chest",
                isFavorite = false
            ),
            Exercise(
                id = 101,
                name = "Plank",
                description = "",
                category = ExerciseCategory.Calisthenics,
                likeability = 3,
                muscleGroup = MuscleGroup.Core,
                muscle = "Core",
                isFavorite = false
            )
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
