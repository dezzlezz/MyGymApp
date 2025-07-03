package com.example.mygymapp.model

data class DailyPlan(
    val id: String,
    val name: String,
    val exercises: MutableList<Pair<String, Int>> = mutableListOf()
)
