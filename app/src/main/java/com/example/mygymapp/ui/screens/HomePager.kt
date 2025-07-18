package com.example.mygymapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.example.mygymapp.ui.theme.*
import androidx.navigation.NavController
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun HomePager(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    Box(Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val color = when (page) {
                0 -> MossGreen
                1 -> TwilightBlue
                2 -> RiverBlue
                else -> SunriseOrange
            }
            Box(Modifier.fillMaxSize().background(color)) {
                when (page) {
                    0 -> WorkoutScreen()
                    1 -> PlansScreen(navController)
                    2 -> ProgressScreen()
                    else -> ProfileScreen(navController)
                }
            }
        }
        FlowingRiver(pagerState)
    }
}

@Composable
private fun FlowingRiver(state: PagerState) {
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val totalW = w * state.pageCount
        val offset = -(state.currentPage + state.currentPageOffsetFraction) * w
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(offset, h * 0.7f)
            var x = 0f
            while (x <= totalW) {
                val y = h * (0.7f + 0.05f * kotlin.math.sin((x / totalW) * 4f * kotlin.math.PI).toFloat())
                lineTo(offset + x, y)
                x += w / 20f
            }
        }
        drawPath(path, color = SeaFoam, style = androidx.compose.ui.graphics.drawscope.Stroke(width = h * 0.05f))
    }
}
