// Datei: app/src/main/java/com/example/mygymapp/ui/viewmodel/WeeklyPlansViewModel.kt
package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.WeeklyPlan
import com.example.mygymapp.data.WeeklyPlanRepository
import com.example.mygymapp.data.WeeklyPlanWithExercises
import kotlinx.coroutines.launch

class WeeklyPlansViewModel(
    private val repo: WeeklyPlanRepository
) : ViewModel() {

    // LiveData<List<WeeklyPlanWithExercises>>
    val weeklyPlans: LiveData<List<WeeklyPlanWithExercises>> =
        repo.getAll().asLiveData()

    fun addPlan(dayOfWeek: Int, name: String) {
        viewModelScope.launch {
            val plan = WeeklyPlan(dayOfWeek = dayOfWeek, name = name)
            repo.insert(plan)
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            repo.deleteById(planId)
        }
    }
}

class WeeklyPlansViewModelFactory(
    private val repo: WeeklyPlanRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeeklyPlansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeeklyPlansViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
