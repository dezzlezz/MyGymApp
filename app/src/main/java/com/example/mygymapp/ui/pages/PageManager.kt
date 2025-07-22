package com.example.mygymapp.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
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
    val pagerState = rememberPagerState()
    Box(Modifier.fillMaxSize()) {
        ForestBackgroundCanvas(currentPageOffset = pagerState.currentPage + pagerState.currentPageOffset)
        HorizontalPager(
            pageCount = pages.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            pages[pageIndex]()
        }
    }
}
