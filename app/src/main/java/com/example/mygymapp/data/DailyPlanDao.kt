// DailyPlanDao.kt
package com.example.mygymapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// DailyPlanDao.kt
@Dao
interface DailyPlanDao {
    @Transaction
    @Query("SELECT * FROM daily_plans")
    fun getAllDailyPlans(): Flow<List<DailyPlanWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDailyPlan(plan: DailyPlan): Long

    @Query("DELETE FROM daily_plans WHERE planId = :planId")
    fun deleteDailyPlanById(planId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addExerciseToPlan(crossRef: DailyPlanExerciseCrossRef)

    @Query("DELETE FROM daily_plan_exercises WHERE planId = :planId AND exerciseId = :exerciseId")
    fun removeExerciseFromPlan(planId: String, exerciseId: Long): Int
}

