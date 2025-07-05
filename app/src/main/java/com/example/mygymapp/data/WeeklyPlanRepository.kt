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

    /**
     * Fügt einen WeeklyPlan ein oder ersetzt ihn, liefert die neue Row-ID
     */
    suspend fun insertWeeklyPlan(plan: WeeklyPlan): Long = withContext(Dispatchers.IO) {
        planDao.insertWeeklyPlan(plan)
    }

    /**
     * Verknüpft eine Exercise mit dem Plan, liefert neue Row-ID
     */
    suspend fun addExerciseToPlan(planId: String, exerciseId: Long): Long = withContext(Dispatchers.IO) {
        crossRefDao.insert(WeeklyPlanExerciseCrossRef(planId, exerciseId))
    }

    /**
     * Entfernt die Verknüpfung, liefert Anzahl gelöschter Zeilen
     */
    suspend fun removeExerciseFromPlan(planId: String, exerciseId: Long): Int = withContext(Dispatchers.IO) {
        crossRefDao.delete(planId, exerciseId)
    }

    /**
     * Löscht einen WeeklyPlan per ID, liefert Anzahl gelöschter Zeilen
     */
    suspend fun deleteWeeklyPlanById(planId: String): Int = withContext(Dispatchers.IO) {
        planDao.deleteWeeklyPlanById(planId)
    }
}
