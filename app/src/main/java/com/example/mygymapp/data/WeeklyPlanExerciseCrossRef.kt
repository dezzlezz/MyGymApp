// WeeklyPlanExerciseCrossRef.kt
package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "weekly_plan_exercises",
    primaryKeys = ["planId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = WeeklyPlan::class,
            parentColumns = ["planId"],    // muss mit dem PK-Feld in WeeklyPlan übereinstimmen
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
    indices = [ Index(value = ["exerciseId"]) ]
)
data class WeeklyPlanExerciseCrossRef(
    val planId: String,
    val exerciseId: Long
)
