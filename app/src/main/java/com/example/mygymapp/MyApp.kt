package com.example.mygymapp

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Exercise
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MyApp : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "mygymapp.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        prepopulateExercisesFromAssets(this)
    }

    private fun prepopulateExercisesFromAssets(context: Context) {
        val dao = database.exerciseDao()
        CoroutineScope(Dispatchers.IO).launch {
            val existing = dao.getAllExercises().firstOrNull()
            if (!existing.isNullOrEmpty()) return@launch

            val input = context.assets.open("default_exercises.json")
            val json = input.bufferedReader().use { it.readText() }

            val gson = Gson()
            val type = object : TypeToken<List<Exercise>>() {}.type
            val exercises: List<Exercise> = gson.fromJson(json, type)

            exercises.forEach { dao.insert(it) }
        }
    }
}
