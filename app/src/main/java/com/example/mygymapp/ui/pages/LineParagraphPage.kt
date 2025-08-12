package com.example.mygymapp.ui.pages

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import android.net.Uri
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.store.JournalStore
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.GaeguButton
import com.example.mygymapp.R

@Composable
fun LineParagraphPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    startTab: Int = 0
) {
    var selectedTab by remember { mutableStateOf(startTab) }
    val tabs = listOf(
        stringResource(R.string.lines_tab),
        stringResource(R.string.paragraphs_tab)
    )
    val lines = JournalStore.allLines
    val paragraphs = JournalStore.allParagraphs
    var editingLine by remember { mutableStateOf<Line?>(null) }
    var showLineEditor by remember { mutableStateOf(false) }

    // When coming back from movement creation, reopen the line editor
    val savedState = navController.currentBackStackEntry?.savedStateHandle
    if (savedState?.get<Boolean>("resume_line_editor") == true) {
        showLineEditor = true
        savedState.remove<Boolean>("resume_line_editor")
    }

    PaperBackground(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(vertical = 12.dp),
                containerColor = Color.Transparent,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .padding(horizontal = 16.dp),
                        color = Color(0xFF556B2F)
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        modifier = Modifier.padding(vertical = 12.dp),
                        text = { Text(title, fontFamily = GaeguRegular, color = Color.Black) }
                    )
                }
            }

            Crossfade(targetState = selectedTab, label = "tab") { tab ->
                when (tab) {
                    0 -> LinesPage(
                        lines = lines,
                        onEdit = {
                            editingLine = it
                            showLineEditor = true
                        },
                        onArchive = { lines.remove(it) },
                        onAdd = {
                            editingLine = null
                            showLineEditor = true
                        },
                        onManageExercises = { navController.navigate("exercise_management") }
                    )
                    else -> ParagraphsPage(
                        paragraphs = paragraphs,
                        archived = emptyList(),
                        planned = emptyList(),
                        onEdit = { paragraph ->
                            navController.navigate("paragraph_editor?id=${paragraph.id}")
                        },
                        onPlan = {},
                        onSaveTemplate = {},
                        onArchive = { paragraphs.remove(it) },
                        onAdd = { navController.navigate("paragraph_editor") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            GaeguButton(
                text = if (selectedTab == 0) stringResource(R.string.compose_new_line_button) else stringResource(R.string.add_paragraph_button),
                onClick = {
                    if (selectedTab == 0) {
                        editingLine = null
                        showLineEditor = true
                    } else {
                        navController.navigate("paragraph_editor")
                    }
                },
                textColor = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showLineEditor) {
        LineEditorPage(
            initial = editingLine,
            onSave = { line ->
                val index = lines.indexOfFirst { it.id == line.id }
                if (index >= 0) lines[index] = line else lines.add(line)
                showLineEditor = false
            },
            onCancel = { showLineEditor = false },
            onCreateExercise = { name ->
                val encoded = Uri.encode(name)
                navController.navigate("movement_editor?name=$encoded")
            }
        )
    }
}
