package com.example.mygymapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.mygymapp.store.JournalStore

@Composable
fun ParagraphEditorScreen(
    navController: NavController,
    editId: Long? = null,
) {
    val paragraphs = JournalStore.allParagraphs
    val initial = paragraphs.find { it.id == editId }

    ParagraphEditorPageSwipe(
        initial = initial,
        onSave = { paragraph ->
            if (initial != null) {
                val index = paragraphs.indexOfFirst { it.id == paragraph.id }
                if (index >= 0) paragraphs[index] = paragraph
            } else {
                paragraphs.add(paragraph)
            }
            navController.navigate("line_paragraph?tab=1") {
                popUpTo("line_paragraph?tab=0") { inclusive = true }
            }
        },
        onCancel = {
            navController.navigate("line_paragraph?tab=1") {
                popUpTo("line_paragraph?tab=0") { inclusive = true }
            }
        }
    )
}