package com.example.mygymapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.example.mygymapp.model.AppTheme
import com.example.mygymapp.ui.theme.DarkForestTheme
import com.example.mygymapp.ui.theme.MountainTheme
import com.example.mygymapp.ui.theme.BeachTheme





@Composable
fun MyGymAppThemeWrapper(theme: AppTheme) {
    when (theme) {
        AppTheme.DarkForest -> DarkForestTheme()
        AppTheme.Mountains -> MountainTheme()
        AppTheme.Beach -> BeachTheme()
    }
}
