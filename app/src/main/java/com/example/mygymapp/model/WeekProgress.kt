package com.example.mygymapp.model

/**
 * Stores the current week's progress and settings.
 * @param weeklyPlanId the selected weekly plan
 * @param restDay index of the rest day (0=Mon..6=Sun) or -1 if none
 * @param modularPlanId optional daily plan for the modular day (Saturday)
 * @param modularRest true if Saturday is a rest day
 * @param day current day index within the week (0..6)
 */
data class WeekProgress(
    val weeklyPlanId: Long,
    val restDay: Int,
    val modularPlanId: Long?,
    val modularRest: Boolean,
    val day: Int = 0
)