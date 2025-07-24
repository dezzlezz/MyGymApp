package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.mygymapp.ui.components.BookmarkMenu
import androidx.compose.foundation.layout.Column

@Composable
fun PageScaffold() {
    var currentPage by remember { mutableStateOf("entry") }
    var isMenuOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentPage) {
            "entry" -> EntryNavigation()
            "toc" -> TocPage()
            "archive" -> ArchivePage()
            "chronicle" -> ChroniclePage()
            "impressum" -> ImpressumPage()
        }

        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isMenuOpen = false }
            )
        }

        BookmarkMenu(
            isOpen = isMenuOpen,
            onToggle = { isMenuOpen = !isMenuOpen },
            onSelect = { label ->
                currentPage = when (label) {
                    "Today's Page" -> "entry"
                    "Table of Contents" -> "toc"
                    "Lines & Paragraphs" -> "archive"
                    "Chronicle" -> "chronicle"
                    else -> "impressum"
                }
                isMenuOpen = false
            },
            modifier = Modifier
                .align(Alignment.TopStart)
        )
    }
}
