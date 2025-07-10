package com.example.mygymapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.theme.MountainTheme
import com.example.mygymapp.ui.theme.ParallaxHeader
import com.example.mygymapp.ui.theme.GlacierAccent

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MountainDemoScreen() {
    MountainTheme {
        val items = listOf(
            NavItem("home", Icons.Outlined.FitnessCenter),
            NavItem("profile", Icons.Outlined.Person)
        )
        var current by remember { mutableStateOf("home") }
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        Scaffold(
            topBar = {
                Column(Modifier.statusBarsPadding()) {
                    ParallaxHeader(scrollBehavior)
                    TabRow(
                        selectedTabIndex = items.indexOfFirst { it.route == current },
                        containerColor = MaterialTheme.colorScheme.background
                    ) {
                        items.forEachIndexed { index, item ->
                            Tab(
                                selected = item.route == current,
                                onClick = { current = item.route },
                                icon = { Icon(item.icon, contentDescription = null) }
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}, containerColor = GlacierAccent.copy(alpha = 0.8f)) {
                    Icon(Icons.Outlined.FitnessCenter, contentDescription = null)
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                AnimatedContent(
                    targetState = current,
                    transitionSpec = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300)) with
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(300))
                    }
                ) { screen ->
                    if (screen == "home") {
                        Column(Modifier.fillMaxSize().padding(16.dp)) {
                            Text("Workout overview")
                        }
                    } else {
                        Column(Modifier.fillMaxSize().padding(16.dp)) {
                            Text("Profile settings")
                        }
                    }
                }
            }
        }
    }
}

private data class NavItem(val route: String, val icon: ImageVector)
