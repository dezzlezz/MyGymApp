package com.example.mygymapp.data

import kotlinx.coroutines.flow.Flow

class PlanRepository(private val dao: PlanDao) {
    fun getWeeklyPlans(): Flow<List<PlanWithExercises>> = dao.getPlansByType(PlanType.WEEKLY)
    fun getDailyPlans(): Flow<List<PlanWithExercises>> = dao.getPlansByType(PlanType.DAILY)

    suspend fun getPlan(id: Long) = dao.getPlanWithExercises(id)

    suspend fun createOrUpdatePlan(plan: Plan, refs: List<PlanExerciseCrossRef>) {
        val id = dao.insertPlan(plan)
        dao.deleteCrossRefsForPlan(id)
        val updatedRefs = refs.map { it.copy(planId = id) }
        dao.insertCrossRefs(updatedRefs)
    }

    suspend fun deletePlan(plan: Plan) {
        dao.deleteCrossRefsForPlan(plan.planId)
        dao.deletePlan(plan)
    }
}