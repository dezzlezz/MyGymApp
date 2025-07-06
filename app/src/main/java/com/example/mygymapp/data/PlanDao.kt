package com.example.mygymapp.data

import androidx.room.*

@Dao
interface PlanDao {

    @Query("SELECT * FROM plans WHERE type = :type ORDER BY isFavorite DESC, name")
    fun loadPlans(type: String): List<Plan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertPlan(plan: Plan): Long

    @Delete
    fun deletePlan(plan: Plan)

    @Transaction
    @Query("SELECT * FROM plans WHERE planId = :planId")
    fun loadDailyPlan(planId: Long): DailyPlanWithExercises

    @Transaction
    @Query("SELECT * FROM plans WHERE planId = :planId")
    fun loadWeeklyPlan(planId: Long): WeeklyPlanWithDays

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertDailyCrossRefs(refs: List<DailyPlanExerciseCrossRef>)

    @Query("DELETE FROM daily_plan_exercises WHERE planId = :planId")
    fun clearDailyExercises(planId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertWeekDays(days: List<WeeklyPlanDayEntity>)

    @Query("DELETE FROM weekly_plan_days WHERE planId = :planId")
    fun clearWeekDays(planId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertWeeklyCrossRefs(refs: List<WeeklyPlanExerciseCrossRef>)

    @Query("""
        DELETE FROM weekly_plan_exercises
        WHERE dayId IN (
          SELECT id FROM weekly_plan_days WHERE planId = :planId
        )
    """)
    fun clearWeeklyExercises(planId: Long)
}
