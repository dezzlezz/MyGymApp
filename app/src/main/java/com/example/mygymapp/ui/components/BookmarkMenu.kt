package com.example.mygymapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import com.example.mygymapp.ui.components.BookmarkToggleIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.dp
import com.example.mygymapp.R
import androidx.compose.ui.layout.ContentScale

@Composable
fun BookmarkMenu(
    isOpen: Boolean,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 🎨 Muster-Hintergrund als Image
    val pattern = painterResource(id = R.drawable.darkforest)

    val transition = updateTransition(targetState = isOpen, label = "bookmarkTransition")

    val menuAlpha by transition.animateFloat(
        label = "alpha",
        transitionSpec = { tween(durationMillis = 400, easing = FastOutSlowInEasing) }
    ) { if (it) 1f else 0f }

    Column(
        modifier = modifier
            .wrapContentWidth()
            .padding(start = 30.dp, top = 0.dp)
    ) {
        // 📜 Menü-Inhalt animiert
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(animationSpec = tween(200)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(800)) + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .shadow(10.dp, shape = RoundedCornerShape(bottomEnd = 16.dp))
                    .clip(RoundedCornerShape(bottomEnd = 16.dp))
                    .background(Color(0xFF3F4E3A))
                    .widthIn(min = 200.dp, max = 200.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 50.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
                )
                {
                    listOf(
                        "Today's Page",
                        "Table of Contents",
                        "Lines & Paragraphs",
                        "Chronicle",
                        "Impressum"
                    ).forEach { label ->
                        Text(
                            text = label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(label) }
                                .padding(vertical = 8.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = FontFamily.Serif,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 📌 Lesezeichen (mit Textur)
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(96.dp)
                .shadow(6.dp, shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp))
                .clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp))
                .background(Color(0xFF3F4E3A))
                .clickable { onToggle() }
        ) {
            // Textur drauflegen
            Image(
                painter = pattern,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize(),
                contentScale = ContentScale.Crop
            )

            Icon(
                imageVector = if (isOpen) Icons.Default.Close else Icons.Default.MenuBook,
                contentDescription = "Toggle Menu",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            )
        }
    }
}