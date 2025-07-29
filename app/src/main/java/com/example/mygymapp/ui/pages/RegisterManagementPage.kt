package com.example.mygymapp.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygymapp.R

@Composable
fun RegisterManagementPage() {
    var newName by rememberSaveable { mutableStateOf("") }
    val registers = rememberSaveable { mutableStateListOf<String>() }
    val inkColor = Color(0xFF1B1B1B)
    val highlight = Color(0xFF5D4037).copy(alpha = 0.2f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDE5D0))
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Image(
            painter = painterResource(R.drawable.background_parchment),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Your Registers",
                style = MaterialTheme.typography.headlineSmall.copy(fontFamily = GaeguBold)
            )
            Spacer(Modifier.height(16.dp))

            Box(Modifier.fillMaxWidth()) {
                BasicTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    textStyle = TextStyle(fontFamily = GaeguRegular, fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .drawBehind {
                            val strokeWidth = 2f
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                color = inkColor,
                                start = androidx.compose.ui.geometry.Offset(0f, y),
                                end = androidx.compose.ui.geometry.Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        },
                    cursorBrush = SolidColor(inkColor),
                    decorationBox = { inner ->
                        if (newName.isEmpty()) {
                            Text("Add new register", fontFamily = GaeguRegular, color = Color.Gray)
                        }
                        inner()
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (newName.isNotBlank()) {
                        registers.add(newName)
                        newName = ""
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F4E3A))
            ) {
                Text("Add", fontFamily = GaeguBold, color = Color.White)
            }

            Spacer(Modifier.height(24.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(registers) { index, item ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(highlight, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = GaeguRegular)
                            )
                            Text(
                                text = "✏️",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable {
                                        newName = item
                                        registers.removeAt(index)
                                    },
                                fontFamily = GaeguRegular
                            )
                            Text(
                                text = "🗑️",
                                modifier = Modifier.clickable { registers.removeAt(index) },
                                fontFamily = GaeguRegular
                            )
                        }
                    }
                }
            }
        }
    }
}
