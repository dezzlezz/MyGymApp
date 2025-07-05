package com.example.mygymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(plan: DailyPlan): Long

    @Query("DELETE FROM daily_plans WHERE planId = :planId")
    fun deleteDailyPlanById(planId: String): Int

    @Transaction
    @Query("SELECT * FROM daily_plans")
    fun getDailyPlansWithExercises(): Flow<List<DailyPlanWithExercises>>
}
