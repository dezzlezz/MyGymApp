package com.example.mygymapp.model

/** Mapping of muscle groups to specific muscles used by exercise forms */
val musclesByGroup = mapOf(
    MuscleGroup.Arms to listOf("Biceps", "Triceps", "Forearm"),
    MuscleGroup.Legs to listOf("Quadriceps", "Hamstrings", "Calves"),
    MuscleGroup.Core to listOf("Abs", "Obliques"),
    MuscleGroup.Chest to listOf("Upper Chest", "Lower Chest"),
    MuscleGroup.Shoulders to listOf("Front", "Lateral", "Rear"),
    MuscleGroup.Back to listOf("Upper Back", "Lower Back", "Lats")
)
