package com.example.mygymapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mygymapp.data.PlanRepository

/**
 * Factory zum Erstellen von PlansViewModel mit PlanRepository-Dependency
 */
class PlansViewModelFactory(
    private val repository: PlanRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlansViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlansViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}