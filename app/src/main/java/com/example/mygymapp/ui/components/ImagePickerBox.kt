package com.example.mygymapp.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mygymapp.ui.pages.GaeguRegular
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

/**
 * A poetic image picker box that allows the user to select and remove an illustration.
 * If no image is selected, shows an "＋ Add Illustration" button.
 */
@Composable
fun ImagePickerBox(
    imageUri: Uri?,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = stringResource(R.string.add_illustration),
    font: FontFamily = GaeguRegular,
    imageSize: Dp = 200.dp
) {
    if (imageUri != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(imageSize),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Illustration",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "✖",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { onRemoveImage() },
                fontFamily = font,
                fontSize = 20.sp,
                color = Color.Red
            )
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = onPickImage,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5D4037).copy(alpha = 0.2f))
            ) {
                Text(placeholderText, fontFamily = font)
            }
        }
    }
}

