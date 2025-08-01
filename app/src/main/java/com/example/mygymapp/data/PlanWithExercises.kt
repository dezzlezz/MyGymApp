package com.example.mygymapp.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Stellt einen Plan mitsamt all seiner CrossRefs (Übungen + dayIndex usw.) und
 * optionalen PlanDay-Einträgen dar. Wird von PlanDao.getPlanWithExercises()
 * zurückgegeben.
 */
data class PlanWithExercises(
    @Embedded val plan: Plan,
    @Relation(
        parentColumn = "planId",
        entityColumn = "planId",
        entity = PlanExerciseCrossRef::class
    )
    val exercises: List<PlanExerciseCrossRef>,
    @Relation(
        parentColumn = "planId",
        entityColumn = "planId",
        entity = PlanDay::class
    )
    val days: List<PlanDay> = emptyList()
)
