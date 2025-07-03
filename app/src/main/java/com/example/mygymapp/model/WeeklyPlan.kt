package com.example.mygymapp.model

data class WeeklyPlan(
    val id: String,
    val name: String,
    val days: MutableMap<String, MutableList<Pair<String, Int>>> = mutableMapOf(
        "Monday" to mutableListOf(),
        "Tuesday" to mutableListOf(),
        "Wednesday" to mutableListOf(),
        "Thursday" to mutableListOf(),
        "Friday" to mutableListOf(),
        "Saturday" to mutableListOf(),
        "Sunday" to mutableListOf()
    )
)
