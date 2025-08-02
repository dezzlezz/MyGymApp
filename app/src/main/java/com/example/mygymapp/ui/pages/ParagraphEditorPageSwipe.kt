package com.example.mygymapp.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.components.PoeticLineCard
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.pages.GaeguRegular
import com.example.mygymapp.viewmodel.LineViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.Alignment

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParagraphEditorPageSwipe(
    initial: Paragraph?,
    onSave: (Paragraph) -> Unit,
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var mood by remember { mutableStateOf(initial?.mood ?: "") }
    var tagsText by remember { mutableStateOf(initial?.tags?.joinToString(", ") ?: "") }
    var note by remember { mutableStateOf(initial?.note ?: "") }

    val lineViewModel: LineViewModel = viewModel()
    val lines by lineViewModel.lines.collectAsState()
    val selectedLines = remember {
        mutableStateListOf<Line?>().apply { repeat(7) { add(null) } }
    }

    LaunchedEffect(lines) {
        if (initial != null && selectedLines.all { it == null }) {
            initial.lineTitles.forEachIndexed { idx, title ->
                selectedLines[idx] = lines.find { it.title == title }
            }
        }
    }

    val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val moods = listOf("calm", "alert", "connected", "alive", "empty", "carried", "searching")
    var moodExpanded by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 7 })
    val coroutineScope = rememberCoroutineScope()

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", fontFamily = GaeguRegular, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = moodExpanded,
                onExpandedChange = { moodExpanded = !moodExpanded }
            ) {
                OutlinedTextField(
                    value = mood,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mood", fontFamily = GaeguRegular, color = Color.Black) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(moodExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black)
                )
                ExposedDropdownMenu(
                    expanded = moodExpanded,
                    onDismissRequest = { moodExpanded = false }
                ) {
                    moods.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontFamily = GaeguRegular, color = Color.Black) },
                            onClick = {
                                mood = option
                                moodExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = tagsText,
                onValueChange = { tagsText = it },
                label = { Text("Tags", fontFamily = GaeguRegular, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note", fontFamily = GaeguRegular, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            TabRow(selectedTabIndex = pagerState.currentPage) {
                dayNames.forEachIndexed { index, day ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(day.take(3), fontFamily = GaeguRegular, color = Color.Black) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                LazyColumn {
                    items(lines) { line ->
                        val isSelected = selectedLines[page]?.id == line.id
                        PoeticLineCard(
                            line = line,
                            isSelected = isSelected,
                            onClick = { selectedLines[page] = line }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val tags = tagsText.split(',').map { it.trim() }.filter { it.isNotBlank() }
                    val lineTitles = selectedLines.map { it?.title ?: "" }
                    val paragraph = Paragraph(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        mood = mood,
                        tags = tags,
                        lineTitles = lineTitles,
                        note = note
                    )
                    onSave(paragraph)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save", fontFamily = GaeguRegular, color = Color.Black)
            }
        }
    }
}

