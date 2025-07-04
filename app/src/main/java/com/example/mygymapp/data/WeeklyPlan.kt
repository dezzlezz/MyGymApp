// WeeklyPlan.kt
package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "weekly_plans")
data class WeeklyPlan(
    @PrimaryKey val planId: String = UUID.randomUUID().toString(),
    val dayOfWeek: Int,  // 1 = Monday … 7 = Sunday
    val name: String
)
