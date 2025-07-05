// DailyPlanExerciseCrossRef.kt
package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface DailyPlanExerciseCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crossRef: DailyPlanExerciseCrossRef): Long

    @Query("DELETE FROM daily_plan_exercises WHERE planId = :planId AND exerciseId = :exerciseId")
    fun delete(planId: String, exerciseId: Long): Int
}
@Entity(
    tableName = "daily_plan_exercises",
    primaryKeys = ["planId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = DailyPlan::class,
            parentColumns = ["planId"],    // muss mit dem PK-Feld in DailyPlan übereinstimmen
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],        // PK in Exercise
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ Index(value = ["exerciseId"]) ]
)
data class DailyPlanExerciseCrossRef(
    val planId: String,
    val exerciseId: Long
)
