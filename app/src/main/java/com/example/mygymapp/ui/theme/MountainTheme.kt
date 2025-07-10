package com.example.mygymapp.ui.theme

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.mygymapp.R

private val MountainColorScheme = lightColorScheme(
    primary = MountainBlue,
    onPrimary = SnowWhite,
    secondary = GlacierAccent,
    onSecondary = SnowWhite,
    background = SnowWhite,
    onBackground = MountainBlue,
    surface = SnowWhite,
    onSurface = MountainBlue
)

private val MountainShapes = Shapes(
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun MountainTheme(content: @Composable () -> Unit) {
    Crossfade(targetState = MountainColorScheme) { colors ->
        MaterialTheme(colorScheme = colors, shapes = MountainShapes) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MistGray, SnowWhite)
                        )
                    )
            ) { content() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParallaxHeader(scrollBehavior: TopAppBarScrollBehavior, modifier: Modifier = Modifier) {
    val height = 200.dp
    val offset = lerp(0.dp, height / 2, scrollBehavior.state.overlappedFraction)
    val density = LocalDensity.current
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer { translationY = -with(density) { offset.toPx() } }
    ) {
        Image(
            painterResource(R.drawable.ic_mountain),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // simple transparent overlay hinting at snow/wind
        Box(Modifier.matchParentSize().background(SnowWhite.copy(alpha = 0.1f)))
    }
}
