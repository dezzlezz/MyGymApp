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
    @Query("SELECT * FROM paragraphs WHERE isArchived = 0")
    fun getActive(): Flow<List<ParagraphEntity>>

    @Query("SELECT * FROM paragraphs WHERE isArchived = 1")
    fun getArchived(): Flow<List<ParagraphEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(paragraph: ParagraphEntity): Long

    @Update
    fun update(paragraph: ParagraphEntity)

    @Query("UPDATE paragraphs SET isArchived = 1 WHERE id = :id")
    fun archive(id: Long)

    @Query("UPDATE paragraphs SET isArchived = 0 WHERE id = :id")
    fun unarchive(id: Long)

    @Delete
    fun delete(paragraph: ParagraphEntity)
}
