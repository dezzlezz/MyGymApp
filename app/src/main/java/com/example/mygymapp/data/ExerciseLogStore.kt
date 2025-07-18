package com.example.mygymapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.mygymapp.model.ExerciseLogEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

/** Stores exercise log entries in SharedPreferences. */
class ExerciseLogStore private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("exercise_log", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val type = object : TypeToken<List<ExerciseLogEntry>>() {}.type

    fun log(exerciseId: Long, reps: Int) {
        val key = "log_$exerciseId"
        val list = load(exerciseId).toMutableList()
        list.add(ExerciseLogEntry(LocalDate.now(), exerciseId, reps))
        prefs.edit().putString(key, gson.toJson(list)).apply()
    }

    fun load(exerciseId: Long): List<ExerciseLogEntry> {
        val json = prefs.getString("log_$exerciseId", null) ?: return emptyList()
        return gson.fromJson(json, type)
    }

    companion object {
        @Volatile private var INSTANCE: ExerciseLogStore? = null

        fun getInstance(context: Context): ExerciseLogStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ExerciseLogStore(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
