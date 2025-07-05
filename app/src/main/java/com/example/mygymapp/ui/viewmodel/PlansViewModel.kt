package com.example.mygymapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlansViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)

    private val dailyRepo = DailyPlanRepository(
        db.dailyPlanDao(),
        db.dailyPlanExerciseCrossRefDao()
    )
    private val weeklyRepo = WeeklyPlanRepository(
        db.weeklyPlanDao(),
        db.weeklyPlanExerciseCrossRefDao()
    )

    // LiveData-Streams
    val dailyPlansWithExercises: LiveData<List<DailyPlanWithExercises>> =
        dailyRepo.getAllPlansWithExercises().asLiveData()
    val weeklyPlansWithExercises: LiveData<List<WeeklyPlanWithExercises>> =
        weeklyRepo.getAllPlansWithExercises().asLiveData()

    // Daily plan Operationen
    fun insertDailyPlan(plan: DailyPlan) = viewModelScope.launch(Dispatchers.IO) {
        dailyRepo.insertDailyPlan(plan)
    }
    fun addExerciseToDailyPlan(planId: String, exerciseId: Long, reps: Int, sets: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            dailyRepo.addExerciseToPlan(planId, exerciseId, reps, sets)
        }
    fun removeExerciseFromDailyPlan(planId: String, exerciseId: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            dailyRepo.removeExerciseFromPlan(planId, exerciseId)
        }
    fun deleteDailyPlanById(planId: String) = viewModelScope.launch(Dispatchers.IO) {
        dailyRepo.deleteDailyPlanById(planId)
    }

    // Beispiel: Plan mit mehreren Exercises + reps/sets anlegen
    fun insertDailyPlanWithDetails(
        planId: String,
        name: String,
        description: String,
        exerciseIds: List<Long>,
        reps: List<Int>,
        sets: List<Int>
    ) = viewModelScope.launch(Dispatchers.IO) {
        dailyRepo.insertDailyPlanWithDetails(planId, name, description, exerciseIds, reps, sets)
    }

    // Weekly plan Operationen (analog)
    fun insertWeeklyPlan(plan: WeeklyPlan) = viewModelScope.launch(Dispatchers.IO) {
        weeklyRepo.insertWeeklyPlan(plan)
    }
    fun addExerciseToWeeklyPlan(planId: String, exerciseId: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            weeklyRepo.addExerciseToPlan(planId, exerciseId)
        }
    fun removeExerciseFromWeeklyPlan(planId: String, exerciseId: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            weeklyRepo.removeExerciseFromPlan(planId, exerciseId)
        }
    fun deleteWeeklyPlanById(planId: String) = viewModelScope.launch(Dispatchers.IO) {
        weeklyRepo.deleteWeeklyPlanById(planId)
    }
}
