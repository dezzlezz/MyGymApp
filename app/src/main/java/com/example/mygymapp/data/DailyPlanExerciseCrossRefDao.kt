package com.example.mygymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyPlanExerciseCrossRefDao {
    /**
     * Fügt eine einzelne Verknüpfung ein oder ersetzt sie.
     * @return Row-ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrossRef(ref: DailyPlanExerciseCrossRef): Long

    /**
     * Fügt mehrere Verknüpfungen ein oder ersetzt sie.
     * @return List der eingefügten Row-IDs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCrossRefs(refs: List<DailyPlanExerciseCrossRef>): List<Long>

    /**
     * Löscht eine Verknüpfung anhand von Plan- und Exercise-ID.
     * @return Anzahl gelöschter Zeilen
     */
    @Query("DELETE FROM daily_plan_exercises WHERE planId = :planId AND exerciseId = :exerciseId")
    fun deleteCrossRef(planId: String, exerciseId: Long): Int
}
