package com.example.mygymapp.data

import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.data.PlanType as DbPlanType
import com.example.mygymapp.model.PlanType as UiPlanType
import kotlinx.coroutines.flow.Flow
import com.example.mygymapp.data.PlanDay

class PlanRepository(
    private val dao: PlanDao
) {

    /** Liefert alle Pläne eines Typs (DAILY oder WEEKLY) */
    fun getPlans(type: UiPlanType): Flow<List<Plan>> =
        dao.getPlansByType(
            DbPlanType.valueOf(type.name)
        )

    /** Lädt einen Plan inkl. aller Cross-Refs oder wirft, wenn nicht gefunden */
    suspend fun getPlanWithExercises(planId: Long): PlanWithExercises =
        dao.getPlanWithExercises(planId)
            ?: throw NoSuchElementException("Kein Plan mit der ID $planId gefunden")

    /**
     * Speichert oder aktualisiert einen Plan + CrossRefs.
     * @return die (neue) planId
     */
    suspend fun savePlan(
        plan: Plan,
        exercises: List<PlanExerciseCrossRef>,
        dayNames: List<String> = emptyList()
    ): Long {
        val newPlanId = dao.insertPlan(plan)
        dao.deleteCrossRefsForPlan(newPlanId)
        dao.insertCrossRefs(exercises.map { it.copy(planId = newPlanId) })
        dao.deleteDaysForPlan(newPlanId)
        if (dayNames.isNotEmpty()) {
            dao.insertDays(dayNames.mapIndexed { idx, name ->
                PlanDay(planId = newPlanId, dayIndex = idx, name = name)
            })
        }
        return newPlanId
    }

    /** Löscht einen Plan komplett */
    suspend fun deletePlan(plan: Plan) =
        dao.deletePlan(plan)
}
