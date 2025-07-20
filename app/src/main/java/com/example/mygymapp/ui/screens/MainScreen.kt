package com.example.mygymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.google.accompanist.pager.HorizontalPagerIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import com.example.mygymapp.R

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(navController: NavHostController) {
    // Accompanist PagerState (initial page = 0)
    val pagerState = rememberPagerState()
    val haptics = LocalHapticFeedback.current

    val haptics = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Use Accompanist HorizontalPager with count
        HorizontalPager(
            count = 4,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { it },
            beyondBoundsPageCount = 0
        ) { page ->
            when (page) {
                0 -> WorkoutScreen()
                1 -> PlansScreen(navController)
                2 -> ProgressScreen()
                3 -> ProfileScreen(navController)
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            activeColor = MaterialTheme.colorScheme.primary
        )

        key(pagerState.currentPage) {
            ExtendedFloatingActionButton(
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    navController.navigate("exercises")
                },
                text = { Text(stringResource(id = R.string.add_plan)) },
                icon = { Icon(Icons.Outlined.FitnessCenter, contentDescription = null) },
                expanded = pagerState.currentPage == 1,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        }
    }
}
