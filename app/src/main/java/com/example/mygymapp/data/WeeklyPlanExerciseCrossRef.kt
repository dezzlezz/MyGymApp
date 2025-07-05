// WeeklyPlanExerciseCrossRef.kt
package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeeklyPlanExerciseCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crossRef: WeeklyPlanExerciseCrossRef): Long

    @Query("DELETE FROM weekly_plan_exercises WHERE planId = :planId AND exerciseId = :exerciseId")
    fun delete(planId: String, exerciseId: Long): Int
}
@Entity(
    tableName = "weekly_plan_exercises",
    primaryKeys = ["planId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = WeeklyPlan::class,
            parentColumns = ["planId"],    // muss mit dem PK-Feld in WeeklyPlan übereinstimmen
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [ Index(value = ["exerciseId"]) ]
)
data class WeeklyPlanExerciseCrossRef(
    val planId: String,
    val exerciseId: Long
)
