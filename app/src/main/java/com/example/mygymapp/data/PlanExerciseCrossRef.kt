package com.example.mygymapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Optional grouping for supersets and circuits. */
import com.example.mygymapp.data.GroupType

@Entity
data class PlanExerciseCrossRef(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val planId: Long,
    val exerciseId: Long,
    val sets: Int,
    val reps: Int,
    val orderIndex: Int,
    val dayIndex: Int = 0,
    val groupId: Long? = null,
    val groupType: GroupType? = null
)
