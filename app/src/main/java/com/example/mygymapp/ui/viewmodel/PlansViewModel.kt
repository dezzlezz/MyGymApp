package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.mygymapp.data.*
import kotlinx.coroutines.launch

class PlansViewModel(
    private val repository: PlanRepository
): ViewModel() {
    val weeklyPlans = repository.getWeeklyPlans().asLiveData()
    val dailyPlans = repository.getDailyPlans().asLiveData()

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    private val _showFavoritesOnly = MutableLiveData(false)
    val showFavoritesOnly: LiveData<Boolean> = _showFavoritesOnly

    fun setSearch(query: String) { _searchQuery.value = query }
    fun toggleFavorites() { _showFavoritesOnly.value = !(_showFavoritesOnly.value ?: false) }

    fun deletePlan(plan: Plan) = viewModelScope.launch { repository.deletePlan(plan) }

    fun savePlan(
        plan: Plan,
        refs: List<PlanExerciseCrossRef>
    ) = viewModelScope.launch {
        repository.createOrUpdatePlan(plan, refs)
    }
}

class PlansViewModelFactory(private val repository: PlanRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PlansViewModel(repository) as T
    }
}