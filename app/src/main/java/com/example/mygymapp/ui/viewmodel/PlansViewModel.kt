package com.example.mygymapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Daily and Weekly plans with their exercises.
 * Instantiates repositories manually via AppDatabase.
 */
class PlansViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)

    private val dailyPlanDao = db.dailyPlanDao()
    private val weeklyPlanDao = db.weeklyPlanDao()
    private val dailyCrossRefDao = db.dailyPlanExerciseCrossRefDao()
    private val weeklyCrossRefDao = db.weeklyPlanExerciseCrossRefDao()

    private val dailyRepo = DailyPlanRepository(dailyPlanDao, dailyCrossRefDao)
    private val weeklyRepo = WeeklyPlanRepository(weeklyPlanDao, weeklyCrossRefDao)

    // LiveData streams of plans with their exercises
    val dailyPlansWithExercises: LiveData<List<DailyPlanWithExercises>> =
        dailyRepo.getAllPlansWithExercises().asLiveData()
    val weeklyPlansWithExercises: LiveData<List<WeeklyPlanWithExercises>> =
        weeklyRepo.getAllPlansWithExercises().asLiveData()

    // Daily plan operations
    fun insertDailyPlan(plan: DailyPlan) = viewModelScope.launch(Dispatchers.IO) {
        dailyRepo.insertDailyPlan(plan)
    }

    fun addExerciseToDailyPlan(planId: String, exerciseId: Long) = viewModelScope.launch(Dispatchers.IO) {
        dailyRepo.addExerciseToPlan(planId, exerciseId)
    }

    fun removeExerciseFromDailyPlan(planId: String, exerciseId: Long) = viewModelScope.launch(Dispatchers.IO) {
        dailyRepo.removeExerciseFromPlan(planId, exerciseId)
    }

    fun deleteDailyPlanById(planId: String) = viewModelScope.launch(Dispatchers.IO) {
        dailyRepo.deleteDailyPlanById(planId)
    }

    // Weekly plan operations
    fun insertWeeklyPlan(plan: WeeklyPlan) = viewModelScope.launch(Dispatchers.IO) {
        weeklyRepo.insertWeeklyPlan(plan)
    }

    fun addExerciseToWeeklyPlan(planId: String, exerciseId: Long) = viewModelScope.launch(Dispatchers.IO) {
        weeklyRepo.addExerciseToPlan(planId, exerciseId)
    }

    fun removeExerciseFromWeeklyPlan(planId: String, exerciseId: Long) = viewModelScope.launch(Dispatchers.IO) {
        weeklyRepo.removeExerciseFromPlan(planId, exerciseId)
    }

    fun deleteWeeklyPlanById(planId: String) = viewModelScope.launch(Dispatchers.IO) {
        weeklyRepo.deleteWeeklyPlanById(planId)
    }
}
