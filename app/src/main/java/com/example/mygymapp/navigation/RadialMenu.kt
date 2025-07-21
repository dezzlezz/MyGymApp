package com.example.mygymapp.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.compose.runtime.setValue

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
    visible: Boolean,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rad = Math.toRadians(angleDeg.toDouble())
    val radiusPx = with(LocalDensity.current) { radius.toPx() }
    val targetX = cos(rad) * radiusPx
    val targetY = sin(rad) * radiusPx

    val x by animateFloatAsState(if (visible) targetX.toFloat() else 0f, label = "x")
    val y by animateFloatAsState(if (visible) targetY.toFloat() else 0f, label = "y")
    val scale by animateFloatAsState(if (visible) 1f else 0f, label = "scale")
    val alpha by animateFloatAsState(if (visible) 1f else 0f, label = "alpha")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
            .scale(scale)
            .alpha(alpha)
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
    FloatingActionButton(
        onClick = onToggle,
        modifier = modifier,
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
    val step = if (items.size > 1) 180f / (items.size - 1) else 0f
    val start = -90f
    val radius = 96.dp

    Box(modifier = modifier.fillMaxSize()) {
        if (open) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { open = false }
            )
        }
        items.forEachIndexed { index, item ->
            RadialMenuItem(
                item = item,
                angleDeg = start + step * index,
                radius = radius,
                visible = open,
                onClick = { route ->
                    navController.navigate(route) { launchSingleTop = true }
                    open = false
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
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
