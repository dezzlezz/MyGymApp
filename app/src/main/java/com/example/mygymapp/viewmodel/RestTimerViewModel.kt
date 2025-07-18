package com.example.mygymapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RestTimerViewModel : ViewModel() {
    private var job: Job? = null

    private val _remaining = mutableStateOf(0)
    val remaining: State<Int> = _remaining
    val isActive = mutableStateOf(false)

    fun start(duration: Int = 45) {
        job?.cancel()
        _remaining.value = duration
        isActive.value = true

        job = viewModelScope.launch {
            while (_remaining.value > 0) {
                delay(1000)
                _remaining.value--
            }
            isActive.value = false
        }
    }

    fun stop() {
        job?.cancel()
        isActive.value = false
        _remaining.value = 0
    }
}
