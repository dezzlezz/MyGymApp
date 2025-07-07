package com.example.mygymapp.data

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

class WorkoutHistoryStorage private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("workout_history", Context.MODE_PRIVATE)

    fun add(entry: WorkoutHistoryEntry) {
        prefs.edit().putString(entry.date.toString(), "${entry.planId};${entry.dayIndex}").apply()
    }

    fun loadAll(): Map<LocalDate, WorkoutHistoryEntry> =
        prefs.all.mapNotNull { (key, value) ->
            val str = value as? String ?: return@mapNotNull null
            val parts = str.split(";")
            if (parts.size != 2) return@mapNotNull null
            val planId = parts[0].toLongOrNull() ?: return@mapNotNull null
            val day = parts[1].toIntOrNull() ?: return@mapNotNull null
            LocalDate.parse(key) to WorkoutHistoryEntry(LocalDate.parse(key), planId, day)
        }.toMap()

    companion object {
        @Volatile
        private var INSTANCE: WorkoutHistoryStorage? = null

        fun getInstance(context: Context): WorkoutHistoryStorage {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WorkoutHistoryStorage(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}