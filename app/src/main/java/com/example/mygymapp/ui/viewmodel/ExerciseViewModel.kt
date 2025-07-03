package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExerciseRepository
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repo: ExerciseRepository) : ViewModel() {
    val exercises: LiveData<List<Exercise>> = repo.allExercises
    fun insert(ex: Exercise) {
        viewModelScope.launch { repo.add(ex) }
    }
}

class ExerciseViewModelFactory(private val repo: ExerciseRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
