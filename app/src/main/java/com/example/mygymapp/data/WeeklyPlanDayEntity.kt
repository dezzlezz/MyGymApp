// app/src/main/java/com/example/mygymapp/data/WeeklyPlanDayEntity.kt
package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_plan_days")
data class WeeklyPlanDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    /** Verweis auf den zugehörigen Plan */
    val planId: Long,

    /** Index 0–4 für Tag 1–5 */
    val dayIndex: Int,

    /** z.B. "Push", "Pull", "Legs" */
    val name: String
)
