// WeeklyPlanDao.kt
package com.example.mygymapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// WeeklyPlanDao.kt
@Dao
interface WeeklyPlanDao {
    @Transaction
    @Query("SELECT * FROM weekly_plans ORDER BY dayOfWeek")
    fun getAllWeeklyPlans(): Flow<List<WeeklyPlanWithExercises>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeeklyPlan(plan: WeeklyPlan): Long

    @Query("DELETE FROM weekly_plans WHERE planId = :planId")
    fun deleteWeeklyPlanById(planId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addExerciseToPlan(crossRef: WeeklyPlanExerciseCrossRef)

    @Query("DELETE FROM weekly_plan_exercises WHERE planId = :planId AND exerciseId = :exerciseId")
    fun removeExerciseFromPlan(planId: String, exerciseId: Long): Int
}


