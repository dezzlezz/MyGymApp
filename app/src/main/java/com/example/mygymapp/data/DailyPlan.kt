// DailyPlan.kt
package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "daily_plans")
data class DailyPlan(
    @PrimaryKey val planId: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String
)
