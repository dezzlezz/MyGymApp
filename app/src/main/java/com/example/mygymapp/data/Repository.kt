package com.example.mygymapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mygymapp.model.*

object Repository {
    private val _exercises = MutableLiveData<List<Exercise>>(emptyList())
    val exercises: LiveData<List<Exercise>> = _exercises

    private val _dailyPlans = MutableLiveData<List<DailyPlan>>(emptyList())
    val dailyPlans: LiveData<List<DailyPlan>> = _dailyPlans

    private val _weeklyPlans = MutableLiveData<List<WeeklyPlan>>(emptyList())
    val weeklyPlans: LiveData<List<WeeklyPlan>> = _weeklyPlans

    fun addExercise(ex: Exercise) {
        _exercises.value = _exercises.value!!.plus(ex)
    }
    fun addDailyPlan(plan: DailyPlan) {
        _dailyPlans.value = _dailyPlans.value!!.plus(plan)
    }
    fun addWeeklyPlan(plan: WeeklyPlan) {
        _weeklyPlans.value = _weeklyPlans.value!!.plus(plan)
    }
}
