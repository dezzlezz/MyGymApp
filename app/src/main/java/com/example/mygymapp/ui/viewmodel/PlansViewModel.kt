package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.PlanWithExercises
import com.example.mygymapp.model.PlanType
import kotlinx.coroutines.launch

class PlansViewModel(
    private val repo: PlanRepository
) : ViewModel() {

    private val _type = MutableLiveData(PlanType.DAILY)
    val type: LiveData<PlanType> = _type

    val plans: LiveData<List<Plan>> = _type.switchMap { t ->
        repo.getPlans(t).asLiveData()
    }

    fun switchType(newType: PlanType) {
        _type.value = newType
    }

    fun delete(plan: Plan) = viewModelScope.launch {
        repo.deletePlan(plan)
    }

    fun save(plan: Plan, exercises: List<PlanExerciseCrossRef>) = viewModelScope.launch {
        repo.savePlan(plan, exercises)
    }

    fun load(planId: Long): LiveData<PlanWithExercises> {
        val result = MutableLiveData<PlanWithExercises>()
        viewModelScope.launch {
            result.value = repo.getPlanWithExercises(planId)
        }
        return result
    }
}

