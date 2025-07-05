package com.example.mygymapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: ExerciseRepository
    val allExercises: LiveData<List<Exercise>>

    init {
        val dao = AppDatabase.getDatabase(application).exerciseDao()
        repo = ExerciseRepository(dao)
        allExercises = repo.getAllExercises().asLiveData()
    }

    fun insert(ex: Exercise) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertExercise(ex)
    }

    fun delete(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteExerciseById(id)
    }

    fun update(ex: Exercise) = viewModelScope.launch(Dispatchers.IO) {
        repo.updateExercise(ex)
    }

    fun getById(id: Long): Exercise? = repo.getExerciseById(id)
}
