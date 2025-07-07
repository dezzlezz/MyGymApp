package com.example.mygymapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM Plan WHERE type = :type ORDER BY name")
    fun getPlansByType(type: PlanType): Flow<List<Plan>>

    @Transaction
    @Query("SELECT * FROM Plan WHERE planId = :id")
    fun getPlanWithExercises(id: Long): PlanWithExercises?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlan(plan: Plan): Long

    @Delete
    fun deletePlan(plan: Plan)

    @Query("DELETE FROM PlanExerciseCrossRef WHERE planId = :planId")
    fun deleteCrossRefsForPlan(planId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrossRefs(refs: List<PlanExerciseCrossRef>)

    @Query("DELETE FROM plan_day WHERE planId = :planId")
    fun deleteDaysForPlan(planId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDays(days: List<PlanDay>)
}
