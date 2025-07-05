// Path: app/src/main/java/com/example/mygymapp/data/WeeklyPlanRepository.kt
package com.example.mygymapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeeklyPlanRepository(
    private val planDao: WeeklyPlanDao,
    private val crossRefDao: WeeklyPlanExerciseCrossRefDao
) {
    fun getAllPlansWithExercises(): Flow<List<WeeklyPlanWithExercises>> =
        planDao.getWeeklyPlansWithExercises()

    suspend fun insertWeeklyPlan(plan: WeeklyPlan): Long = withContext(Dispatchers.IO) {
        planDao.insert(plan)
    }

    suspend fun addExerciseToPlan(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        crossRefDao.insert(WeeklyPlanExerciseCrossRef(planId, exerciseId))
    }

    suspend fun removeExerciseFromPlan(planId: String, exerciseId: Long) = withContext(Dispatchers.IO) {
        crossRefDao.delete(planId, exerciseId)
    }

    suspend fun deleteWeeklyPlanById(planId: String) = withContext(Dispatchers.IO) {
        planDao.deleteWeeklyPlanById(planId)
    }
}