package com.example.mygymapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ParagraphDao {
    @Query("SELECT * FROM paragraphs")
    fun getAll(): Flow<List<ParagraphEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(paragraph: ParagraphEntity): Long

    @Update
    fun update(paragraph: ParagraphEntity)

    @Delete
    fun delete(paragraph: ParagraphEntity)
}
