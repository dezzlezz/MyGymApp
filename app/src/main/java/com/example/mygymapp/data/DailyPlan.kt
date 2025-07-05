package com.example.mygymapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_plans")
data class DailyPlan(
    @PrimaryKey
    @ColumnInfo(name = "planId")
    val planId: String,
    val name: String,
    val description: String
)
