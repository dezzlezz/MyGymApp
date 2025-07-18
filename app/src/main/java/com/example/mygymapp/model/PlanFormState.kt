package com.example.mygymapp.model

data class PlanFormState(
    val name: String = "",
    val description: String = "",
    val difficulty: Int = 3,
    val duration: Int = 30,
    val equipment: List<String> = emptyList()
)
