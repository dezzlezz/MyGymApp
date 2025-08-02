package com.example.mygymapp.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.drawBehind
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.model.Line
import com.example.mygymapp.model.Paragraph
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.components.PoeticLineCard
import com.example.mygymapp.ui.pages.GaeguBold
import com.example.mygymapp.ui.pages.GaeguRegular
import com.example.mygymapp.viewmodel.LineViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParagraphEditorPageSwipe(
    initial: Paragraph?,
    onSave: (Paragraph) -> Unit,
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
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
    val pagerState = rememberPagerState(pageCount = { 7 })
    val coroutineScope = rememberCoroutineScope()
    var showSavedOverlay by remember { mutableStateOf(false) }

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", fontFamily = GaeguRegular, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontFamily = GaeguRegular, color = Color.Black),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))

            val placeholder = remember {
                listOf("What connects this week?", "Where did it lead you?").random()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.background_parchment),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .drawBehind {
                            val lineSpacing = 32.dp.toPx()
                            val paddingStart = 8.dp.toPx()
                            val paddingEnd = size.width - 8.dp.toPx()
                            val lines = (size.height / lineSpacing).toInt()
                            repeat(lines) { i ->
                                val wave = if (i % 2 == 0) 0f else 1.5f
                                val y = (i + 1) * lineSpacing + wave
                                drawLine(
                                    color = Color.Black.copy(alpha = 0.15f),
                                    start = Offset(paddingStart, y),
                                    end = Offset(paddingEnd, y),
                                    strokeWidth = 1.2f
                                )
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    BasicTextField(
                        value = note,
                        onValueChange = { note = it },
                        textStyle = TextStyle(
                            fontFamily = GaeguRegular,
                            fontSize = 18.sp,
                            lineHeight = 32.sp,
                            color = Color.Black
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopStart
                            ) {
                                if (note.isBlank()) {
                                    Text(
                                        placeholder,
                                        fontFamily = GaeguRegular,
                                        fontSize = 18.sp,
                                        color = Color.Black.copy(alpha = 0.4f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            TabRow(selectedTabIndex = pagerState.currentPage) {
                dayNames.forEachIndexed { index, day ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(day.take(3), fontFamily = GaeguRegular, color = Color.Black) },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { page ->
                var query by remember(page) { mutableStateOf("") }
                var selectedCategory by remember(page) { mutableStateOf<String?>(null) }
                var categoryExpanded by remember(page) { mutableStateOf(false) }
                var showAll by remember(page) { mutableStateOf(false) }
                val sheetState = rememberModalBottomSheetState()

                val categories = lines.map { it.category }.distinct().sorted()
                val filteredLines = lines.filter { line ->
                    line.title.contains(query, ignoreCase = true) &&
                        (selectedCategory == null || line.category == selectedCategory)
                }
                val randomLines = remember(filteredLines) { filteredLines.shuffled().take(3) }

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Search lines") },
                        )
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded },
                        ) {
                            OutlinedTextField(
                                value = selectedCategory ?: "All",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier.menuAnchor(),
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All") },
                                    onClick = {
                                        selectedCategory = null
                                        categoryExpanded = false
                                    },
                                )
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            selectedCategory = cat
                                            categoryExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                    ) {
                        items(randomLines) { line ->
                            val isSelected = selectedLines[page]?.id == line.id
                            PoeticLineCard(
                                line = line,
                                isSelected = isSelected,
                                onClick = { selectedLines[page] = line },
                            )
                        }
                    }

                    TextButton(
                        onClick = { showAll = true },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Show all lines")
                    }
                }

                if (showAll) {
                    ModalBottomSheet(
                        onDismissRequest = { showAll = false },
                        sheetState = sheetState,
                    ) {
                        LazyColumn {
                            items(filteredLines) { line ->
                                val isSelected = selectedLines[page]?.id == line.id
                                PoeticLineCard(
                                    line = line,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedLines[page] = line
                                        showAll = false
                                    },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    val lineTitles = selectedLines.map { it?.title ?: "" }
                    val paragraph = Paragraph(
                        id = initial?.id ?: System.currentTimeMillis(),
                        title = title,
                        lineTitles = lineTitles,
                        note = note,
                    )
                    showSavedOverlay = true
                    coroutineScope.launch {
                        delay(1000)
                        onSave(paragraph)
                    }
                },
                modifier = Modifier.align(Alignment.End),
            ) {
                Text("Save", fontFamily = GaeguRegular, color = Color.Black)
            }
        }

        AnimatedVisibility(
            visible = showSavedOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Eine Woche wurde geplant.",
                    color = Color.White,
                    style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp)
                )
            }
        }
    }
}

