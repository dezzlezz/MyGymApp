package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mygymapp.viewmodel.ExerciseViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.mygymapp.ui.background.ForestBackgroundCanvas

@Composable
fun MainScreen() {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    var showExercises by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val pageWidth = with(LocalDensity.current) { maxWidth.toPx() }
        val offset = -(pagerState.currentPage + pagerState.currentPageOffsetFraction) * pageWidth * 0.2f

        ForestBackgroundCanvas(
            modifier = Modifier.fillMaxSize(),
            offsetX = offset,
            showFog = pagerState.currentPage == 1,
            showLightCone = pagerState.currentPage == 3
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.9f }
            ) {
                when (page) {
                    0 -> WorkoutScreen()
                    1 -> PlansScreen(rememberNavController())
                    2 -> ProgressScreen()
                    3 -> ProfileScreen(rememberNavController())
                }
            }
        }

        if (showExercises) {
            Box(Modifier.fillMaxSize()) {
                ExerciseListScreen(
                    viewModel = viewModel<com.example.mygymapp.viewmodel.ExerciseViewModel>(),
                    onAddExercise = {},
                    onEditExercise = {}
                )
                FloatingActionButton(
                    onClick = { showExercises = false },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
            }
        }

        FloatingActionButton(
            onClick = { showExercises = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null)
        }
    }
}
