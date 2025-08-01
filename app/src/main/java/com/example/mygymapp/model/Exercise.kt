package com.example.mygymapp.model

data class Exercise(
    val id: Long,
    val name: String,
    val sets: Int,
    val repsOrDuration: String,
    val prGoal: Int? = null,
    val note: String = ""
)
