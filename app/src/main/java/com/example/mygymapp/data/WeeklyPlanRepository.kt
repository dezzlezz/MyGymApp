package com.example.mygymapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeeklyPlanRepository(private val dao: WeeklyPlanDao) {

    fun getAll(): Flow<List<WeeklyPlanWithExercises>> =
        dao.getAllWeeklyPlans()

    suspend fun insert(plan: WeeklyPlan) = withContext(Dispatchers.IO) {
        dao.insertWeeklyPlan(plan)
    }

    suspend fun deleteById(planId: String) = withContext(Dispatchers.IO) {
        dao.deleteWeeklyPlanById(planId)
    }

    suspend fun addExercise(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        dao.addExerciseToPlan(PlanExerciseCrossRef(planId, exerciseId))
    }

    suspend fun removeExercise(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        dao.removeExerciseFromPlan(planId, exerciseId)
    }
}
