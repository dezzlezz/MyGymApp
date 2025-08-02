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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.text.font.FontFamily
import com.example.mygymapp.R
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ParagraphEditorPageSwipe(
    initial: Paragraph?,
    onSave: (Paragraph) -> Unit,
    onCancel: () -> Unit,
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

    val dayNames =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val pagerState = rememberPagerState(pageCount = { 7 })
    val coroutineScope = rememberCoroutineScope()
    var showSavedOverlay by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                ) {
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.Start),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Cancel", fontFamily = FontFamily.Serif, fontSize = 14.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "✒ Compose your weekly paragraph",
                    fontFamily = GaeguBold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", fontFamily = GaeguRegular, color = Color.Black) },
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = GaeguRegular,
                        color = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))

                val placeholder = "What connects this week?"
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

                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions ->
                        if (tabPositions.isNotEmpty() && pagerState.currentPage < tabPositions.size) {
                            TabRowDefaults.Indicator(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                    .height(2.dp),
                                color = Color.Black
                            )
                        }
                    },
                ) {
                    dayNames.forEachIndexed { index, day ->
                        val isSelected = pagerState.currentPage == index
                        Tab(
                            selected = isSelected,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        ) {
                            Surface(
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                                color = if (isSelected) Color(0xFFF0E0C0) else Color(0xFFFFF8E1),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    day,
                                    fontFamily = GaeguBold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) { page ->
                    var showAll by remember(page) { mutableStateOf(false) }
                    val sheetState = rememberModalBottomSheetState()

                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                            val selected = selectedLines[page]
                            if (selected != null) {
                                PoeticLineCard(
                                    line = selected,
                                    isSelected = true
                                )
                            } else {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                                    shape = RoundedCornerShape(12.dp),
                                ) {
                                    Text(
                                        "No page selected for ${dayNames[page]}",
                                        modifier = Modifier.padding(16.dp),
                                        fontFamily = GaeguRegular,
                                        color = Color.Black.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = { showAll = true },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("📖 Browse lines", fontFamily = GaeguRegular, color = Color.Black)
                        }
                    }

                    if (showAll) {
                        var query by remember { mutableStateOf("") }
                        var selectedCategory by remember { mutableStateOf<String?>(null) }
                        var categoryExpanded by remember { mutableStateOf(false) }
                        val categories = lines.map { it.category }.distinct().sorted()
                        val filteredLines = lines.filter { line ->
                            line.title.contains(query, ignoreCase = true) &&
                                    (selectedCategory == null || line.category == selectedCategory)
                        }

                        ModalBottomSheet(
                            onDismissRequest = { showAll = false },
                            sheetState = sheetState,
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
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
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = categoryExpanded
                                                )
                                            },
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
                                Spacer(Modifier.height(8.dp))
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
                }

                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (selectedLines.any { it == null }) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Select a line for each day")
                        }
                    } else {
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
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0E0C0))
            ) {
                Text("📜 Save this paragraph", fontFamily = GaeguRegular, color = Color.Black)
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
                        "A new chapter has been written...",
                        color = Color.White,
                        style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp)
                    )
                }
            }
        }
    }
}
