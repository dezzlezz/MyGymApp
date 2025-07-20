package com.example.mygymapp.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

@Composable
fun StarRating(rating: Int, max: Int = 5) {
    Row {
        repeat(max) { i ->
            Icon(
                Icons.Outlined.Star,
                contentDescription = null,
                tint = if (i < rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
