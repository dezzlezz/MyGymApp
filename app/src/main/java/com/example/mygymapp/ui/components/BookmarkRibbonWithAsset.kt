package com.example.mygymapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import androidx.compose.ui.graphics.Color

@Composable
fun BookmarkRibbonWithAsset(modifier: Modifier = Modifier) {
    val swing by rememberInfiniteTransition(label = "swing").animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "ribbonSwing"
    )

    Icon(
        painter = painterResource(id = R.drawable.quaste),
        contentDescription = "Bookmark Ribbon",
        tint = Color.Unspecified,
        modifier = modifier
            .size(width = 28.dp, height = 84.dp)
            .rotate(swing)
    )
}
