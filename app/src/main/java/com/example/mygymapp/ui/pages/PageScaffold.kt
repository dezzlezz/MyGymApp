package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mygymapp.ui.components.BookmarkMenu

@Composable
fun PageScaffold() {
    var currentPage by remember { mutableStateOf("entry") }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentPage) {
            "entry" -> EntryPage()
            "toc" -> TocPage()
            "archive" -> ArchivePage()
            "chronicle" -> ChroniclePage()
            "impressum" -> ImpressumPage()
        }

        BookmarkMenu(currentPage = currentPage) {
            currentPage = it
        }
    }
}
