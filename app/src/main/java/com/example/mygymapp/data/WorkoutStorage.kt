package com.example.mygymapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.mygymapp.model.WeekProgress

class WorkoutStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("workout_prefs", Context.MODE_PRIVATE)

    fun load(): WeekProgress? {
        val id = prefs.getLong("planId", -1L)
        if (id == -1L) return null
        val rest = prefs.getInt("restDay", -1)
        val modularId = prefs.getLong("modPlan", -1L).takeIf { it != -1L }
        val modRest = prefs.getBoolean("modRest", false)
        val day = prefs.getInt("day", 0)
        return WeekProgress(id, rest, modularId, modRest, day)
    }

    fun save(progress: WeekProgress) {
        prefs.edit().apply {
            putLong("planId", progress.weeklyPlanId)
            putInt("restDay", progress.restDay)
            if (progress.modularPlanId != null) putLong("modPlan", progress.modularPlanId) else remove("modPlan")
            putBoolean("modRest", progress.modularRest)
            putInt("day", progress.day)
        }.apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}