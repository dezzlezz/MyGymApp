package com.example.mygymapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.mygymapp.ui.MainScreen
import com.example.mygymapp.ui.theme.MyGymAppThemeWrapper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val theme by themeVm.currentTheme.collectAsState()
            MyGymAppThemeWrapper(theme) {
                MainScreen(themeVm)
            }
        }
    }
}
