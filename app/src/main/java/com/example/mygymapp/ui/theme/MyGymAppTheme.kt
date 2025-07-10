package com.example.mygymapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.animation.Crossfade
import androidx.compose.ui.graphics.Color
import com.example.mygymapp.model.AppTheme
import com.example.mygymapp.ui.theme.DarkForestTheme


private val MountainColors = lightColorScheme(
    primary = AccentGreen,
    onPrimary = DeepBlack,
    secondary = KaizenBeige,
    onSecondary = DeepBlack,
    background = OnDark,
    onBackground = DeepBlack,
    surface = OnDark,
    onSurface = DeepBlack
)

private val BeachColors = lightColorScheme(
    primary = Color(0xFFFF8A65),
    onPrimary = DeepBlack,
    secondary = Color(0xFFFFCC80),
    onSecondary = DeepBlack,
    background = Color(0xFFFFF8E1),
    onBackground = DeepBlack,
    surface = Color(0xFFFFF8E1),
    onSurface = DeepBlack
)

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
        else -> {
            val colors = when (theme) {
                AppTheme.Mountains -> MountainColors
                AppTheme.Beach -> BeachColors
                else -> MountainColors
            }
            Crossfade(targetState = colors) { scheme ->
                MaterialTheme(
                    colorScheme = scheme,
                    typography = Typography(),
                    content = content
                )
            }
        }
    }
}
