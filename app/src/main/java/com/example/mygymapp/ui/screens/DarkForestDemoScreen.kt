package com.example.mygymapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.theme.DarkForestSidebar
import com.example.mygymapp.ui.theme.DarkForestTheme
import com.example.mygymapp.ui.theme.DarkNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkForestDemoScreen() {
    DarkForestTheme {
        val items = listOf(
            DarkNavItem("home", "Home", Icons.Outlined.FitnessCenter),
            DarkNavItem("profile", "Profile", Icons.Outlined.Person)
        )
        var current by remember { mutableStateOf("home") }
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Row {
            DarkForestSidebar(items = items, current = current, onSelect = { current = it })
            Scaffold(
                modifier = Modifier.weight(1f),
                topBar = {
                    LargeTopAppBar(
                        title = { Text(current) },
                        scrollBehavior = scrollBehavior,
                        colors = TopAppBarDefaults.largeTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        )
                    )
                }
            ) { padding ->
                Box(modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()) {
                    AnimatedVisibility(visible = current == "home", enter = fadeIn(), exit = fadeOut()) {
                        Column(Modifier.padding(16.dp)) { Text("Workout overview") }
                    }
                    AnimatedVisibility(visible = current == "profile", enter = fadeIn(), exit = fadeOut()) {
                        Column(Modifier.padding(16.dp)) { Text("Profile settings") }
                    }
                }
            }
        }
    }
}

