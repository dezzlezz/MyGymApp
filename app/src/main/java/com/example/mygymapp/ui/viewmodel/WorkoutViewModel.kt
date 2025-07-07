package com.example.mygymapp.ui.viewmodel

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = PlanRepository(AppDatabase.getDatabase(application).planDao())
    private val storage = WorkoutStorage(application)
    private val exerciseRepo = ExerciseRepository(AppDatabase.getDatabase(application).exerciseDao())

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
            val plan = repo.getPlanWithExercises(planId)
            _todayPlan.postValue(plan)
        }
    }

    fun startWeek(newProgress: WeekProgress) {
        storage.save(newProgress)
        loadProgress()
    }

    fun finishDay() {
        val current = _progress.value ?: return
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

    fun getExerciseName(id: Long): String =
        exerciseRepo.getExerciseById(id)?.name ?: "Exercise $id"
}