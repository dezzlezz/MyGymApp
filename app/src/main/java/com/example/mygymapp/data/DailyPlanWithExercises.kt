package com.example.mygymapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DailyPlanWithExercises(
    @Embedded val plan: DailyPlan,
    @Relation(
        parentColumn = "planId",
        entityColumn = "id",  // Achtung: id – nicht exerciseId!
        associateBy = Junction(DailyPlanExerciseCrossRef::class)
    )
    val exercises: List<Exercise>
)
