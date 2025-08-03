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


    // Delete a single exercise by its primary key
    @Query("DELETE FROM exercises WHERE id = :id")
    fun deleteById(id: Long): Int

    // Retrieve a single exercise by its primary key
    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getById(id: Long): Exercise?
}
