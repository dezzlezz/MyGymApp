package com.example.mygymapp.ui.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun StarRating(rating: Int, max: Int = 5) {
    Row {
        repeat(max) { i ->
            if (i < rating) {
                Icon(Icons.Filled.Star, contentDescription = null)
            } else {
                Icon(Icons.Outlined.Star, contentDescription = null)
            }
        }
    }
}
