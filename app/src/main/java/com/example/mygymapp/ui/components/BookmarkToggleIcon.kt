package com.example.mygymapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@Composable
fun BookmarkToggleIcon(
    isOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(56.dp)
            .height(112.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.TopCenter
    ) {
        val rotation by animateFloatAsState(if (isOpen) 90f else 0f)

        Box(
            modifier = Modifier
                .width(56.dp)
                .height(96.dp)
                .clip(RoundedCornerShape(bottomEnd = 20.dp))
                .background(Color(0xFF3F4E3A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isOpen) Icons.Default.Close else Icons.Default.MenuBook,
                contentDescription = "Bookmark Menu",
                tint = Color.White,
                modifier = Modifier.rotate(rotation)
            )
        }

        // Cord extending from the bookmark
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-16).dp)
                .width(2.dp)
                .height(14.dp)
                .background(Color(0xFFE6D3B1))
        )

        // Tassel drawn with multiple strands
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = 16.dp, height = 10.dp)
        ) {
            val strandWidth = size.width / 5f
            for (i in 0..4) {
                val x = strandWidth * i + strandWidth / 2f
                drawLine(
                    color = Color(0xFFE6D3B1),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = strandWidth * 0.7f
                )
            }
        }
    }
}

