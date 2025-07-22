package com.example.mygymapp.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BookmarkMenu(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    expandedHeight: Dp = 250.dp
) {
    val height by animateDpAsState(
        targetValue = if (isOpen) expandedHeight else 0.dp,
        animationSpec = spring(dampingRatio = 0.7f)
    )

    if (isOpen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        )
    }

    Box(
        modifier = modifier
            .offset(x = 16.dp, y = 72.dp)
            .width(220.dp)
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2EDE3))
    ) {
        if (height > 0.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                listOf(
                    "Today's Page",
                    "Table of Contents",
                    "Lines & Paragraphs",
                    "Chronicle",
                    "Impressum"
                ).forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                onItemSelected(label)
                                onDismiss()
                            },
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif)
                    )
                }
            }
        }
    }
}

