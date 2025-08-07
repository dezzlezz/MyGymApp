package com.example.mygymapp.model

import java.io.Serializable

data class Exercise(
    val id: Long,
    val name: String,
    val sets: Int,
    val repsOrDuration: String,
    val prGoal: Int? = null,
    val note: String = "",
    val section: String = ""
) : Serializable
