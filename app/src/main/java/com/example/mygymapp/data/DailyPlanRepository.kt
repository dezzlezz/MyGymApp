package com.example.mygymapp.data

import kotlinx.coroutines.flow.Flow

class DailyPlanRepository(
    private val dailyPlanDao: DailyPlanDao,
    private val crossRefDao: DailyPlanExerciseCrossRefDao
) {

    suspend fun insertDailyPlan(plan: DailyPlan): Long =
        dailyPlanDao.insertDailyPlan(plan)

    suspend fun deleteDailyPlanById(planId: String): Int =
        dailyPlanDao.deleteDailyPlanById(planId)

    suspend fun addExerciseToPlan(planId: String, exerciseId: Long) {
        // nutzt nun dao.insert(...) anstatt insertCrossRefs
        crossRefDao.insert(DailyPlanExerciseCrossRef(planId, exerciseId))
    }

    suspend fun removeExerciseFromPlan(planId: String, exerciseId: Long): Int =
        crossRefDao.delete(planId, exerciseId)

    fun getAllPlansWithExercises(): Flow<List<DailyPlanWithExercises>> =
        dailyPlanDao.getAllDailyPlansWithExercises()

    suspend fun insertDailyPlanWithDetails(
        planId: String,
        name: String,
        description: String,
        exerciseIds: List<Long>,
        reps: List<Int>,      // nur falls du reps/sets weiter verwenden willst
        sets: List<Int>
    ): Long {
        val plan = DailyPlan(planId, name, description)
        val newId = dailyPlanDao.insertDailyPlan(plan)
        exerciseIds.forEachIndexed { idx, exId ->
            crossRefDao.insert(
                DailyPlanExerciseCrossRef(planId, exId, reps[idx], sets[idx])
            )
        }
        return newId
    }
}
