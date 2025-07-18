package com.example.mygymapp.data

import android.content.Context
import android.content.SharedPreferences

class ExercisePRStore private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("exercise_pr", Context.MODE_PRIVATE)

    fun getPR(exerciseId: Long): Int = prefs.getInt("pr_$exerciseId", 0)

    fun updateIfHigher(exerciseId: Long, newReps: Int) {
        val current = getPR(exerciseId)
        if (newReps > current) {
            prefs.edit().putInt("pr_$exerciseId", newReps).apply()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ExercisePRStore? = null

        fun getInstance(context: Context): ExercisePRStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ExercisePRStore(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
