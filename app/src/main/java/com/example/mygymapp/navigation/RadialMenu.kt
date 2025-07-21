package com.example.mygymapp.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class RadialItem(
    val icon: ImageVector,
    val label: String,
    val destination: String
)

@Composable
private fun RadialMenuItem(
    item: RadialItem,
    angleDeg: Float,
    radius: Dp,
    index: Int,
    open: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rad = Math.toRadians(angleDeg.toDouble())
    val radiusPx = with(LocalDensity.current) { radius.toPx() }
    val targetOffset = Offset(cos(rad).toFloat() * radiusPx, sin(rad).toFloat() * radiusPx)

    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(open) {
        if (open) {
            launch { offset.animateTo(targetOffset, tween(300, delayMillis = index * 50, easing = FastOutSlowInEasing)) }
            launch { scale.animateTo(1f, tween(300, delayMillis = index * 50, easing = FastOutSlowInEasing)) }
            launch { alpha.animateTo(1f, tween(300, delayMillis = index * 50, easing = FastOutSlowInEasing)) }
        } else {
            launch { offset.animateTo(Offset.Zero, tween(200, easing = FastOutSlowInEasing)) }
            launch { scale.animateTo(0.7f, tween(200, easing = FastOutSlowInEasing)) }
            launch { alpha.animateTo(0f, tween(200, easing = FastOutSlowInEasing)) }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .scale(scale.value)
            .alpha(alpha.value)
    ) {
        FloatingActionButton(
            onClick = { onClick(item.destination) },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(item.icon, contentDescription = item.label)
        }
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun RadialMenuCenterButton(isOpen: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val scale by animateFloatAsState(if (isOpen) 1.2f else 1f, label = "centerScale")
    FloatingActionButton(
        onClick = onToggle,
        modifier = modifier.scale(scale),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(if (isOpen) 8.dp else 4.dp)
    ) {
        Icon(Icons.Outlined.Dashboard, contentDescription = null)
    }
}

@Composable
fun RadialMenu(
    navController: NavHostController,
    items: List<RadialItem>,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }
    val start = -135f
    val step = if (items.size > 1) 90f / (items.size - 1) else 0f
    val radius = 96.dp

    Box(modifier = modifier.fillMaxSize()) {
        if (open) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { open = false }
            )
        }
        items.forEachIndexed { index, item ->
            RadialMenuItem(
                item = item,
                angleDeg = start + step * index,
                radius = radius,
                index = index,
                open = open,
                onClick = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                    open = false
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .zIndex(1f)
            )
        }
        RadialMenuCenterButton(
            isOpen = open,
            onToggle = { open = !open },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
