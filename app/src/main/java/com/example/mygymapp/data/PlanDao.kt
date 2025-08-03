package com.example.mygymapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM plan WHERE type = :arg0 ORDER BY name")
    fun getPlansByType(planType: PlanType): Flow<List<Plan>>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM plan")
    fun getAllPlans(): List<Plan>

    @Transaction
    @Query("SELECT * FROM plan WHERE planId = :arg0")
    fun getPlanWithExercises(planId: Long): PlanWithExercises?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlan(plan: Plan): Long

    @Delete
    fun deletePlan(plan: Plan)

    @Query("DELETE FROM PlanExerciseCrossRef WHERE planId = :arg0")
    fun deleteCrossRefsForPlan(planId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrossRefs(refs: List<PlanExerciseCrossRef>)

    @Query("DELETE FROM plan_day WHERE planId = :arg0")
    fun deleteDaysForPlan(planId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDays(days: List<PlanDay>)
}
