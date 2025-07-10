package com.example.mygymapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.mygymapp.model.AppTheme
import com.example.mygymapp.ui.theme.DarkForestTheme
import com.example.mygymapp.ui.theme.MountainTheme
import com.example.mygymapp.ui.theme.BeachTheme





@Composable
fun MyGymAppThemeWrapper(theme: AppTheme, animationsEnabled: Boolean = true) {
    when (theme) {
        AppTheme.DarkForest -> DarkForestTheme(animationsEnabled)
        AppTheme.Mountains -> MountainTheme(animationsEnabled)
        AppTheme.Beach -> BeachTheme(animationsEnabled)
    }
}
