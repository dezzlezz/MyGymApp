package com.example.mygymapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.mygymapp.ui.theme.MyGymColorScheme.DarkColors
import com.example.mygymapp.ui.theme.MyGymColorScheme.LightColors
import com.example.mygymapp.ui.theme.MyGymShapes
import com.example.mygymapp.ui.theme.MyGymTypography

@Composable
fun MyGymAppTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MyGymTypography,
        shapes = MyGymShapes,
        content = content
    )
}
