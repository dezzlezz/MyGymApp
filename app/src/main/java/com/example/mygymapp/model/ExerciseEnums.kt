package com.example.mygymapp.model

enum class ExerciseCategory(val display: String) {
    Cardio("Cardio"),
    Gym("Gym"),
    Calisthenics("Calisthenics"),
    Warmup("Warmup"),
    Flexibility("Flexibility");
}

enum class MuscleGroup(val display: String) {
    Legs("Legs"),
    Shoulders("Shoulders"),
    Chest("Chest"),
    Arms("Arms"),
    Core("Core"),
    Back("Back");
}
