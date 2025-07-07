package com.example.mygymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exercise: Exercise): Long

    @Update
    fun update(exercise: Exercise): Int


    // Lösche per ID, wie gehabt:
    @Query("DELETE FROM exercises WHERE id = :id")
    fun deleteById(id: Long): Int

    // Hole einzelnen Exercise per ID:
    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getById(id: Long): Exercise?
}
