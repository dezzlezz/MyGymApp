package com.example.mygymapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExerciseRepository
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.data.WorkoutHistoryEntry
import com.example.mygymapp.data.WorkoutHistoryStorage
import com.example.mygymapp.data.WorkoutStorage
import com.example.mygymapp.data.ExercisePRStore
import com.example.mygymapp.data.ExerciseLogStore
import com.example.mygymapp.model.WeekProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PlanRepository(AppDatabase.getDatabase(application).planDao())
    private val exerciseRepo = ExerciseRepository(AppDatabase.getDatabase(application).exerciseDao())
    private val storage = WorkoutStorage(application)
    private val historyStore = WorkoutHistoryStorage.getInstance(application)
    private val prStore = ExercisePRStore.getInstance(application)
    private val logStore = ExerciseLogStore.getInstance(application)

    private val _progress = MutableStateFlow<WeekProgress?>(null)
    val progress: StateFlow<WeekProgress?> = _progress.asStateFlow()

    private val _todayPlan = MutableStateFlow<PlanWithExercises?>(null)
    val todayPlan: StateFlow<PlanWithExercises?> = _todayPlan.asStateFlow()

    private val _history = MutableStateFlow(historyStore.loadAll())
    val history: StateFlow<Map<LocalDate, WorkoutHistoryEntry>> = _history.asStateFlow()

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepo.getAllExercises().collect { list ->
                _exercises.value = list
            }
        }
        loadProgress()
    }

    fun loadProgress() {
        val state = storage.load()
        _progress.value = state
        state?.let { updateTodayPlan(it) } ?: _todayPlan.tryEmit(null)
    }

    fun refreshHistory() {
        _history.value = historyStore.loadAll()
    }

    private fun updateTodayPlan(state: WeekProgress) {
        val planId = if (state.day == 5 && state.modularPlanId != null && !state.modularRest) {
            state.modularPlanId!!
        } else {
            state.weeklyPlanId
        }
        viewModelScope.launch(Dispatchers.IO) {
            val plan = repo.getPlanWithExercisesOrNull(planId)
            _todayPlan.emit(plan)
        }
    }

    val lastWorkout: WorkoutHistoryEntry?
        get() = history.value.maxByOrNull { it.key }?.value

    val workoutsThisWeek: Int
        get() {
            val today = LocalDate.now(ZoneId.systemDefault())
            val start = today.with(DayOfWeek.MONDAY)
            return history.value.keys.count { !it.isBefore(start) }
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

    fun getExerciseGroup(id: Long): String =
        _exercises.value.firstOrNull { it.id == id }?.muscleGroup?.display ?: ""

    suspend fun getEntryInfo(entry: WorkoutHistoryEntry): Pair<String, String>? = withContext(Dispatchers.IO) {
        val plan = repo.getPlanWithExercisesOrNull(entry.planId) ?: return@withContext null
        val planName = plan.plan.name
        val dayName = plan.days.firstOrNull { it.dayIndex == entry.dayIndex }?.name
            ?: "Day ${entry.dayIndex + 1}"
        planName to dayName
    }

    suspend fun getPrInfo(date: LocalDate): Pair<String, Int>? = withContext(Dispatchers.IO) {
        for (ex in _exercises.value) {
            val logs = logStore.load(ex.id)
            val last = logs.lastOrNull() ?: continue
            if (last.date == date) {
                val pr = prStore.getPR(ex.id)
                if (pr == last.reps && pr > 0) {
                    return@withContext ex.name to pr
                }
            }
        }
        null
    }
}
