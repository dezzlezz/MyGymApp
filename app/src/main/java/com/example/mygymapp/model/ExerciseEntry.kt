package com.example.mygymapp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.GroupType
import kotlin.random.Random

/**
 * Helper model representing an Exercise in a plan with editable sets and reps.
 */

class ExerciseEntry(
    val exercise: Exercise,
    sets: Int = 3,
    reps: Int = 10,
    val id: Long = Random.nextLong(),
    var groupId: Long? = null,
    var groupType: GroupType? = null
) {
    var sets by mutableIntStateOf(sets)
    var reps by mutableIntStateOf(reps)
}