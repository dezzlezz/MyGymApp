package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mygymapp.ui.background.ForestBackgroundCanvas

@Composable
fun MainScreen(navController: NavHostController) {
    val pagerState = rememberPagerState()
    val pageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction

    Box(modifier = Modifier.fillMaxSize()) {
        ForestBackgroundCanvas(
            currentPageOffset = pageOffset,
            showFog = pagerState.currentPage == 1,
            showLightCone = pagerState.currentPage == 3,
            modifier = Modifier.fillMaxSize()
        )

        HorizontalPager(
            pageCount = 4,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> WorkoutScreen()
                1 -> PlansScreen(navController)
                2 -> ProgressScreen()
                3 -> ProfileScreen(navController)
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("exercises") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFF4B6E4D),
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = "Zu Übungen")
        }
    }
}
