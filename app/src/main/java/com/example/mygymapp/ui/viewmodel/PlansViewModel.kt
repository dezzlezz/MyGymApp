package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.PlanWithExercises
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel für die Anzeige und Verwaltung von Plänen
 */
class PlansViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val weeklyPlans: StateFlow<List<PlanWithExercises>> =
        repository.getWeeklyPlans()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val dailyPlans: StateFlow<List<PlanWithExercises>> =
        repository.getDailyPlans()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Löscht einen Plan asynchron.
     */
    fun deletePlan(plan: Plan) {
        viewModelScope.launch {
            repository.deletePlan(plan)
        }
    }

    /**
     * Fügt einen neuen Plan hinzu.
     */
    fun insertPlan(plan: Plan) {
        viewModelScope.launch {
            repository.insertPlan(plan)
        }
    }

    /**
     * Speichert die Cross-Refs für einen Plan.
     */
    fun insertCrossRefs(refs: List<PlanExerciseCrossRef>) {
        viewModelScope.launch {
            repository.insertCrossRefs(refs)
        }
    }
}