package com.example.mygymapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.zIndex
import com.example.mygymapp.ui.components.BookmarkToggleIcon

@Composable
fun BookmarkMenu(
    isOpen: Boolean,
    onToggle: () -> Unit,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusPadding = remember {
        WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    }
    Column(
        modifier = modifier
            .padding(top = statusPadding)
            .offset(y = -statusPadding)
            .zIndex(2f)
    ) {
        BookmarkToggleIcon(
            isOpen = isOpen,
            onClick = onToggle
        )

        AnimatedVisibility(
            visible = isOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(Color(0xFFF2EDE3))
            ) {
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
                                },
                            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Serif)
                        )
                    }
                }
            }
        }
    }
}

