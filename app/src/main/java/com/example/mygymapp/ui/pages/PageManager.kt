package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.example.mygymapp.ui.background.ForestBackgroundCanvas

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageManager() {
    val pages = listOf<@Composable () -> Unit>(
        { StartPage() },
        { PlanPage() },
        { WorkoutPage() },
        { ProfilePage() }
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    Box(Modifier.fillMaxSize()) {
        ForestBackgroundCanvas(currentPageOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            pages[pageIndex]()
        }
    }
}
