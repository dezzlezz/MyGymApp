package com.example.mygymapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.mygymapp.data.Exercise

/** Liefert einen Plan mitsamt seiner Übungen zurück */
data class PlanWithExercises(
    @Embedded val plan: Plan,
    @Relation(
        parentColumn = "planId",
        entityColumn = "id",               // das PK-Feld deiner Exercise-Entity
        associateBy = Junction(
            value = PlanExerciseCrossRef::class,
            parentColumn = "planId",
            entityColumn = "exerciseId"      // das FK-Feld in PlanExerciseCrossRef
        )
    )
    val exercises: List<Exercise>
)
