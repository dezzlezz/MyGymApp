package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mygymapp.data.StringListConverter

@Entity(tableName = "paragraphs")
@TypeConverters(StringListConverter::class)
data class ParagraphEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val lineTitles: List<String>,
    val note: String,
    val isArchived: Boolean = false
)
