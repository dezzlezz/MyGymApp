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

    /** Einzelne Verknüpfung mit reps/sets anlegen */
    suspend fun addExerciseToPlan(planId: String, exerciseId: Long, reps: Int, sets: Int) {
        crossRefDao.insertCrossRef(
            DailyPlanExerciseCrossRef(planId, exerciseId, reps, sets)
        )
    }

    /** Einzelne Verknüpfung wieder löschen */
    suspend fun removeExerciseFromPlan(planId: String, exerciseId: Long): Int =
        crossRefDao.deleteCrossRef(planId, exerciseId)

    /** Alle Pläne mit ihren Exercises laden */
    fun getAllPlansWithExercises(): Flow<List<DailyPlanWithExercises>> =
        dailyPlanDao.getAllPlansWithExercises()

    /** Plan plus mehrere Verknüpfungen in einem Schritt anlegen */
    suspend fun insertDailyPlanWithDetails(
        planId: String,
        name: String,
        description: String,
        exerciseIds: List<Long>,
        reps: List<Int>,
        sets: List<Int>
    ): Long {
        val plan = DailyPlan(planId, name, description)
        val newId = dailyPlanDao.insertDailyPlan(plan)
        // Batch-insert der CrossRefs
        val refs = exerciseIds.mapIndexed { idx, eid ->
            DailyPlanExerciseCrossRef(planId, eid, reps[idx], sets[idx])
        }
        crossRefDao.insertCrossRefs(refs)
        return newId
    }
}
