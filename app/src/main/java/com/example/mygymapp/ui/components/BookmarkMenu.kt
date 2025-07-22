package com.example.mygymapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shadow
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun BookmarkMenu(
    currentPage: String,
    onPageSelected: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = { isVisible = !isVisible },
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp)
                .align(Alignment.TopStart)
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = "Open menu",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .padding(start = 16.dp, top = 64.dp)
                    .wrapContentSize()
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val items = listOf(
                        MenuItem("entry", "Today's Page", Icons.Filled.Note),
                        MenuItem("toc", "Table of Contents", Icons.Filled.List),
                        MenuItem("archive", "Lines & Paragraphs", Icons.Filled.Archive),
                        MenuItem("chronicle", "Chronicle", Icons.Filled.CalendarMonth),
                        MenuItem("impressum", "Impressum", Icons.Filled.MenuBook)
                    )

                    items.forEach { item ->
                        RowItem(label = item.label, icon = item.icon) {
                            onPageSelected(item.key)
                            isVisible = false
                        }
                    }
                }
            }
        }
    }
}

private data class MenuItem(val key: String, val label: String, val icon: ImageVector)

@Composable
private fun RowItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            }
            .scaleOnTap(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}

fun Modifier.scaleOnTap(): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    this.pointerInput(Unit) {
        detectTapGestures(
            onPress = {
                pressed = true
                tryAwaitRelease()
                pressed = false
            }
        )
    }.graphicsLayer {
        scaleX = if (pressed) 0.96f else 1f
        scaleY = if (pressed) 0.96f else 1f
    }
}
