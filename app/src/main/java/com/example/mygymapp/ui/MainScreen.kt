package com.example.mygymapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.mygymapp.ui.theme.MyGymAppThemeWrapper
import com.example.mygymapp.navigation.AppNavGraph
import com.example.mygymapp.viewmodel.ThemeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

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

@Composable
fun MainScreen(themeViewModel: ThemeViewModel) {
    val theme by themeViewModel.currentTheme.collectAsState()
    AppNavGraph(theme = theme)
}