package com.example.mygymapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.model.UserPreferences
import com.example.mygymapp.data.Plan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreferencesViewModel(private val repo: PlanRepository) : ViewModel() {
    private val _prefs = MutableStateFlow(UserPreferences())
    val prefs: StateFlow<UserPreferences> = _prefs

    private val _suggestions = MutableStateFlow<List<Plan>>(emptyList())
    val suggestions: StateFlow<List<Plan>> = _suggestions

    fun update(p: UserPreferences) {
        _prefs.value = p
    }

    fun loadSuggestions(p: UserPreferences) {
        _prefs.value = p
        viewModelScope.launch(Dispatchers.IO) {
            _suggestions.value = repo.getSuggestions(p)
        }
    }
}

class PreferencesViewModelFactory(private val repo: PlanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreferencesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
