package com.example.mygymapp.model

/** Temporary user preferences used for plan suggestions */
data class UserPreferences(
    val daysPerWeek: Int = 3,
    val maxDuration: Int = 30,
    val equipment: Set<String> = emptySet(),
    val goal: GoalType = GoalType.FIT,
    val focusGroups: Set<MuscleGroup> = emptySet()
)

enum class GoalType {
    HYPERTROPHY,
    STRENGTH,
    ENDURANCE,
    FIT
}
