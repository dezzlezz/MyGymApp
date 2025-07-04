package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "daily_plan_exercises",
    primaryKeys = ["planId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = DailyPlan::class,
            parentColumns = ["planId"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["exerciseId"])]
)
data class DailyPlanExerciseCrossRef(
    val planId: String,
    val exerciseId: Long
)
