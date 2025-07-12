@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.mygymapp.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity

@Composable
fun RainyForestScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0C0B))) {
        RainOverlay()
        Row {
            Sidebar()
            Column(modifier = Modifier.fillMaxSize()) {
                Header()
                Content()
            }
        }
    }
}

@Composable
private fun RainOverlay() {
    val transition = rememberInfiniteTransition(label = "rain")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "progress"
    )

    val density = LocalDensity.current
    val dropHeight = with(density) { 40.dp.toPx() }
    val dropWidth = with(density) { 1.dp.toPx() }

    Canvas(modifier = Modifier.fillMaxSize().blur(4.dp)) {
        val totalHeight = size.height + dropHeight
        val spacing = size.width / 50f
        for (i in 0 until 50) {
            val y = (progress * totalHeight + i * 20f) % totalHeight - dropHeight
            this.drawRect(
                color = Color(0xFF1F9D55).copy(alpha = 0.3f),
                topLeft = Offset(spacing * i, y),
                size = Size(dropWidth, dropHeight)
            )
        }
    }
}

@Composable
private fun Sidebar() {
    val iconColor = Color(0xFF1F9D55)
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(64.dp)
            .background(Color(0xFF121515))
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Chat, contentDescription = "Chat", tint = iconColor)
        }
        IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = iconColor)
        }
        IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = iconColor)
        }
    }
}

@Composable
private fun Header() {
    val accent = Color(0xFF1F9D55)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFF121515))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var query by remember { mutableStateOf("") }
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search...") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = accent,
                unfocusedIndicatorColor = accent,
                focusedTextColor = Color(0xFFE2E8E6),
                unfocusedTextColor = Color(0xFFE2E8E6)
            )
        )
        Spacer(Modifier.width(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionButton(Icons.Default.Notifications)
            ActionButton(Icons.Default.Person)
        }
    }
}

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    val accent = Color(0xFF1F9D55)
    OutlinedButton(
        onClick = {},
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        border = BorderStroke(1.dp, accent),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = accent)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun Content() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardItem(title = "Obere Karte", modifier = Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CardItem(title = "Linke Karte", modifier = Modifier.weight(1f))
            CardItem(title = "Rechte Karte", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun CardItem(title: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2E3B38))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 20.sp, color = Color(0xFFE2E8E6))
            Spacer(Modifier.height(4.dp))
            Text("Inhalt", fontSize = 16.sp, color = Color(0xFFE2E8E6))
        }
    }
}