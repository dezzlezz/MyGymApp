package com.example.mygymapp.data

import android.net.Uri
import androidx.room.*

@Entity
@TypeConverters(PlanConverters::class)
data class Plan(
    @PrimaryKey(autoGenerate = true) val planId: Long = 0L,
    val name: String,
    val description: String,
    val isFavorite: Boolean = false,
    val iconUri: String?,
    val type: PlanType
)

enum class PlanType { DAILY, WEEKLY }

@Entity(primaryKeys = ["planId", "exerciseId"])
data class PlanExerciseCrossRef(
    val planId: Long,
    val exerciseId: Long,
    val sets: Int,
    val reps: Int,
    val orderIndex: Int,
    val dayIndex: Int = 0
)