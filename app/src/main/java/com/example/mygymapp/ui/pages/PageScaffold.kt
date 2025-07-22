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
import com.example.mygymapp.ui.components.BookmarkMenu
import com.example.mygymapp.ui.components.BookmarkToggleIcon

@Composable
fun PageScaffold() {
    var currentPage by remember { mutableStateOf("entry") }
    var isMenuOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentPage) {
            "entry" -> EntryPage()
            "toc" -> TocPage()
            "archive" -> ArchivePage()
            "chronicle" -> ChroniclePage()
            "impressum" -> ImpressumPage()
        }

        BookmarkToggleIcon(
            isOpen = isMenuOpen,
            onClick = { isMenuOpen = !isMenuOpen },
            modifier = Modifier.align(Alignment.TopStart)
        )

        BookmarkMenu(
            isOpen = isMenuOpen,
            onDismiss = { isMenuOpen = false },
            modifier = Modifier.align(Alignment.TopStart)
        ) { label ->
            currentPage = when (label) {
                "Today's Page" -> "entry"
                "Table of Contents" -> "toc"
                "Lines & Paragraphs" -> "archive"
                "Chronicle" -> "chronicle"
                else -> "impressum"
            }
        }
    }
}
