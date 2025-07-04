package com.example.mygymapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class WeeklyPlanWithExercises(
    @Embedded val plan: WeeklyPlan,
    @Relation(
        parentColumn = "planId",
        entityColumn = "id",  // id der Exercise-Entity
        associateBy = Junction(WeeklyPlanExerciseCrossRef::class)
    )
    val exercises: List<Exercise>
)
