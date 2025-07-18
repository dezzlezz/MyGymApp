package com.example.mygymapp.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsStorage private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _notifications = MutableStateFlow(prefs.getBoolean("notifications", true))
    val notifications: StateFlow<Boolean> = _notifications

    private val _userName = MutableStateFlow(prefs.getString("user_name", "User") ?: "User")
    val userName: StateFlow<String> = _userName


    fun setNotifications(value: Boolean) {
        _notifications.value = value
        prefs.edit().putBoolean("notifications", value).apply()
    }

    fun setUserName(value: String) {
        _userName.value = value
        prefs.edit().putString("user_name", value).apply()
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