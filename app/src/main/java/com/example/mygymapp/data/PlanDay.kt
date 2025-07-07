package com.example.mygymapp.data

import androidx.room.Entity

@Entity(primaryKeys = ["planId", "dayIndex"], tableName = "plan_day")
data class PlanDay(
    val planId: Long,
    val dayIndex: Int,
    val name: String
)