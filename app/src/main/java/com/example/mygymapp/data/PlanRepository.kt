// app/src/main/java/com/example/mygymapp/data/PlanRepository.kt
package com.example.mygymapp.data

import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.DailyPlanWithExercises
import com.example.mygymapp.data.WeeklyPlanWithDays
import com.example.mygymapp.model.PlanType
import kotlinx.coroutines.flow.Flow

class PlanRepository(private val dao: PlanDao) {

    /** Liefert alle Pläne des Typs (DAILY oder WEEKLY) als Flow */
    fun getPlans(type: PlanType): Flow<List<Plan>> =
        dao.loadPlans(type)

    /** Lädt einen Tages-Plan inklusive aller Exercises (dayIndex = 0) */
    suspend fun getDailyPlan(planId: Long): DailyPlanWithExercises =
        dao.loadDailyPlan(planId)

    /** Lädt einen Wochen-Plan inklusive aller Exercises (dayIndex 0…6) */
    suspend fun getWeeklyPlan(planId: Long): WeeklyPlanWithDays =
        dao.loadWeeklyPlan(planId)

    /** Speichert oder updated einen Tages-Plan */
    suspend fun saveDailyPlan(
        plan: Plan,
        exercises: List<PlanExerciseCrossRef>
    ) {
        // Plan speichern
        dao.upsertPlan(plan)
        // alle alten CrossRefs löschen
        dao.clearCrossRefs(plan.planId)
        // neue CrossRefs mit dayIndex = 0 anlegen
        val dailyRefs = exercises.map {
            it.copy(planId = plan.planId, dayIndex = 0)
        }
        dao.upsertCrossRefs(dailyRefs)
    }

    /** Speichert oder updated einen Wochen-Plan */
    suspend fun saveWeeklyPlan(
        plan: Plan,
        exercises: List<PlanExerciseCrossRef>
    ) {
        dao.upsertPlan(plan)
        dao.clearCrossRefs(plan.planId)
        // Übungen kommen mit dayIndex (0…6) vom Aufrufer
        val refs = exercises.map { it.copy(planId = plan.planId) }
        dao.upsertCrossRefs(refs)
    }

    /** Löscht einen Plan */
    suspend fun deletePlan(plan: Plan) {
        dao.deletePlan(plan)
    }
}
