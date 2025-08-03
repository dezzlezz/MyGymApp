package com.example.mygymapp.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.viewmodel.ParagraphViewModel

@Composable
fun ParagraphEditorScreen(
    navController: NavController,
    editId: Long? = null,
    paragraphViewModel: ParagraphViewModel = viewModel()
) {
    val paragraphs by paragraphViewModel.paragraphs.collectAsState()
    val initial = paragraphs.find { it.id == editId }

    ParagraphEditorPageSwipe(
        initial = initial,
        onSave = { paragraph ->
            if (initial != null) {
                paragraphViewModel.editParagraph(paragraph)
            } else {
                paragraphViewModel.addParagraph(paragraph)
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