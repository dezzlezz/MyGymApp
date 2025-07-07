package com.example.mygymapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.PlanRepository
import com.example.mygymapp.data.SettingsStorage
import com.example.mygymapp.data.WorkoutHistoryEntry
import com.example.mygymapp.data.WorkoutHistoryStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val settings = SettingsStorage.getInstance(application)
    private val historyStore = WorkoutHistoryStorage.getInstance(application)
    private val repo = PlanRepository(AppDatabase.getDatabase(application).planDao())

    val userName: StateFlow<String> = settings.userName
    val darkMode: StateFlow<Boolean> = settings.darkMode
    val notifications: StateFlow<Boolean> = settings.notifications

    private val _history = kotlinx.coroutines.flow.MutableStateFlow(historyStore.loadAll())
    val history: StateFlow<Map<LocalDate, WorkoutHistoryEntry>> = _history

    fun setUserName(name: String) = settings.setUserName(name)
    fun setDarkMode(enabled: Boolean) = settings.setDarkMode(enabled)
    fun setNotifications(enabled: Boolean) = settings.setNotifications(enabled)

    fun refreshHistory() {
        _history.value = historyStore.loadAll()
    }

    fun getEntryInfo(entry: WorkoutHistoryEntry, onResult: (String, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val plan = repo.getPlanWithExercises(entry.planId)
            val planName = plan.plan.name
            val dayName = plan.days.firstOrNull { it.dayIndex == entry.dayIndex }?.name
                ?: "Tag ${entry.dayIndex + 1}"
            launch(Dispatchers.Main) { onResult(planName, dayName) }
        }
    }

    val totalWorkouts: Int
        get() = history.value.size

    fun logout() {
        // For demo purposes we just clear progress
        com.example.mygymapp.data.WorkoutStorage(getApplication()).clear()
    }
}