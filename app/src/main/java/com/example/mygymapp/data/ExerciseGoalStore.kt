package com.example.mygymapp.data

import android.content.Context
import android.content.SharedPreferences

class ExerciseGoalStore private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("exercise_goals", Context.MODE_PRIVATE)

    fun getGoal(exerciseId: Long): Int = prefs.getInt("goal_${'$'}exerciseId", 0)

    fun setGoal(exerciseId: Long, target: Int) {
        prefs.edit().putInt("goal_${'$'}exerciseId", target).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: ExerciseGoalStore? = null

        fun getInstance(context: Context): ExerciseGoalStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ExerciseGoalStore(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
