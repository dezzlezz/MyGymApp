// app/src/main/java/com/example/mygymapp/ui/viewmodel/PlansViewModel.kt
package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.mygymapp.data.Plan
import com.example.mygymapp.data.PlanExerciseCrossRef
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.model.PlanType
import kotlinx.coroutines.launch

class PlansViewModel(
    private val repo: PlanRepository
) : ViewModel() {

    private val _type = MutableLiveData(PlanType.DAILY)
    val type: LiveData<PlanType> = _type

    /** Alle Pläne (LiveData) passend zum aktuellen Typ */
    val plans: LiveData<List<Plan>> = _type.switchMap { t ->
        repo.getPlans(t).asLiveData()
    }

    /** Zwischen Tages- und Wochen-Modus umschalten */
    fun switchType(newType: PlanType) {
        _type.value = newType
    }

    /** Plan löschen */
    fun delete(plan: Plan) = viewModelScope.launch {
        repo.deletePlan(plan)
    }

    /** Tages-Plan mit CrossRefs speichern */
    fun saveDaily(
        plan: Plan,
        exercises: List<PlanExerciseCrossRef>
    ) = viewModelScope.launch {
        repo.saveDailyPlan(plan, exercises)
    }

    /** Wochen-Plan mit CrossRefs speichern */
    fun saveWeekly(
        plan: Plan,
        exercises: List<PlanExerciseCrossRef>
    ) = viewModelScope.launch {
        repo.saveWeeklyPlan(plan, exercises)
    }

    /** Lädt einen Tages-Plan (für Composable-Dialogs o.Ä.) */
    suspend fun loadDaily(id: Long) = repo.getDailyPlan(id)

    /** Lädt einen Wochen-Plan */
    suspend fun loadWeekly(id: Long) = repo.getWeeklyPlan(id)
}

/** Factory zum Erzeugen des ViewModels */
class PlansViewModelFactory(
    private val repo: PlanRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PlansViewModel(repo) as T
    }
}
