package com.example.mygymapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>

    // kein suspend mehr, klare Rückgabe Long
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(exercise: Exercise): Long

    // Rückgabe Int = Anzahl gelöschter Zeilen
    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    fun deleteById(exerciseId: Long): Int
}
