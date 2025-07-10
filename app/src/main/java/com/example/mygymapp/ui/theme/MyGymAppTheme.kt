package com.example.mygymapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.animation.Crossfade
import com.example.mygymapp.model.AppTheme
import com.example.mygymapp.ui.theme.DarkForestTheme
import com.example.mygymapp.ui.theme.MountainTheme
import com.example.mygymapp.ui.theme.BeachTheme




private val LightColorScheme = lightColorScheme(
    primary = NatureGreen,
    onPrimary = DeepBlack,
    secondary = KaizenBeige,
    onSecondary = DeepBlack,
    background = OnDark,
    onBackground = DeepBlack,
    surface = OnDark,
    onSurface = DeepBlack,
    error = ErrorRed,
    onError = DeepBlack
)

@Composable
fun MyGymAppThemeWrapper(theme: AppTheme, content: @Composable () -> Unit) {
    when (theme) {
        AppTheme.DarkForest -> DarkForestTheme(content)
        AppTheme.Mountains -> MountainTheme(content)
        AppTheme.Beach -> BeachTheme(content)
        else -> {
            Crossfade(targetState = LightColorScheme) { scheme ->
                MaterialTheme(colorScheme = scheme, typography = Typography(), content = content)
            }
        }
    }
}
