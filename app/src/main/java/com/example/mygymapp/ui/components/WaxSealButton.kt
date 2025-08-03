package com.example.mygymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Offset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.R
import com.example.mygymapp.ui.pages.GaeguBold

/**
 * A poetic action button using a wax seal illustration.
 * Displays centered label text over a wax image (e.g. for Save/Create/Finish actions).
 * Designed for consistent use across all major actions in the app.
 */
@Composable
fun WaxSealButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.waxseal,
    font: FontFamily = GaeguBold,
    textColor: Color = Color.White,
    textSize: TextUnit = 16.sp,
    shadowColor: Color = Color.Black,
    shadowOffset: Offset = Offset(1f, 1f),
    sealSize: Dp = 100.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(sealSize)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = label,
            modifier = Modifier.size(sealSize),
            contentScale = ContentScale.Fit
        )
        androidx.compose.material3.Text(
            text = label,
            style = TextStyle(
                fontFamily = font,
                fontSize = textSize,
                shadow = Shadow(shadowColor, shadowOffset, blurRadius = 2f),
                color = textColor
            )
        )
    }
}

