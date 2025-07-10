package com.example.mygymapp.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.mygymapp.model.AppTheme

class SettingsStorage private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _darkMode = MutableStateFlow(prefs.getBoolean("dark_mode", true))
    val darkMode: StateFlow<Boolean> = _darkMode

    private val _notifications = MutableStateFlow(prefs.getBoolean("notifications", true))
    val notifications: StateFlow<Boolean> = _notifications

    private val _userName = MutableStateFlow(prefs.getString("user_name", "User") ?: "User")
    val userName: StateFlow<String> = _userName

    private val _appTheme = MutableStateFlow(
        AppTheme.values()[prefs.getInt("app_theme", AppTheme.DarkForest.ordinal)]
    )
    val appTheme: StateFlow<AppTheme> = _appTheme

    fun setDarkMode(value: Boolean) {
        _darkMode.value = value
        prefs.edit().putBoolean("dark_mode", value).apply()
    }

    fun setNotifications(value: Boolean) {
        _notifications.value = value
        prefs.edit().putBoolean("notifications", value).apply()
    }

    fun setUserName(value: String) {
        _userName.value = value
        prefs.edit().putString("user_name", value).apply()
    }

    fun setAppTheme(theme: AppTheme) {
        _appTheme.value = theme
        prefs.edit().putInt("app_theme", theme.ordinal).apply()
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsStorage? = null

        fun getInstance(context: Context): SettingsStorage {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsStorage(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}