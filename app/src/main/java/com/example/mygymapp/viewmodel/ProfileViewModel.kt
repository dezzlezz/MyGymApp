package com.example.mygymapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.ExerciseRepository
import com.example.mygymapp.data.SettingsStorage
import com.example.mygymapp.data.WorkoutHistoryEntry
import com.example.mygymapp.data.WorkoutHistoryStorage
import com.example.mygymapp.data.WorkoutStorage
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.model.MuscleGroupStat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.ZoneId

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val settings = SettingsStorage.getInstance(application)
    private val historyStore = WorkoutHistoryStorage.getInstance(application)
    private val repo = PlanRepository(AppDatabase.getDatabase(application).planDao())
    private val exerciseRepo = ExerciseRepository(AppDatabase.getDatabase(application).exerciseDao())

    val userName: StateFlow<String> = settings.userName
    val notifications: StateFlow<Boolean> = settings.notifications

    private val _history = MutableStateFlow(historyStore.loadAll())
    val history: StateFlow<Map<LocalDate, WorkoutHistoryEntry>> = _history

    fun setUserName(name: String) = settings.setUserName(name)
    fun setNotifications(enabled: Boolean) = settings.setNotifications(enabled)

    fun refreshHistory() {
        _history.value = historyStore.loadAll()
    }

    fun getEntryInfo(entry: WorkoutHistoryEntry, onResult: (String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val plan = repo.getPlanWithExercisesOrNull(entry.planId)
            if (plan != null) {
                val planName = plan.plan.name
                val dayName = plan.days.firstOrNull { it.dayIndex == entry.dayIndex }?.name
                    ?: "Tag ${entry.dayIndex + 1}"
                launch(Dispatchers.Main) { onResult(planName, dayName) }
            }
        }
    }

    suspend fun getMuscleGroupSummary(): Map<MuscleGroup, Int> = withContext(Dispatchers.IO) {
        val today = LocalDate.now(ZoneId.systemDefault())
        val start = today.minusDays(6)
        val entries = historyStore.loadAll().filterKeys { !it.isBefore(start) }

        val summary = MuscleGroup.values().associateWith { 0 }.toMutableMap()
        for (entry in entries.values) {
            val plan = repo.getPlanWithExercisesOrNull(entry.planId) ?: continue
            val refs = plan.exercises.filter { it.dayIndex == entry.dayIndex }
            refs.forEach { ref ->
                val ex = exerciseRepo.getExerciseById(ref.exerciseId) ?: return@forEach
                val group = ex.muscleGroup
                summary[group] = summary.getOrDefault(group, 0) + 1
            }
        }
        summary
    }

    suspend fun getMuscleGroupStats(): List<MuscleGroupStat> =
        getMuscleGroupSummary()
            .map { (g, c) -> MuscleGroupStat(g, c) }
            .sortedByDescending { it.count }

    val totalWorkouts: Int
        get() = history.value.size

    val workoutsThisWeek: Int
        get() {
            val today = LocalDate.now(ZoneId.systemDefault())
            val startOfWeek = today.with(DayOfWeek.MONDAY)
            return history.value.keys.count { !it.isBefore(startOfWeek) }
        }

    val workoutStreak: Int
        get() {
            val today = LocalDate.now(ZoneId.systemDefault())
            var day = today
            var count = 0
            while (history.value.containsKey(day)) {
                count++
                day = day.minusDays(1)
            }
            return count
        }

    fun logout() {
        // For demo purposes we just clear progress
        WorkoutStorage(getApplication()).clear()
    }}
