package com.example.mygymapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DailyPlanRepository(
    private val planDao: DailyPlanDao,
    private val crossRefDao: DailyPlanExerciseCrossRefDao
) {
    fun getAllPlansWithExercises(): Flow<List<DailyPlanWithExercises>> =
        planDao.getDailyPlansWithExercises()

    suspend fun insertDailyPlan(plan: DailyPlan): Long = withContext(Dispatchers.IO) {
        planDao.insert(plan)
    }

    suspend fun addExerciseToPlan(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        crossRefDao.insert(DailyPlanExerciseCrossRef(planId, exerciseId))
    }

    suspend fun removeExerciseFromPlan(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        crossRefDao.delete(planId, exerciseId)
    }

    suspend fun deleteDailyPlanById(planId: String) = withContext(Dispatchers.IO) {
        planDao.deleteDailyPlanById(planId)
    }
}
