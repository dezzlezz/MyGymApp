package com.example.mygymapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.navigation.NavController
import com.example.mygymapp.ui.background.*

private enum class HomeSection { WORKOUT, PLAN, PROGRESS, PROFILE }

@Composable
fun HomePager(navController: NavController) {
    val pages = HomeSection.entries
    val pagerState = rememberPagerState()
    Box(Modifier.fillMaxSize()) {
        ForestBackground(pagerState, pages.size)
        HorizontalPager(count = pages.size, state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            val page = pages[index]
            Box(Modifier.fillMaxSize()) {
                when (page) {
                    HomeSection.WORKOUT -> {
                        WorkoutScreen()
                        FallingLeavesOverlay()
                    }
                    HomeSection.PLAN -> {
                        PlansScreen(navController)
                        FogOverlay()
                    }
                    HomeSection.PROGRESS -> {
                        ProgressScreen()
                        AnimatedRiver(pagerState, pages.size)
                    }
                    HomeSection.PROFILE -> {
                        ProfileScreen(navController)
                        SunRaysOverlay()
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { navController.navigate("exercises") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.FitnessCenter, contentDescription = null)
        }
    }
}
