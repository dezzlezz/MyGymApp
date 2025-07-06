// app/src/main/java/com/example/mygymapp/data/WeeklyPlanExerciseCrossRef.kt
package com.example.mygymapp.data

import androidx.room.Entity

@Entity(
    tableName = "weekly_plan_exercises",
    primaryKeys = ["dayId", "exerciseId"]
)
data class WeeklyPlanExerciseCrossRef(
    val dayId: Long,
    val exerciseId: Long,
    val sets: Int,
    val reps: Int,
    val `order`: Int
)
