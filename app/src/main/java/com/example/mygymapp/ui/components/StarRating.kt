package com.example.mygymapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.LocalIndication



@Composable
fun StarRating(rating: Int, onRatingChanged: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Outlined.Star else Icons.Outlined.StarBorder,
                contentDescription = "$i Star",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(32.dp)
                    .indication(interactionSource, LocalIndication.current)
                    .clickable(
                        onClick = { onRatingChanged(i) },
                        interactionSource = interactionSource,
                        indication = null
                    )
            )
        }
    }
}

