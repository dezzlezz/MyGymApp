package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mygymapp.data.StringListConverter

@Entity(tableName = "plan")
@TypeConverters(PlanConverters::class, StringListConverter::class)
data class Plan(
    @PrimaryKey(autoGenerate = true)
    val planId: Long = 0L,
    val name: String,
    val description: String,
    val difficulty: Int = 3,
    val isFavorite: Boolean = false,
    val iconUri: String?,
    val type: PlanType,
    val durationMinutes: Int = 30,
    val requiredEquipment: List<String> = emptyList()
)

enum class PlanType {
    DAILY,
    WEEKLY
}
