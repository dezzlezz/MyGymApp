// Datei: app/src/main/java/com/example/mygymapp/ui/viewmodel/DailyPlansViewModel.kt
package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.DailyPlan
import com.example.mygymapp.data.DailyPlanRepository
import com.example.mygymapp.data.DailyPlanWithExercises
import kotlinx.coroutines.launch

class DailyPlansViewModel(
    private val repo: DailyPlanRepository
) : ViewModel() {

    // LiveData<List<DailyPlanWithExercises>>
    val dailyPlans: LiveData<List<DailyPlanWithExercises>> =
        repo.getAll().asLiveData()

    fun addPlan(name: String, description: String) {
        viewModelScope.launch {
            val plan = DailyPlan(name = name, description = description)
            repo.insert(plan)
        }
    }

    fun deletePlan(planId: String) {
        viewModelScope.launch {
            repo.deleteById(planId)
        }
    }
}

class DailyPlansViewModelFactory(
    private val repo: DailyPlanRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyPlansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyPlansViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
