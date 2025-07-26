// Hinweis: Diese Version enthält folgende Erweiterungen:
// - automatische Navigation nach dem Speichern
// - einfache PR-Erkennung (Keywords: "PR", "personal record")
// - bessere Lesbarkeit durch angepasste Schriftfarben

package com.example.mygymapp.ui.pages

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.R
import com.example.mygymapp.ui.theme.handwritingText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditorScreen(
    initialName: String = "",
    initialCategory: String = "",
    initialMuscleGroup: String = "",
    initialRating: Int = 3,
    initialImageUri: Uri? = null,
    onSave: (String, String, String, Int, Uri?, String) -> Unit,
    onCancel: () -> Unit,
    onNavigateToEntry: () -> Unit // NEU: automatische Navigation nach dem Speichern
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var muscleGroup by remember { mutableStateOf(initialMuscleGroup) }
    var rating by remember { mutableStateOf(initialRating) }
    var imageUri by remember { mutableStateOf(initialImageUri) }
    var note by remember { mutableStateOf("") }
    var showSavedOverlay by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val categoryOptions = listOf("Warmup", "Strength", "Flexibility", "Cardio", "Recovery")
    val muscleOptions = listOf("Chest", "Legs", "Back", "Arms", "Core", "Shoulders")

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    BackHandler(onBack = onCancel)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.hintergrundstandard),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.95f
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (initialName.isNotBlank()) "Refine This Movement" else "Describe a New Movement",
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF2E1F0F)
                            )
                            Text(
                                text = "Every movement tells a story — what’s the essence of this one?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Cursive,
                                    color = Color(0xFF4B3820)
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onCancel) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Cancel"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageUri != null) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        IconButton(
                            onClick = { imageUri = null },
                            modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Image",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else {
                    OutlinedButton(onClick = { launcher.launch("image/*") }) {
                        Icon(Icons.Outlined.Photo, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Capture its form", fontFamily = FontFamily.Serif, color = Color(0xFF3B2C1B))
                    }
                }

                StyledTextField(label = "Give it a name", value = name, onValueChange = { name = it })
                DropdownField("What kind of movement is this?", categoryOptions, category) { category = it }
                DropdownField("Where does it resonate?", muscleOptions, muscleGroup) { muscleGroup = it }

                Text("How much do you enjoy this movement?", fontFamily = FontFamily.Serif, color = Color(0xFF3A2B1B))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) { index ->
                        val selected = index < rating
                        Icon(
                            painter = painterResource(if (selected) R.drawable.tintenfleck else R.drawable.tintenfleck_outline),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { rating = index + 1 },
                            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }

                StyledTextField(
                    label = "What would you like to remember?",
                    value = note,
                    onValueChange = { note = it },
                    height = 120.dp,
                    isHandwriting = true
                )

                Button(
                    onClick = {
                        if (name.isNotBlank() && category.isNotBlank() && muscleGroup.isNotBlank()) {
                            val containsPR = listOf("pr", "personal record").any { note.contains(it, ignoreCase = true) || name.contains(it, ignoreCase = true) }
                            showSavedOverlay = true
                            scope.launch {
                                delay(1200)
                                onSave(name, category, muscleGroup, rating, imageUri, note + if (containsPR) " 🟤" else "")
                                delay(400)
                                onNavigateToEntry()
                            }
                        }
                    },
                    enabled = name.isNotBlank() && category.isNotBlank() && muscleGroup.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text("Write this line", fontFamily = FontFamily.Serif, color = Color(0xFF2C1A0E))
                }
            }
        }

        AnimatedVisibility(
            visible = showSavedOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A new line has been written...",
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    height: Dp = 56.dp,
    isHandwriting: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontFamily = FontFamily.Serif, color = MaterialTheme.colorScheme.onSurface) },
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        textStyle = if (isHandwriting) handwritingText else TextStyle(fontFamily = FontFamily.Serif, color = MaterialTheme.colorScheme.onSurface),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontFamily = FontFamily.Serif) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
