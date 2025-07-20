package com.example.mygymapp.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalIndication
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun DifficultyRating(
    rating: Int,
    modifier: Modifier = Modifier,
    onRatingChanged: ((Int) -> Unit)? = null,
    max: Int = 5
) {
    Row(modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        repeat(max) { i ->
            val icon = Icons.Outlined.Eco
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .indication(interactionSource, LocalIndication.current)
                    .clickable(
                        enabled = onRatingChanged != null,
                        interactionSource = interactionSource,
                        indication = null
                    ) { onRatingChanged?.invoke(i + 1) }
            )
        }
    }
}