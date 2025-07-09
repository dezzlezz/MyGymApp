package com.example.mygymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.mygymapp.ui.MainScreen
import com.example.mygymapp.ui.theme.MyGymAppTheme
import com.example.mygymapp.data.SettingsStorage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = SettingsStorage.getInstance(this)
        setContent {
            val dark by settings.darkMode.collectAsState()
            MyGymAppTheme(darkTheme = dark) {
                MainScreen()
            }
        }
    }
}
