package com.example.mygymapp.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.model.ExerciseCategory
import com.example.mygymapp.model.MuscleGroup
import com.example.mygymapp.ui.components.PaperBackground
import com.example.mygymapp.ui.widgets.DifficultyRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditorScreen(
    initialName: String = "",
    initialCategory: String = "",
    initialMuscleGroup: String = "",
    initialRating: Int = 3,
    initialImageUri: Uri? = null,
    onSave: (String, String, String, Int, Uri?, String) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var muscleGroup by remember { mutableStateOf(initialMuscleGroup) }
    var rating by remember { mutableStateOf(initialRating) }
    var imageUri by remember { mutableStateOf(initialImageUri) }
    var note by remember { mutableStateOf("") }

    val categories = ExerciseCategory.values().map { it.display }
    val groups = MuscleGroup.values().map { it.display }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    PaperBackground(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Write a new movement",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif),
                color = MaterialTheme.colorScheme.onBackground
            )

            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            OutlinedButton(onClick = { launcher.launch("image/*") }) {
                Icon(Icons.Outlined.Photo, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Choose image", fontFamily = FontFamily.Serif)
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", fontFamily = FontFamily.Serif) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Category", fontFamily = FontFamily.Serif)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat, fontFamily = FontFamily.Serif) }
                    )
                }
            }

            Text("Muscle group", fontFamily = FontFamily.Serif)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groups.forEach { grp ->
                    FilterChip(
                        selected = muscleGroup == grp,
                        onClick = { muscleGroup = grp },
                        label = { Text(grp, fontFamily = FontFamily.Serif) }
                    )
                }
            }

            Text("How much do you enjoy this movement?", fontFamily = FontFamily.Serif)
            DifficultyRating(rating = rating, onRatingChanged = { rating = it })

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Give this movement a note", fontFamily = FontFamily.Serif) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Cursive, color = Color.DarkGray)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (name.isNotBlank() && category.isNotBlank() && muscleGroup.isNotBlank()) {
                            onSave(name, category, muscleGroup, rating, imageUri, note)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save", fontFamily = FontFamily.Serif)
                }
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}
