package com.example.mygymapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.mygymapp.model.PlanFormState

class AddDailyPlanViewModel : ViewModel() {
    private val _form = MutableStateFlow(PlanFormState())
    val form: StateFlow<PlanFormState> = _form.asStateFlow()

    fun updateName(value: String) = _form.update { it.copy(name = value) }
    fun updateDesc(value: String) = _form.update { it.copy(description = value) }
    fun updateDifficulty(value: Int) = _form.update { it.copy(difficulty = value) }
    fun updateDuration(value: Int) = _form.update { it.copy(duration = value) }
    fun toggleEquipment(equipment: String) = _form.update {
        val list = it.equipment.toMutableList()
        if (list.contains(equipment)) list.remove(equipment) else list.add(equipment)
        it.copy(equipment = list)
    }
}
