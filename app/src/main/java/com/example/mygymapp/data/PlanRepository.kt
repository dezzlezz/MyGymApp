package com.example.mygymapp.data

import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.data.PlanType as DbPlanType
import com.example.mygymapp.model.PlanType as UiPlanType
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.mygymapp.data.PlanDay

class PlanRepository(
    private val dao: PlanDao
) {

    /** Liefert alle Pläne eines Typs (DAILY oder WEEKLY) */
    fun getPlans(type: UiPlanType): Flow<List<Plan>> =
        dao.getPlansByType(
            DbPlanType.valueOf(type.name)
        )

    /** Lädt einen Plan inkl. aller Cross-Refs oder wirft, wenn nicht gefunden */
    suspend fun getPlanWithExercises(planId: Long): PlanWithExercises =
        withContext(Dispatchers.IO) {
            dao.getPlanWithExercises(planId)
                ?: throw NoSuchElementException("Kein Plan mit der ID $planId gefunden")
        }

    /**
     * Speichert oder aktualisiert einen Plan + CrossRefs.
     * @return die (neue) planId
     */
    suspend fun savePlan(
        plan: Plan,
        exercises: List<PlanExerciseCrossRef>,
        dayNames: List<String> = emptyList()
    ): Long = withContext(Dispatchers.IO) {
        val newPlanId = dao.insertPlan(plan)
        dao.deleteCrossRefsForPlan(newPlanId)
        dao.insertCrossRefs(exercises.map { it.copy(planId = newPlanId) })
        dao.deleteDaysForPlan(newPlanId)
        if (dayNames.isNotEmpty()) {
            dao.insertDays(dayNames.mapIndexed { idx, name ->
                PlanDay(planId = newPlanId, dayIndex = idx, name = name)
            })
        }
        newPlanId
    }

    /** Löscht einen Plan komplett */
    suspend fun deletePlan(plan: Plan) = withContext(Dispatchers.IO) {
        dao.deletePlan(plan)
    }

    suspend fun getAllPlans(): List<Plan> = withContext(Dispatchers.IO) {
        dao.getAllPlans()
    }

    suspend fun getSuggestions(prefs: com.example.mygymapp.model.UserPreferences): List<Plan> =
        getAllPlans().filter { plan ->
            plan.durationMinutes <= prefs.maxDuration &&
                plan.requiredEquipment.all { it in prefs.equipment }
        }

    /**
     * Generate a new weekly plan from the given preferences and exercises.
     * The created plan is persisted and returned with all relations.
     */
    suspend fun generatePlanFromPreferences(
        preferences: UserPreferences,
        allExercises: List<Exercise>
    ): PlanWithExercises {
        val filteredExercises = allExercises.filter { ex ->
            ex.muscleGroup in preferences.focusGroups &&
                ex.category != ExerciseCategory.Cardio &&
                (
                    preferences.equipment.contains("Keine") ||
                        preferences.equipment.any { eq ->
                            ex.description.contains(eq, ignoreCase = true)
                        }
                    )
        }

        val days = List(preferences.daysPerWeek) { dayIndex ->
            val dayExercises = filteredExercises.shuffled().take(4)
            dayExercises.mapIndexed { i, ex ->
                PlanExerciseCrossRef(
                    planId = 0L,
                    exerciseId = ex.id,
                    sets = 3,
                    reps = 10,
                    orderIndex = i,
                    dayIndex = dayIndex
                )
            }
        }

        val plan = Plan(
            name = "Auto-Plan ${System.currentTimeMillis()}",
            description = "Generiert f\u00fcr ${preferences.goal}",
            difficulty = 3,
            iconUri = null,
            type = DbPlanType.WEEKLY
        )

        val allRefs = days.flatten()
        val dayNames = days.indices.map { "Tag ${it + 1}" }

        val planId = savePlan(plan, allRefs, dayNames)
        return getPlanWithExercises(planId)
    }
}
