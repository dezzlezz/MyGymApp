// DailyPlanWithExercises.kt
package com.example.mygymapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DailyPlanWithExercises(
    @Embedded
    val plan: DailyPlan,

    @Relation(
        parentColumn = "planId",         // PK-Feld in DailyPlan
        entityColumn = "id",             // PK-Feld in Exercise
        associateBy = Junction(
            value = DailyPlanExerciseCrossRef::class,
            parentColumn = "planId",     // FK-Spalte in CrossRef
            entityColumn = "exerciseId"  // FK-Spalte in CrossRef
        )
    )
    val exercises: List<Exercise>
)
