package com.example.mygymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeeklyPlan(plan: WeeklyPlan): Long

    // Parametername muss exakt `planId` sein:
    @Query("DELETE FROM weekly_plans WHERE planId = :planId")
    fun deleteWeeklyPlanById(planId: String): Int

    @Transaction
    @Query("SELECT * FROM weekly_plans")
    fun getWeeklyPlansWithExercises(): Flow<List<WeeklyPlanWithExercises>>
}
