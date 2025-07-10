package com.example.mygymapp.ui.screens

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.theme.BeachTheme

@Composable
fun BeachDemoScreen() {
    BeachTheme {
        val items = listOf(
            BeachNavItem("home", "Home", Icons.Outlined.FitnessCenter),
            BeachNavItem("profile", "Profile", Icons.Outlined.Person)
        )
        var current by remember { mutableStateOf("home") }
        Scaffold(
            bottomBar = {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = current == item.route,
                            onClick = { current = item.route },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                if (current == "home") {
                    Text("Beach workout overview", modifier = Modifier.padding(16.dp))
                } else {
                    Text("Beach profile settings", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

private data class BeachNavItem(val route: String, val label: String, val icon: ImageVector)
