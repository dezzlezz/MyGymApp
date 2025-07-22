package com.example.mygymapp.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PageScaffold() {
    var currentPage by remember { mutableStateOf("entry") }
    var menuOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentPage) {
            "entry" -> EntryPage()
            "toc" -> TocPage()
            "archive" -> ArchivePage()
            "chronicle" -> ChroniclePage()
            "impressum" -> ImpressumPage()
        }

        IconButton(
            onClick = { menuOpen = true },
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.Outlined.MenuBook, contentDescription = "Menü")
        }

        if (menuOpen) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp),
                shape = CardDefaults.shape,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("\uD83D\uDCD6 Eintrag", modifier = Modifier.clickable {
                        currentPage = "entry"
                        menuOpen = false
                    })
                    Text("\uD83D\uDCD8 Inhaltsverzeichnis", modifier = Modifier.clickable {
                        currentPage = "toc"
                        menuOpen = false
                    })
                    Text("\uD83D\uDCC2 Archiv", modifier = Modifier.clickable {
                        currentPage = "archive"
                        menuOpen = false
                    })
                    Text("\uD83D\uDDFA\uFE0F Chronik", modifier = Modifier.clickable {
                        currentPage = "chronicle"
                        menuOpen = false
                    })
                    Text("\uD83D\uDCDD Impressum", modifier = Modifier.clickable {
                        currentPage = "impressum"
                        menuOpen = false
                    })
                }
            }
        }
    }
}
