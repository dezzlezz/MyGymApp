package com.example.mygymapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Wenn du kein AndroidViewModel willst, kannst du auch ViewModel + Factory nutzen
class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    // 1) Repository initialisieren
    private val repository: ExerciseRepository

    // 2) allExercises als LiveData, basierend auf DAO-Flow
    val allExercises: LiveData<List<Exercise>>

    init {
        // Datenbank-Instanz holen
        val dao = AppDatabase
            .getDatabase(application)
            .exerciseDao()
        repository = ExerciseRepository(dao)

        // Flow -> LiveData
        allExercises = repository
            .getAllExercises()    // <-- DAO muss diese Methode liefern
            .asLiveData()         // aus room-ktx
    }

    // 3) Insert im IO-Thread
    fun insert(exercise: Exercise) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(exercise)
    }
}
