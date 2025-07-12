package com.example.mygymapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.mygymapp.model.AppTheme
import com.example.mygymapp.ui.theme.DarkForestTheme
import com.example.mygymapp.ui.theme.MountainTheme
import com.example.mygymapp.ui.theme.BeachTheme





@Composable
fun MyGymAppThemeWrapper(theme: AppTheme, darkMode: Boolean, animationsEnabled: Boolean = true) {
    when (theme) {
        AppTheme.DarkForest -> DarkForestTheme(animationsEnabled, darkMode)
        AppTheme.Mountains -> MountainTheme(animationsEnabled, darkMode)
        AppTheme.Beach -> BeachTheme(animationsEnabled, darkMode)
    }
}
