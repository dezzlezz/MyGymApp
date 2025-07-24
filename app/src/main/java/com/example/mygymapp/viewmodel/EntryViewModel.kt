package com.example.mygymapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygymapp.data.EntryNumberStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    private val store = EntryNumberStore.getInstance(application)

    private val _entryNumber = MutableStateFlow(1)
    val entryNumber: StateFlow<Int> = _entryNumber.asStateFlow()

    init {
        viewModelScope.launch {
            _entryNumber.value = store.load()
        }
    }

    fun finishedEntry() {
        viewModelScope.launch {
            val next = _entryNumber.value + 1
            store.save(next)
            _entryNumber.value = next
        }
    }
}
