// WeeklyPlanWithExercises.kt
package com.example.mygymapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WeeklyPlanWithExercises(
    @Embedded
    val plan: WeeklyPlan,

    @Relation(
        parentColumn = "planId",         // PK-Feld in WeeklyPlan
        entityColumn = "id",             // PK-Feld in Exercise
        associateBy = Junction(
            value = WeeklyPlanExerciseCrossRef::class,
            parentColumn = "planId",     // FK-Spalte in CrossRef
            entityColumn = "exerciseId"  // FK-Spalte in CrossRef
        )
    )
    val exercises: List<Exercise>
)
