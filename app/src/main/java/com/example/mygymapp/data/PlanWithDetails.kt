// app/src/main/java/com/example/mygymapp/data/PlanWithDetails.kt (new)
package com.example.mygymapp.data

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DailyPlanWithExercises(
    @Embedded val plan: PlanEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "planId",
        entity = DailyPlanExerciseCrossRef::class
    )
    val exercises: List<DailyPlanExerciseCrossRef>
)

data class WeeklyPlanDayWithExercises(
    @Embedded val day: WeeklyPlanDayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "dayId",
        entity = WeeklyPlanExerciseCrossRef::class
    )
    val exercises: List<WeeklyPlanExerciseCrossRef>
)

data class WeeklyPlanWithDays(
    @Embedded val plan: PlanEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "planId",
        entity = WeeklyPlanDayEntity::class
    )
    val days: List<WeeklyPlanDayWithExercises>
)
