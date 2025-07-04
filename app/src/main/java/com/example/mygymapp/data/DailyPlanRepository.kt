package com.example.mygymapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DailyPlanRepository(private val dao: DailyPlanDao) {

    fun getAll(): Flow<List<DailyPlanWithExercises>> =
        dao.getAllDailyPlans()

    suspend fun insert(plan: DailyPlan) = withContext(Dispatchers.IO) {
        dao.insertDailyPlan(plan)
    }

    suspend fun deleteById(planId: String) = withContext(Dispatchers.IO) {
        dao.deleteDailyPlanById(planId)
    }

    suspend fun addExercise(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        dao.addExerciseToPlan(DailyPlanExerciseCrossRef(planId, exerciseId))
    }

    suspend fun removeExercise(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        dao.removeExerciseFromPlan(planId, exerciseId)
    }
}
