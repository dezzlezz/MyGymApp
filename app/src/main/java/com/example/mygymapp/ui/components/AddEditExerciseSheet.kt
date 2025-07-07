package com.example.mygymapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
    onSave: (String, String, String, Int, Uri?) -> Unit,
    onCancel: () -> Unit,
    categories: List<String>,
    muscleGroups: List<String>
) {
    var name by remember { mutableStateOf(initialName) }
    var category by remember { mutableStateOf(initialCategory) }
    var muscleGroup by remember { mutableStateOf(initialMuscleGroup) }
    var rating by remember { mutableStateOf(initialRating) }
    var imageUri by remember { mutableStateOf(initialImageUri) }

    val context = LocalContext.current

    // Image picker
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) imageUri = uri
    }

    ModalBottomSheet(
        onDismissRequest = onCancel,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(if (initialName.isEmpty()) "Add Exercise" else "Edit Exercise", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Exercise image",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                }
                OutlinedButton(onClick = { launcher.launch("image/*") }) {
                    Icon(Icons.Default.Photo, contentDescription = "Select Image")
                    Spacer(Modifier.width(4.dp))
                    Text("Select Image")
                }
            }
            Spacer(Modifier.height(12.dp))
            FilterChips(
                items = categories,
                selected = category.takeIf { it.isNotEmpty() },
                onSelected = { category = it ?: "" }
            )
            Spacer(Modifier.height(12.dp))
            FilterChips(
                items = muscleGroups,
                selected = muscleGroup.takeIf { it.isNotEmpty() },
                onSelected = { muscleGroup = it ?: "" }
            )
            Spacer(Modifier.height(12.dp))
            Text("Likeability")
            StarRating(rating = rating, onRatingChanged = { rating = it })
            Spacer(Modifier.height(24.dp))
            Row {
                Button(
                    onClick = {
                        if (name.isNotBlank() && category.isNotBlank() && muscleGroup.isNotBlank()) {
                            onSave(name, category, muscleGroup, rating, imageUri)
                        }
                    },
                    enabled = name.isNotBlank() && category.isNotBlank() && muscleGroup.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
                Spacer(Modifier.width(12.dp))
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
            }
        }
    }
}
