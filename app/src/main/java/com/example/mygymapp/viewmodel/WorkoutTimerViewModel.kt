package com.example.mygymapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class WorkoutTimerViewModel : ViewModel() {
    private var timerJob: Job? = null
    private val _time = mutableStateOf(0L)
    val time: State<Long> = _time

    val isRunning = mutableStateOf(false)

    fun start() {
        if (isRunning.value) return
        isRunning.value = true
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _time.value++
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        isRunning.value = false
    }

    fun reset() {
        stop()
        _time.value = 0L
    }
}
