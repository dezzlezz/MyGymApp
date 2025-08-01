package com.example.mygymapp.data

import androidx.room.TypeConverter
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup


class ExerciseConverters {
    @TypeConverter
    fun fromExerciseCategory(value: ExerciseCategory): String = value.name

    @TypeConverter
    fun toExerciseCategory(value: String): ExerciseCategory = ExerciseCategory.valueOf(value)

    @TypeConverter
    fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup = MuscleGroup.valueOf(value)
}
