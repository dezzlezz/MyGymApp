package com.example.mygymapp.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DifficultyRating(
    rating: Int,
    modifier: Modifier = Modifier,
    onRatingChanged: ((Int) -> Unit)? = null,
    max: Int = 5
) {
    Row(modifier) {
        repeat(max) { i ->
            val icon = if (i < rating) Icons.Filled.Eco else Icons.Outlined.Eco
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .clickable(enabled = onRatingChanged != null) { onRatingChanged?.invoke(i + 1) }
            )
        }
    }
}