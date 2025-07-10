package com.example.mygymapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mygymapp.data.SettingsStorage
import com.example.mygymapp.model.AppTheme
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val settings = SettingsStorage.getInstance(application)

    val currentTheme: StateFlow<AppTheme> = settings.appTheme

    fun setTheme(theme: AppTheme) = settings.setAppTheme(theme)
}
