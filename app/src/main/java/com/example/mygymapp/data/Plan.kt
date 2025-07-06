package com.example.mygymapp.data

import android.net.Uri
import androidx.room.*
import com.example.mygymapp.data.Exercise

/**
 * Konverter für Uri und PlanType
 */
class PlanConverters {
    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun stringToUri(value: String?): Uri? = value?.let { Uri.parse(it) }

    @TypeConverter
    fun planTypeToString(type: PlanType): String = type.name

    @TypeConverter
    fun stringToPlanType(value: String): PlanType = PlanType.valueOf(value)
}

/**
 * Typ für Plan (täglich oder wöchentlich)
 */
enum class PlanType { DAILY, WEEKLY }

/**
 * Haupt-Entity für einen Trainingsplan
 */
@Entity
@TypeConverters(PlanConverters::class)
data class Plan(
    @PrimaryKey(autoGenerate = true) val planId: Long = 0L,
    val name: String,
    val description: String,
    val isFavorite: Boolean = false,
    val iconUri: String?,
    val type: PlanType
)

/**
 * Kreuztabelle für Zuordnung Plan ↔ Exercise mit Meta-Daten
 */
@Entity(primaryKeys = ["planId", "exerciseId"])
data class PlanExerciseCrossRef(
    val planId: Long,
    val exerciseId: Long,
    val sets: Int,
    val reps: Int,
    val orderIndex: Int,
    val dayIndex: Int = 0
)

/**
 * POJO für die Relation zwischen Plan und Exercise
 */
@SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
@JvmSuppressWildcards
data class PlanWithExercises(
    @Embedded val plan: Plan,
    @Relation(
        parentColumn = "planId",
        entityColumn = "id",
        associateBy = Junction(
            value = PlanExerciseCrossRef::class,
            parentColumn = "planId",
            entityColumn = "exerciseId"
        )
    ) val exercises: List<Exercise>
)