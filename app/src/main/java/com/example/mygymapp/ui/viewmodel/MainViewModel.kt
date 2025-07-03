package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mygymapp.data.Repository
import com.example.mygymapp.model.*

class MainViewModel : ViewModel() {
    val exercises   = Repository.exercises
    val dailyPlans  = Repository.dailyPlans
    val weeklyPlans = Repository.weeklyPlans

    fun addExercise(ex: Exercise)     = Repository.addExercise(ex)
    fun addDailyPlan(plan: DailyPlan) = Repository.addDailyPlan(plan)
    fun addWeeklyPlan(plan: WeeklyPlan) = Repository.addWeeklyPlan(plan)
}
