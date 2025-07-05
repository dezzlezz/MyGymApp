package com.example.mygymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeeklyPlanExerciseCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crossRef: WeeklyPlanExerciseCrossRef): Long

    // Parameternamen müssen exakt `planId` und `exerciseId` sein:
    @Query("""
        DELETE FROM weekly_plan_exercises
        WHERE planId = :planId AND exerciseId = :exerciseId
    """)
    fun delete(planId: String, exerciseId: Long): Int
}
