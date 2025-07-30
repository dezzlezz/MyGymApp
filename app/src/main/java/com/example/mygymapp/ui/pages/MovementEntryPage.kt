@file:OptIn(ExperimentalLayoutApi::class)
package com.example.mygymapp.ui.pages

import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.R
import com.example.mygymapp.ui.theme.handwritingText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.border
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing



val GaeguRegular = FontFamily(Font(R.font.gaegu_regular))
val GaeguBold = FontFamily(Font(R.font.gaegu_bold))
val GaeguLight = FontFamily(Font(R.font.gaegu_light))



@Composable
fun MovementEntryPage(
    navController: NavController,
    editId: Long? = null,
    userCategories: List<String> = com.example.mygymapp.model.CustomCategories.list
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var note by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }
    var showSavedOverlay by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Neue Validierungsstates
    var showNameError by remember { mutableStateOf(false) }
    var showCategoryError by remember { mutableStateOf(false) }
    var showMuscleGroupError by remember { mutableStateOf(false) }
    val hasLoadedEditData = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).exerciseDao() }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    val categoryOptions = listOf(
        "\uD83C\uDF3F Warmup",
        "\uD83D\uDCAA Strength",
        "\uD83E\uDDCD Bodyform",
        "\uD83C\uDF00 Flexibility",
        "\uD83D\uDD25 Cardio",
        "\uD83C\uDF2C\uFE0F Recovery"
    ) + userCategories
    val muscleOptions = listOf("Chest", "Legs", "Back", "Arms", "Core", "Shoulders")

    val inkColor = Color(0xFF1B1B1B)
    val highlightColor = Color(0xFF5D4037).copy(alpha = 0.2f)

    LaunchedEffect(editId) {
        if (editId != null && !hasLoadedEditData.value) {
            val ex = withContext(Dispatchers.IO) { dao.getById(editId) }
            ex?.let {
                name = it.name
                val target = it.customCategory ?: it.category.display
                category = categoryOptions.find { opt -> opt.endsWith(target) } ?: target
                muscleGroup = it.muscleGroup.display
                rating = it.likeability
                imageUri = it.imageUri?.let { uri -> Uri.parse(uri) }
                note = it.description
                isFavorite = it.isFavorite
            }
            hasLoadedEditData.value = true
        }
    }

    // Tinten-Animation
    val animatedInkAlpha by animateFloatAsState(
        targetValue = if (note.isNotBlank()) 1f else 0.6f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing), label = "inkAlpha"
    )

    // Schreibgeräusch (Placeholder für tatsächliche Implementation)
    LaunchedEffect(note) {
        if (note.isNotBlank()) {
            // Hier kann z.B. ein Sound abgespielt werden
            // z. B. mit MediaPlayer oder Accompanist Sound
            // playPenScratchSound()
        }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFEDE5D0)).padding(WindowInsets.systemBars.asPaddingValues())) {

        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clickable { isFavorite = !isFavorite }
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color(0xFF1B1B1B).copy(alpha = 0.05f), shape = RoundedCornerShape(bottomStart = 6.dp))
            ) {}
        }




            Image(
                painter = painterResource(id = if (isFavorite) R.drawable.background_parchment_eselohr else R.drawable.background_parchment),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Column(
                modifier = Modifier.fillMaxSize().imePadding().padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Describe this movement",
                    style = TextStyle(fontFamily = GaeguBold, fontSize = 28.sp)
                )
                if (editId != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "\u270F\uFE0F Editing movement",
                        style = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp)
                    )
                }
                Spacer(Modifier.height(8.dp))


                // 🔴 Name mit Validierungsrahmen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            if (showNameError) Color.Red else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (it.isNotBlank()) showNameError = false
                        },
                        textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 24.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .drawBehind {
                                val strokeWidth = 2f
                                val y = size.height - strokeWidth / 2
                                drawLine(
                                    color = inkColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = strokeWidth
                                )
                            },
                        cursorBrush = SolidColor(inkColor)
                    )
                }


                Spacer(Modifier.height(16.dp))
                Text(
                    "What kind of movement is this?",
                    style = TextStyle(fontFamily = GaeguLight, fontSize = 20.sp)
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            if (showCategoryError) Color.Red else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryOptions.forEach { label ->
                        Text(
                            text = label,
                            modifier = Modifier
                                .background(
                                    if (category == label) highlightColor else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    category = label
                                    showCategoryError = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    "Which area does it speak to?",
                    style = TextStyle(fontFamily = GaeguLight, fontSize = 18.sp)
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            2.dp,
                            if (showMuscleGroupError) Color.Red else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(4.dp),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    muscleOptions.forEach { label ->
                        Text(
                            text = label,
                            modifier = Modifier
                                .background(
                                    if (muscleGroup == label) highlightColor else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    muscleGroup = label
                                    showMuscleGroupError = false
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            style = TextStyle(fontFamily = GaeguRegular, fontSize = 18.sp)
                        )
                    }
                }


                Spacer(Modifier.height(24.dp))
                Text(
                    "How challenging is it for you?",
                    style = TextStyle(fontFamily = GaeguLight, fontSize = 20.sp)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            painter = painterResource(
                                if (index < rating) R.drawable.tintenfleck else R.drawable.tintenfleck_outline
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = index + 1 },
                            tint = inkColor
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    "Illustration (optional)",
                    style = TextStyle(fontFamily = GaeguLight, fontSize = 18.sp)
                )
                Spacer(Modifier.height(8.dp))
                if (imageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            "✖",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clickable { imageUri = null },
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontFamily = GaeguBold,
                                color = Color.Red
                            )
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                launcher.launch("image/*")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = highlightColor)
                        ) { // Content lambda for Button starts here
                            Text("📷 Add Illustration", fontFamily = GaeguRegular)
                        } // Content lambda for Button ends here, THEN close the Button call
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    "Your notes on this movement",
                    style = TextStyle(fontFamily = GaeguLight, fontSize = 20.sp)
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .drawBehind {
                            val lineSpacing = 32.dp.toPx()
                            val paddingStart = 8.dp.toPx()
                            val paddingEnd = size.width - 8.dp.toPx()
                            val lines = (size.height / lineSpacing).toInt()
                            repeat(lines) { i ->
                                val wave = if (i % 2 == 0) 0f else 1.5f
                                val y = (i + 1) * lineSpacing + wave
                                drawLine(
                                    color = inkColor.copy(alpha = animatedInkAlpha),
                                    start = Offset(paddingStart, y),
                                    end = Offset(paddingEnd, y),
                                    strokeWidth = 1.6f
                                )
                            }
                        }
                        .padding(horizontal = 8.dp)
                ) {
                    BasicTextField(
                        value = note,
                        onValueChange = { note = it },
                        textStyle = handwritingText.copy(fontSize = 18.sp, lineHeight = 32.sp),
                        modifier = Modifier.fillMaxSize(),
                        cursorBrush = SolidColor(inkColor)
                    )
                }

                Spacer(Modifier.height(32.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp).padding(bottom = 16.dp)
                        .clickable {
                            val nameMissing = name.isBlank()
                            val categoryMissing = category.isBlank()
                            val muscleMissing = muscleGroup.isBlank()

                            showNameError = nameMissing
                            showCategoryError = categoryMissing
                            showMuscleGroupError = muscleMissing

                            if (nameMissing || categoryMissing || muscleMissing) {
                                showError = true
                                return@clickable
                            }

                            showSavedOverlay = true
                            showError = false
                            scope.launch {
                                delay(1000)
                                val catName = category.substringAfter(" ")
                                val categoryEnum = ExerciseCategory.values().find { it.display == catName }
                                val userCat = if (categoryEnum == null) category else null
                                val finalEnum = categoryEnum ?: ExerciseCategory.Calisthenics
                                val muscleGroupEnum = MuscleGroup.values().find { it.display == muscleGroup } ?: MuscleGroup.Core

                                val exercise = Exercise(
                                    id = editId ?: 0L,
                                    name = name,
                                    description = note,
                                    category = finalEnum,
                                    customCategory = userCat,
                                    likeability = rating,
                                    muscleGroup = muscleGroupEnum,
                                    muscle = muscleGroupEnum.display,
                                    imageUri = imageUri?.toString(),
                                    isFavorite = isFavorite
                                )

                                withContext(Dispatchers.IO) {
                                    if (editId != null) dao.update(exercise) else dao.insert(exercise)
                                }
                                navController.popBackStack()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.waxseal),
                        contentDescription = "Save Movement",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        if (editId != null) "Save" else "Create",
                        style = TextStyle(
                            fontFamily = GaeguBold,
                            fontSize = 16.sp,
                            shadow = Shadow(Color.Black, Offset(1f, 1f), 2f)
                        ),
                        color = Color.White
                    )
                }

                if (showError) {
                    Text(
                        "Please fill out name, category and muscle group",
                        color = Color.Red,
                        fontFamily = GaeguRegular,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (showSavedOverlay) {
                Box(
                    Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "A new line has been written...",
                        color = Color.White,
                        style = TextStyle(fontFamily = GaeguBold, fontSize = 20.sp)
                    )
                }
            }
        }
    }
