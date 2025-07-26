package com.example.mygymapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.ui.widgets.DifficultyRating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExerciseSheet(
    initialName: String = "",
    initialCategory: String = "",
    initialMuscleGroup: String = "",
    initialRating: Int = 3,
    initialImageUri: Uri? = null,
    onSave: (String, String, String, Int, Uri?, String) -> Unit,
    onCancel: () -> Unit,
    categories: List<String>,
    muscleGroups: List<String>
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var muscleGroup by remember { mutableStateOf(initialMuscleGroup) }
    var rating by remember { mutableStateOf(initialRating) }
    var imageUri by remember { mutableStateOf(initialImageUri) }
    var description by remember { mutableStateOf("") }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Write a new movement",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Serif)
            )

            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Exercise Image",
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            OutlinedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Outlined.Photo, contentDescription = "Select Image")
                Spacer(Modifier.width(6.dp))
                Text("Choose Image", fontFamily = FontFamily.Serif)
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name", fontFamily = FontFamily.Serif) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            FilterChips(
                items = categories,
                selected = category.takeIf { it.isNotEmpty() },
                onSelected = { category = it ?: "" }
            )

            FilterChips(
                items = muscleGroups,
                selected = muscleGroup.takeIf { it.isNotEmpty() },
                onSelected = { muscleGroup = it ?: "" }
            )

            Text("How much do you enjoy this exercise?", fontFamily = FontFamily.Serif)
            DifficultyRating(rating = rating, onRatingChanged = { rating = it })

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = {
                    Text(
                        "Give this movement a note",
                        fontFamily = FontFamily.Serif
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Cursive,
                    color = Color.DarkGray
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (name.isNotBlank() && category.isNotBlank() && muscleGroup.isNotBlank()) {
                            onSave(name, category, muscleGroup, rating, imageUri, description)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", fontFamily = FontFamily.Serif)
                }
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", fontFamily = FontFamily.Serif)
                }
            }
        }
    }
}
