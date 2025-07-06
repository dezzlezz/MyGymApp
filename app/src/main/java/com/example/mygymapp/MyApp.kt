package com.example.mygymapp

import android.app.Application
import com.example.mygymapp.data.AppDatabase

class MyApp : Application() {

    // Datenbank-Instanz (Singleton)
    private val database by lazy { AppDatabase.getDatabase(this) }

    // Nur noch ExerciseDao zum globalen Zugriff
    val exerciseDao
        get() = database.exerciseDao()

    override fun onCreate() {
        super.onCreate()
        // Optional: Initialisierungen oder Logging
    }
}
