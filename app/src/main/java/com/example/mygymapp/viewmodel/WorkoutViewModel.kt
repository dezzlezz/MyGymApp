package com.example.mygymapp.viewmodel
ff
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.WorkoutStorage
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.data.Plan
import com.example.mygymapp.model.WeekProgress
import com.example.mygymapp.model.PlanType
import com.example.mygymapp.data.ExerciseRepository
import com.example.mygymapp.data.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.NoSuchElementException
import androidx.lifecycle.asLiveData
import com.example.mygymapp.data.WorkoutHistoryStorage
import com.example.mygymapp.data.WorkoutHistoryEntry
import java.time.LocalDate

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PlanRepository(AppDatabase.getDatabase(application).planDao())
    private val storage = WorkoutStorage(application)
    private val historyStore = WorkoutHistoryStorage.getInstance(application)
    private val exerciseRepo = ExerciseRepository(AppDatabase.getDatabase(application).exerciseDao())
    val exercises: LiveData<List<Exercise>> =
        exerciseRepo.getAllExercises().asLiveData()

    val weeklyPlans: LiveData<List<Plan>> =
        repo.getPlans(PlanType.WEEKLY).asLiveData()
    val dailyPlans: LiveData<List<Plan>> =
        repo.getPlans(PlanType.DAILY).asLiveData()

    private val _progress = MutableLiveData<WeekProgress?>()
    val progress: LiveData<WeekProgress?> = _progress

    private val _todayPlan = MutableLiveData<PlanWithExercises?>()
    val todayPlan: LiveData<PlanWithExercises?> = _todayPlan

    init {
        loadProgress()
    }

    fun loadProgress() {
        val state = storage.load()
        _progress.value = state
        state?.let { updateTodayPlan(it) }
    }

    private fun updateTodayPlan(state: WeekProgress) {
        val planId = if (state.day == 5 && state.modularPlanId != null && !state.modularRest) {
            state.modularPlanId
        } else {
            state.weeklyPlanId
        }
        viewModelScope.launch(Dispatchers.IO) {
            val plan = try {
                repo.getPlanWithExercises(planId)
            } catch (e: NoSuchElementException) {
                storage.clear()
                _progress.postValue(null)
                null
            }
            _todayPlan.postValue(plan)
        }
    }

    fun startWeek(newProgress: WeekProgress) {
        storage.save(newProgress)
        loadProgress()
    }

    fun finishDay() {
        val current = _progress.value ?: return
        val index = calculatePlanIndex(current)
        val planId = if (current.day == 5 && current.modularPlanId != null && !current.modularRest) {
            current.modularPlanId!!
        } else {
            current.weeklyPlanId
        }
        historyStore.add(WorkoutHistoryEntry(LocalDate.now(), planId, index))

        val next = current.copy(day = current.day + 1)
        if (next.day >= 7) {
            storage.clear()
            _progress.postValue(null)
            _todayPlan.postValue(null)
        } else {
            storage.save(next)
            _progress.postValue(next)
            updateTodayPlan(next)
        }
    }
    private fun calculatePlanIndex(state: WeekProgress): Int {
        var idx = state.day
        if (state.restDay in 0..6 && state.day > state.restDay) idx--
        if (state.day > 5) idx--
        return idx
    }

    fun getExerciseName(id: Long): String =
        exercises.value?.firstOrNull { it.id == id }?.name ?: "Exercise $id"

    fun getExerciseGroup(id: Long): String =
        exercises.value?.firstOrNull { it.id == id }?.muscleGroup?.display ?: ""

    fun loadPlan(planId: Long): LiveData<PlanWithExercises> {
        val result = MutableLiveData<PlanWithExercises>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repo.getPlanWithExercises(planId))
        }
        return result
    }}
